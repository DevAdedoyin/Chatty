package com.example.chatty;

public class ContactInfo {

    private String name;
    private String relationship;
    private String pm;
    private String lm;
    private Boolean online;

    public ContactInfo(String name, String relationship, String lm, Boolean online) {
        this.name = name;
        this.relationship = relationship;
        this.lm = lm;
        this.online = online;
    }

    public ContactInfo(String name, String relationship, String pm) {
        this.name = name;
        this.relationship = relationship;
        this.pm = pm;
    }

    public String getName() {
        return name;
    }

    public String getRelationship() {
        return relationship;
    }

    public String getPm() {
        return pm;
    }

    public String getLm() {
        return lm;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public void setPm(String pm) {
        this.pm = pm;
    }

    public void setLm(String lm) {
        this.lm = lm;
    }
}
