package com.example.elementcollection;

import com.example.elementcollection.model.CompositeId;
import com.example.elementcollection.model.MainEntity;
import com.example.elementcollection.model.MetaInformation;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.UUID;

@SpringBootApplication
public class ElementCollectionApplication implements CommandLineRunner {
    @Autowired
    EntityManager entityManager;

    public static void main(String[] args) {
        SpringApplication.run(ElementCollectionApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Initial creation
        MainEntity mainEntity = new MainEntity();

        mainEntity.compositeId = new CompositeId();
        mainEntity.compositeId.uuid = UUID.randomUUID().toString();
        mainEntity.compositeId.version = "1";
        mainEntity.someStatus = false;

        mainEntity.metaInformation = new MetaInformation();
        mainEntity.metaInformation.parentResource = mainEntity;
        mainEntity.metaInformation.globalId = UUID.randomUUID().toString();
        mainEntity.metaInformation.profile = new ArrayList<>();
        mainEntity.metaInformation.profile.add("first profile");

        entityManager.persist(mainEntity);
        entityManager.flush();

        // Simulates remapping to JPA with changed boolean status
        // Creates duplicate but with another meta global id (it is autogenerated on mapping steps)
        // will fail with hibernate 6.0.2.Final but works with 6.4.4.Final
        MainEntity secondEntity = new MainEntity();

        secondEntity.compositeId = new CompositeId();
        secondEntity.compositeId.uuid = mainEntity.compositeId.uuid;
        secondEntity.compositeId.version = mainEntity.compositeId.version;
        secondEntity.someStatus = true;

        secondEntity.metaInformation = new MetaInformation();
        secondEntity.metaInformation.parentResource = secondEntity;
        secondEntity.metaInformation.globalId = UUID.randomUUID().toString();
        secondEntity.metaInformation.profile = new ArrayList<>(mainEntity.metaInformation.profile);

        // By some reason 6.0.2.Final do not generate "delete from meta_information_profile where meta_information_global_id=?"
        // to delete profiles of old meta before to delete meta by itself and replace it by the new one with new profile entries (but with same content)

        // 6.4.4.Final queries:
        // Hibernate: delete from meta_information_profile where meta_information_global_id=?
        // Hibernate: delete from meta_information where global_id=?
        // Hibernate: insert into meta_information (parent_resource_uuid,parent_resource_version,global_id) values (?,?,?)
        // Hibernate: update main_entity set some_status=? where uuid=? and version=?
        // Hibernate: insert into meta_information_profile (meta_information_global_id,element_order,profile) values (?,?,?)

        // 6.0.2.Final queries
        // *** NO PROFILE DELETION ***
        // Hibernate: delete from meta_information where global_id=?
        // *** FAIL ***
        // ERROR: update or delete on table "meta_information" violates foreign key constraint "fkrtx3sqqscclna5m0h762r2vqu" on table "meta_information_profile"
        // Detail: Key (global_id)=(40b82a9f-aa15-40af-9e34-20dc776c34f8) is still referenced from table "meta_information_profile".
        entityManager.merge(secondEntity);
        entityManager.flush();
    }
}
