package com.emc.lean.signup.applications;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ApplicationsRepository extends CrudRepository<Application, Integer>, JpaRepository<Application, Integer> {
    Iterable<Application> findByUuid(String uuid);
}

