package com.usrinter.zzaming;

public class QuestBundle {
    private String bundleId;
    private String name;
    private String description;

    public QuestBundle(String bundleId, String name, String description) {
        this.bundleId = bundleId;
        this.name = name;
        this.description = description;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
