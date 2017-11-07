package com.emc.lean.signup.users;

import com.emc.lean.signup.applications.Application;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UsersTest {

    @Test
    public void construction() {
        User user = new User();
        assertNotNull(user);
    }

    @Test
    public void constructionWithArgs() {
        User user = new User("Name", "EMAIL", null, "ATTRIBUTES");

        assertNotNull(user);
        assertEquals("Name", user.getName());
        assertEquals("EMAIL", user.getEmail());
        assertEquals("ATTRIBUTES", user.getAttributes());
    }

    @Test
    public void setAndGetId() {
        User user = new User();
        user.setId(1);

        assertEquals(1, user.getId().intValue());
    }

    @Test
    public void setAndGetName() {
        User user = new User();
        user.setName("My Name");

        assertEquals("My Name", user.getName());
    }

    @Test
    public void setAndGetEmail() {
        User user = new User();
        user.setEmail("My emaiL");

        assertEquals("My emaiL", user.getEmail());
    }


    @Test
    public void setAndGetApplication() {
        User user = new User();
        user.setApplication(new Application("Name", "UUID", null, null));

        assertEquals("Name", user.getApplication().getName());
        assertEquals("UUID", user.getApplication().getUuid());
    }

    @Test
    public void setAndGetAttributes() {
        Map<String, String> user_1_attributes = new HashMap<String, String>();
        user_1_attributes.put("message", "This is my message, its lovely");
        user_1_attributes.put("bank name", "this is my bank name, its awesome");
        String user_1_attributes_string = new Gson().toJson(user_1_attributes);

        User user = new User();
        user.setAttributes(user_1_attributes_string);

        assertEquals("{\"bank name\":\"this is my bank name, its awesome\",\"message\":\"This is my message, its lovely\"}", user.getAttributes());
    }
}
