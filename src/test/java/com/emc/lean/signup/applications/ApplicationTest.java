package com.emc.lean.signup.applications;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ApplicationTest {

    @Test
    public void construction() {
        Application application = new Application();
        assertNotNull(application);
    }

    @Test
    public void setAndGetId() {
        Application application = new Application();
        application.setId(1);

        assertEquals(1, application.getId().intValue());
    }

    @Test
    public void setAndGetName() {
        Application application = new Application();
        application.setName("My Name");

        assertEquals("My Name", application.getName());
    }

    @Test
    public void setAndGetUUID() {
        Application application = new Application();
        application.setUuid("My UUID");

        assertEquals("MY UUID", application.getUuid());
    }

    @Test
    public void setAndGetGoogleProperty() {
        Application application = new Application();
        application.setGoogleProperty("My Google property");

        assertEquals("My Google property", application.getGoogleProperty());
    }

    @Test
    public void setAndGetCampaignToken() {
        Application application = new Application();
        application.setCampaignToken("My Campaign Token");

        assertEquals("My Campaign Token", application.getCampaignToken());
    }
}
