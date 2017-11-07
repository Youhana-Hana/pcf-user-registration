package com.emc.lean.signup.users;

import com.emc.lean.signup.applications.Application;

import javax.persistence.*;

@Entity
@Table(name="users",
      uniqueConstraints = {@UniqueConstraint(columnNames = {"email", "appId"})})
public class User{
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    private String name;

    private String attributes;

    @Column(name = "email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "appId", nullable = false, updatable = false, referencedColumnName = "id")
    private Application application;

    public User() {

    }

    public User(String name, String email, Application application, String attributes) {
        this.name = name;
        this.email = email;
        this.application = application;
        this.attributes = attributes;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Application  getApplication() {
        return this.application;
    }

    public void setApplication(Application app) {
        this.application = app;
    }

    public String getAttributes() {
        return this.attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }
}