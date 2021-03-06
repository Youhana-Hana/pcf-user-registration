package com.emc.lean.signup.applications;

import org.assertj.core.util.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ApplicationsRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    ApplicationsRepository applicationsRepository;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void getAllApplication() {
        Application application1 = new Application("Name 1", "uuid 1", null, null);
        Application application2 = new Application("Name 2", "uuid 2", "P1", "T2");

        this.entityManager.persist(application1);
        this.entityManager.persist(application2);

        Iterable<Application> actual = applicationsRepository.findAll();


        ArrayList<Application> applications = Lists.newArrayList(actual.iterator());
        assertEquals(2, applications.size());

        Application result = applications.get(0);
        assertEquals("Name 1", result.getName());
        assertEquals("UUID 1", result.getUuid());

        result = applications.get(1);
        assertEquals("Name 2", result.getName());
        assertEquals("UUID 2", result.getUuid());
        assertEquals("P1", result.getGoogleProperty());
        assertEquals("T2", result.getCampaignToken());
    }

    @Test
    public void getApplicationByUUID() {
        Application application1 = new Application("Name 1", "uuid 1", null, null);
        Application application2 = new Application("Name 2", "uuid 2", "P1", "T2");

        this.entityManager.persist(application1);
        this.entityManager.persist(application2);

        Iterable<Application> actual = applicationsRepository.findByUuid("UUID 1");
        assertTrue(actual.iterator().hasNext());
        Application result = actual.iterator().next();
        assertEquals("Name 1", result.getName());
        assertEquals("UUID 1", result.getUuid());
    }

    @Test
    public void autoGeneratedApplicationId() {
        Application application1 = new Application("Name 1", "uuid 1", null, null);
        Application application2 = new Application("Name 2", "uuid 2", "P1", "T2");

        this.entityManager.persist(application1);
        this.entityManager.persist(application2);

        Iterable<Application> actual = applicationsRepository.findAll();
        ArrayList<Application> applications = Lists.newArrayList(actual.iterator());
        assertEquals(2, applications.size());

        assertNotEquals(0, applications.get(0).getId().intValue());
        assertNotEquals(0, applications.get(1).getId().intValue());
        assertTrue(applications.get(1).getId().intValue() > applications.get(0).getId().intValue());
    }

    @Test
    public void UUIDisUniqulyIdentifiyingApplication() throws Exception {
        expectedEx.expect(javax.persistence.PersistenceException.class);
        expectedEx.expectMessage("org.hibernate.exception.ConstraintViolationException: could not execute statement");

        Application application1 = new Application("Name 1", "uuid 1", null, null);
        Application application2 = new Application("Name 2", "uuid 1", "P1", "T2");

        this.entityManager.persist(application1);
        this.entityManager.persist(application2);
    }
}
