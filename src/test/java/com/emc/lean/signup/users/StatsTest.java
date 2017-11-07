package com.emc.lean.signup.users;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class StatsTest {

    Stats stats;

    @Before
    public void setup() {
        stats = new Stats("Stats 1", "UUID 1", 10);
    }

    @Test
    public void setAndGet() {
        assertEquals("Stats 1", this.stats.getName());
        assertEquals("UUID 1", this.stats.getUuid());
        assertEquals(10, this.stats.getCount());
    }

    @Test
    public void setAndGetName() {
        this.stats.setName("My Name");

        assertEquals("My Name", this.stats.getName());
    }

    @Test
    public void setAndGetUUID() {
        this.stats.setUuid("My UUID");

        assertEquals("My UUID", this.stats.getUuid());
    }

    @Test
    public void setAndGetCount() {
        this.stats.setCount(42);

        assertEquals(42, this.stats.getCount());
    }
}
