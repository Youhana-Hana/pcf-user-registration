package com.emc.lean.signup.controllers;

import com.emc.lean.signup.applications.Application;
import com.emc.lean.signup.applications.ApplicationsRepository;
import com.emc.lean.signup.users.Stats;
import com.emc.lean.signup.users.User;
import com.emc.lean.signup.users.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(path="/users")
public class ApisController {
    private static final Logger logger = LoggerFactory.getLogger(ApisController.class);
    private final String ApiKey = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx";

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ApplicationsRepository applicationsRepository;

    @PostMapping(path="", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addNewUser(
            @RequestHeader(value = "x-api-key") String key,
            @RequestBody User entry) {
        logger.debug("add new user");

        if(!authenticated(key)) {
            logger.info("add new user, invalid key", key);
            return new ResponseEntity<String>("un-authorized", HttpStatus.UNAUTHORIZED);
        }

        try {
            Application referencedApp = this.findApp(entry.getApplication());
            if(referencedApp == null) {
                referencedApp = addApp(entry.getApplication());
            }
            User user = addUser(entry, referencedApp);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } catch(Exception e) {
            logger.error("add user failed", entry, e);
            return new ResponseEntity<String>("Error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path="", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUsers(
            @RequestHeader(value = "x-api-key") String key,
            @RequestParam(value = "uuid", required = false) String uuid,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "100") int size) {
        logger.debug("get users");

        if(!authenticated(key)) {
            logger.info("get users, invalid key", key);
            return new ResponseEntity<String>("un-authorized!", HttpStatus.UNAUTHORIZED);
        }

        try {
            Iterable<User> results;

            if (size > 1000) { size = 1000; }
            PageRequest pageRequest = new PageRequest(page, size);

            if(uuid != null && !uuid.isEmpty()) {
                results = getUsersByApp(uuid, pageRequest);
            } else {
                results = usersRepository.findAll(pageRequest);
            }

            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch(Exception e) {
            logger.error("getUsers failed", e);
            return new ResponseEntity<>("Error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path="/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getStats(
            @RequestHeader(value = "x-api-key") String key,
            @RequestParam(value = "uuid", required = false) String uuid) {
        logger.debug("get count");

        if(!authenticated(key)) {
            logger.info("get count, invalid key", key);
            return new ResponseEntity<String>("un-authorized!", HttpStatus.UNAUTHORIZED);
        }

        try {
            List<Stats> results = new ArrayList<Stats>();

            if(uuid != null && !uuid.isEmpty()) {
                Stats stats = getUsersCountByApp(uuid);
                if(stats != null) {
                    results.add(stats);
                }
            } else {
                results = usersRepository.countPerApplication();
            }

            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch(Exception e) {
            logger.error("getUsers failed", e);
            return new ResponseEntity<>("Error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean authenticated(String key) {
        return key.equals(ApiKey);
    }

    private Stats getUsersCountByApp(String uuid) {
        Application application = this.findAppByUuid(uuid);
        if(application != null) {
            long count = usersRepository.countByApplication(application);
            return new Stats(application.getName(), application.getUuid(), count);
        }

        return null;
    }

    private Iterable<User> getUsersByApp(String uuid, PageRequest pageRequest) {
        Application application = this.findAppByUuid(uuid);
        if(application != null) {
            return usersRepository.findByApplication(application, pageRequest);
        }

        return new ArrayList<User>();
    }

    private User addUser(User entry, Application application) {
        User user = new User();
        user.setName((entry.getName()));
        user.setEmail(entry.getEmail());
        user.setApplication(application);
        return usersRepository.save(user);
    }

    private Application findApp(Application application) {
        Iterable<Application> apps = applicationsRepository.findByUuid(application.getUuid());
        long size = apps.spliterator().getExactSizeIfKnown();
        return (size != 0) ? apps.iterator().next() : null;
    }

    private Application findAppByUuid (String uuid) {
        Application application = new Application();
        application.setUuid(uuid);
        return findApp(application);
    }

    private Application addApp(Application application) {
        return applicationsRepository.save(application);
    }
}
