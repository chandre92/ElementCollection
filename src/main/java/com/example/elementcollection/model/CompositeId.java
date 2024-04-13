package com.example.elementcollection.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CompositeId implements Serializable {
    public String uuid;

    public String version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompositeId that = (CompositeId) o;
        return Objects.equals(uuid, that.uuid) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, version);
    }
}
