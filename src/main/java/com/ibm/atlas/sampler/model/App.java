package com.ibm.atlas.sampler.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "app")
public class App {
    private String name;
    private String description;
    private String version;

    public App(String name, String description, String version) {
        this.name = name;
        this.description = description;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }
}
