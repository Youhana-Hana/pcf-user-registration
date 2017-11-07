package com.emc.lean.signup.controllers;

import com.emc.lean.signup.applications.Application;
import com.emc.lean.signup.applications.ApplicationsRepository;
import com.emc.lean.signup.users.Stats;
import com.emc.lean.signup.users.User;
import com.emc.lean.signup.users.UsersRepository;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.doReturn;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ApisController.class)
public class ApisControllerTest {

    private final String ApiKey = "cMGdJYk85UX44fyQEVYa-7ivvN6FM3XHNq4PVIdhI";

    @Autowired
    private MockMvc mvc;

    @MockBean
    UsersRepository usersRepository;

    @MockBean
    ApplicationsRepository applicationsRepository;

    @Test
    public void addFistUser() throws Exception {
        Application application = new Application("Name 2", "uuid 2", "P1", "T2");
        User user = new User("Name 1", "email 1", application, "ATTRIBUTES_1");
        String body = new Gson().toJson(user);

        doReturn(new ArrayList<Application>()).when(this.applicationsRepository).findByUuid("UUID 2");

        Application savedApp = new Application("Name 2", "uuid 2", "P1", "T2");
        savedApp.setId(1);
        doReturn(savedApp).when(this.applicationsRepository).save(application);

        user.setId(1);
        user.setApplication(savedApp);
        doReturn(user).when(this.usersRepository).save(any(User.class));

        String expectedBody = new Gson().toJson(user);

        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", ApiKey)
                .content(body)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(expectedBody));

        verify(this.applicationsRepository).findByUuid("UUID 2");
        verify(this.applicationsRepository).save(any(Application.class));
        verify(this.usersRepository).save(any(User.class));
    }

    @Test
    public void addSecondUser() throws Exception {
        Application application = new Application("Name 2", "uuid 2", "P1", "T2");
        User user = new User("Name 1", "email 1", application, "ATTRIBUTES_2");
        String body = new Gson().toJson(user);

        Application foundApplication = new Application("Name 2", "uuid 2", "P1", "T2");
        foundApplication.setId(1);
        ArrayList<Application> applications = new ArrayList<Application>();
        applications.add(foundApplication);
        doReturn(applications).when(this.applicationsRepository).findByUuid("UUID 2");

        user.setId(1);
        user.setApplication(foundApplication);
        doReturn(user).when(this.usersRepository).save(any(User.class));

        String expectedBody = new Gson().toJson(user);

        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", ApiKey)
                .content(body)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(expectedBody));

        verify(this.applicationsRepository).findByUuid("UUID 2");
        verify(this.applicationsRepository, never()).save(any(Application.class));
        verify(this.usersRepository).save(any(User.class));
    }

    @Test
    public void missingApiKeyHeader() throws Exception {
        Application application = new Application("Name 2", "uuid 2", "P1", "T2");
        User user = new User("Name 1", "email 1", application, "ATTRIBUTES_1");
        String body = new Gson().toJson(user);

        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body)
        )
                .andExpect(status().isBadRequest());

        verify(this.applicationsRepository, never()).findByUuid(anyString());
        verify(this.applicationsRepository, never()).save(any(Application.class));
        verify(this.usersRepository, never()).save(any(User.class));
    }

    @Test
    public void incorrectApiKeyHeader() throws Exception {
        Application application = new Application("Name 2", "uuid 2", "P1", "T2");
        User user = new User("Name 1", "email 1", application, "ATTRIBUTES_1");
        String body = new Gson().toJson(user);

        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", "KEY")
                .content(body)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("un-authorized"));

        verify(this.applicationsRepository, never()).findByUuid(anyString());
        verify(this.applicationsRepository, never()).save(any(Application.class));
        verify(this.usersRepository, never()).save(any(User.class));
    }

    @Test
    public void addUserFailed() throws Exception {
        Application application = new Application("Name 2", "uuid 2", "P1", "T2");
        User user = new User("Name 1", "email 1", application, "ATTRIBUTES_1");
        String body = new Gson().toJson(user);

        doThrow(Exception.class).when(this.applicationsRepository).findByUuid("UUID 2");

        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", ApiKey)
                .content(body)
        )
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error!"));
    }

    @Test
    public void getUsers() throws Exception {
        Application application = new Application("Name 2", "uuid 2", "P1", "T2");
        User user = new User("Name 1", "email 1", application, "ATTRIBUTES_1");

        ArrayList<User> users = new ArrayList<User>();
        users.add(user);

        Page<User> usersPage = new PageImpl<User>(users);

        doReturn(usersPage).when(this.usersRepository).findAll(any(PageRequest.class));

        String expectedBody = "{\"content\":[{\"id\":null,\"name\":\"Name 1\",\"email\":\"email 1\",\"application\":{\"id\":null,\"name\":\"Name 2\",\"googleProperty\":\"P1\",\"campaignToken\":\"T2\",\"uuid\":\"UUID 2\"}}],\"last\":true,\"totalPages\":1,\"totalElements\":1,\"sort\":null,\"first\":true,\"numberOfElements\":1,\"size\":0,\"number\":0}";

        mvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", ApiKey)
        )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedBody, false));

        ArgumentCaptor<PageRequest> pageRequestArgumentCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(this.usersRepository).findAll(pageRequestArgumentCaptor.capture());
        PageRequest pageRequest = pageRequestArgumentCaptor.getValue();
        assertEquals(0, pageRequest.getPageNumber());
        assertEquals(100, pageRequest.getPageSize());
    }

    @Test
    public void getUsersByPageAndSize() throws Exception {
        Application application = new Application("Name 2", "uuid 2", "P1", "T2");
        User user = new User("Name 1", "email 1", application, "ATTRIBUTES_1");

        ArrayList<User> users = new ArrayList<User>();
        users.add(user);

        Page<User> usersPage = new PageImpl<User>(users);

        doReturn(usersPage).when(this.usersRepository).findAll(any(PageRequest.class));

        mvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", ApiKey)
                .param("page", "1")
                .param("size", "10")
        )
                .andExpect(status().isOk());

        ArgumentCaptor<PageRequest> pageRequestArgumentCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(this.usersRepository).findAll(pageRequestArgumentCaptor.capture());
        PageRequest pageRequest = pageRequestArgumentCaptor.getValue();
        assertEquals(1, pageRequest.getPageNumber());
        assertEquals(10, pageRequest.getPageSize());
    }

    @Test
    public void sizeGreaterThan1000GetUsersByPageAndSize() throws Exception {
        Application application = new Application("Name 2", "uuid 2", "P1", "T2");
        User user = new User("Name 1", "email 1", application, "ATTRIBUTES_1");

        ArrayList<User> users = new ArrayList<User>();
        users.add(user);

        Page<User> usersPage = new PageImpl<User>(users);

        doReturn(usersPage).when(this.usersRepository).findAll(any(PageRequest.class));

        mvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", ApiKey)
                .param("page", "1")
                .param("size", "100000000")
        )
                .andExpect(status().isOk());

        ArgumentCaptor<PageRequest> pageRequestArgumentCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(this.usersRepository).findAll(pageRequestArgumentCaptor.capture());
        PageRequest pageRequest = pageRequestArgumentCaptor.getValue();
        assertEquals(1, pageRequest.getPageNumber());
        assertEquals(1000, pageRequest.getPageSize());
    }

    @Test
    public void uuidIsEmptyGetUsersByPageAndSize() throws Exception {
        Application application = new Application("Name 2", "uuid 2", "P1", "T2");
        User user = new User("Name 1", "email 1", application, "ATTRIBUTES_1");

        ArrayList<User> users = new ArrayList<User>();
        users.add(user);

        Page<User> usersPage = new PageImpl<User>(users);

        doReturn(usersPage).when(this.usersRepository).findAll(any(PageRequest.class));

        mvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", ApiKey)
                .param("uuid", "")
                .param("page", "1")
                .param("size", "100000000")
        )
                .andExpect(status().isOk());

        verify(this.usersRepository, never()).findByApplication(any(Application.class), any(PageRequest.class));

        ArgumentCaptor<PageRequest> pageRequestArgumentCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(this.usersRepository).findAll(pageRequestArgumentCaptor.capture());
        PageRequest pageRequest = pageRequestArgumentCaptor.getValue();
        assertEquals(1, pageRequest.getPageNumber());
        assertEquals(1000, pageRequest.getPageSize());
    }

    @Test
    public void missingApiKeyHeaderForGetUser() throws Exception {
        mvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("page", "1")
                .param("size", "10")
        )
                .andExpect(status().isBadRequest());

        verify(this.usersRepository, never()).findAll(any(PageRequest.class));
    }

    @Test
    public void incorrectApiKeyHeaderForGetUser() throws Exception {
        mvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", "ApiKey")
                .param("page", "1")
                .param("size", "10")
        )
                .andExpect(status().isUnauthorized());

        verify(this.usersRepository, never()).findAll(any(PageRequest.class));
    }

    @Test
    public void getUsersByUuid() throws Exception {
        Application application = new Application("Name 2", "uuid 2", "P1", "T2");
        User user = new User("Name 1", "email 1", application, "ATTRIBUTES_1");

        ArrayList<User> users = new ArrayList<User>();
        users.add(user);

        Page<User> usersPage = new PageImpl<User>(users);

        ArrayList<Application> applications = new ArrayList<Application>();
        applications.add(application);
        doReturn(applications).when(this.applicationsRepository).findByUuid("UUID 2");
        doReturn(usersPage).when(this.usersRepository).findByApplication(any(Application.class), any(PageRequest.class));

        String expectedBody = "{\"content\":[{\"id\":null,\"name\":\"Name 1\",\"email\":\"email 1\",\"application\":{\"id\":null,\"name\":\"Name 2\",\"googleProperty\":\"P1\",\"campaignToken\":\"T2\",\"uuid\":\"UUID 2\"}}],\"last\":true,\"totalPages\":1,\"totalElements\":1,\"sort\":null,\"numberOfElements\":1,\"first\":true,\"size\":0,\"number\":0}";

        mvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", ApiKey)
                .param("uuid", "uuid 2")
        )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedBody, false));

        verify(this.usersRepository, never()).findAll(any(PageRequest.class));

        ArgumentCaptor<PageRequest> pageRequestArgumentCaptor = ArgumentCaptor.forClass(PageRequest.class);
        ArgumentCaptor<Application> applicationArgumentCaptor = ArgumentCaptor.forClass(Application.class);

        verify(this.usersRepository).findByApplication(applicationArgumentCaptor.capture(), pageRequestArgumentCaptor.capture());
        Application applicationCapture = applicationArgumentCaptor.getValue();
        assertEquals("UUID 2", applicationCapture.getUuid());
        assertEquals("Name 2", applicationCapture.getName());

        PageRequest pageRequest = pageRequestArgumentCaptor.getValue();
        assertEquals(0, pageRequest.getPageNumber());
        assertEquals(100, pageRequest.getPageSize());
    }

    @Test
    public void uuidDoesNotExistGetUsersByUuid() throws Exception {
        ArrayList<User> users = new ArrayList<User>();
        Page<User> usersPage = new PageImpl<User>(users);
        ArrayList<Application> applications = new ArrayList<Application>();

        doReturn(applications).when(this.applicationsRepository).findByUuid("UUID 2");
        doReturn(usersPage).when(this.usersRepository).findByApplication(any(Application.class), any(PageRequest.class));

        String expectedBody = "[]";

        mvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", ApiKey)
                .param("uuid", "uuid 2")
        )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedBody, false));

        verify(this.usersRepository, never()).findAll(any(PageRequest.class));
        verify(this.usersRepository, never()).findByApplication(any(Application.class), any(PageRequest.class));
    }

    @Test
    public void errorDuringGettingsUsersByUuid() throws Exception {
        doThrow(Exception.class).when(this.applicationsRepository).findByUuid("UUID 2");

        mvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", ApiKey)
                .param("uuid", "uuid 2")
        )
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error!"));

        verify(this.usersRepository, never()).findAll(any(PageRequest.class));
        verify(this.applicationsRepository).findByUuid("UUID 2");
    }

    @Test
    public void getStats() throws Exception {
        ArrayList<Stats> stats = new ArrayList<Stats>();
        stats.add(new Stats("Name 1", "UUID 1", 2));
        stats.add(new Stats("Name 2", "UUID 2", 3));

        doReturn(stats).when(this.usersRepository).countPerApplication();

        String expectedBody = new Gson().toJson(stats);

        mvc.perform(get("/users/stats")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", ApiKey)
        )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedBody, false));

        verify(this.usersRepository).countPerApplication();
    }

    @Test
    public void missingApiKeyHeaderForGetStats() throws Exception {
        mvc.perform(get("/users/stats")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());

        verify(this.usersRepository, never()).countPerApplication();
    }

    @Test
    public void incorrectApiKeyHeaderForGetStats() throws Exception {
        mvc.perform(get("/users/stats")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", "ApiKey")
        )
                .andExpect(status().isUnauthorized());

        verify(this.usersRepository, never()).countPerApplication();
    }

    @Test
    public void getStatsByApplication() throws Exception {
        Application application = new Application("Name 2", "uuid 2", "P1", "T2");
        ArrayList<Application> applications = new ArrayList<Application>();
        applications.add(application);
        doReturn(applications).when(this.applicationsRepository).findByUuid("UUID 2");

        ArrayList<Stats> stats = new ArrayList<Stats>();
        stats.add(new Stats("Name 2", "UUID 2", 2));
        doReturn(2l).when(this.usersRepository).countByApplication(any(Application.class));

        String expectedBody = new Gson().toJson(stats);

        mvc.perform(get("/users/stats")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", ApiKey)
                .param("uuid", "uuid 2")
        )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedBody, false));

        verify(this.usersRepository, never()).countPerApplication();

        ArgumentCaptor<Application> applicationArgumentCaptor = ArgumentCaptor.forClass(Application.class);

        verify(this.usersRepository).countByApplication(applicationArgumentCaptor.capture());
        Application applicationCapture = applicationArgumentCaptor.getValue();
        assertEquals("UUID 2", applicationCapture.getUuid());
        assertEquals("Name 2", applicationCapture.getName());
    }

    @Test
    public void applicationUuidIsEmptyGetStats() throws Exception {
        ArrayList<Stats> stats = new ArrayList<Stats>();
        stats.add(new Stats("Name 1", "UUID 1", 2));
        stats.add(new Stats("Name 2", "UUID 2", 3));

        doReturn(stats).when(this.usersRepository).countPerApplication();

        String expectedBody = new Gson().toJson(stats);

        mvc.perform(get("/users/stats")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", ApiKey)
                .param("uuid", "")
        )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedBody, false));

        verify(this.usersRepository).countPerApplication();
        verify(this.usersRepository, never()).countByApplication(any(Application.class));
    }

    @Test
    public void uuidDoesNotExistGetStatsByApplication() throws Exception {
        ArrayList<Application> applications = new ArrayList<Application>();
        doReturn(applications).when(this.applicationsRepository).findByUuid("UUID 2");

        String expectedBody = "[]";

        mvc.perform(get("/users/stats")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", ApiKey)
                .param("uuid", "uuid 2")
        )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedBody, false));

        verify(this.usersRepository, never()).countPerApplication();
        verify(this.usersRepository, never()).countByApplication(any(Application.class));
    }

    @Test
    public void errorDuringGettingsByApplication() throws Exception {
        doThrow(Exception.class).when(this.applicationsRepository).findByUuid("UUID 2");

        mvc.perform(get("/users/stats")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-api-key", ApiKey)
                .param("uuid", "uuid 2")
        )
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error!"));

        verify(this.usersRepository, never()).countByApplication(any(Application.class));
        verify(this.applicationsRepository).findByUuid("UUID 2");
    }
}
