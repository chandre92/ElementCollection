package com.example.elementcollection.model;

import jakarta.persistence.*;

@Entity
public class MainEntity {
    @EmbeddedId
    public CompositeId compositeId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "parentResource")
    public MetaInformation metaInformation;

    public boolean someStatus;
}
