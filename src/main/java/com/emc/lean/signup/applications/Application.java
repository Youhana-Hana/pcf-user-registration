package com.emc.lean.signup.applications;

import javax.persistence.*;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    private String googleProperty;

    private String campaignToken;

    @Column(unique = true)
    private String uuid;

    public Application() {
    }

    public Application(String name, String uuid, String googleProperty, String campaignToken) {
        this.name = name;
        this.setUuid(uuid);
        this.googleProperty = googleProperty;
        this.campaignToken = campaignToken;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoogleProperty() {
        return googleProperty;
    }

    public void setGoogleProperty(String googleProperty) {
        this.googleProperty = googleProperty;
    }

    public String getUuid() {
        return this.uuid.toUpperCase();
    }

    public void setUuid(String uuid) {
        this.uuid = uuid.toUpperCase();
    }

    public String getCampaignToken() {
        return campaignToken;
    }

    public void setCampaignToken(String campaignToken) {
        this.campaignToken = campaignToken;
    }
}
