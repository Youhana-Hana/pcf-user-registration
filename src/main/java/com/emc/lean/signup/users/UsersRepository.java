package com.emc.lean.signup.users;

import com.emc.lean.signup.applications.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UsersRepository extends CrudRepository<User, Integer>, JpaRepository<User, Integer>{
    Page<User> findByApplication(Application application, Pageable pageable);

    Long countByApplication(Application application);

    @Query(value  = "SELECT new com.emc.lean.signup.users.Stats(u.application.name as name, u.application.uuid as uuid , count(u.application.id) as count) FROM User u GROUP BY u.application.id")
    List<Stats> countPerApplication();
}
