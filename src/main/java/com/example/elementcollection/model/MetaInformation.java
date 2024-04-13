package com.example.elementcollection.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class MetaInformation {
    @Id
    public String globalId;

    @ElementCollection
    @OrderColumn(name = "elementOrder", nullable = false)
    public List<String> profile;

    @OneToOne(optional = false)
    public MainEntity parentResource;
}
