package com.emc.lean.signup.users;

import java.io.Serializable;

public class Stats implements Serializable {

    private String name;

    private String uuid;

    private long count;

    public Stats(String name, String uuid, long count) {
        this.name = name;
        this.uuid = uuid;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

}
