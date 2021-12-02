package com.sbt.pprb.qa.test_task.repository;

import com.sbt.pprb.qa.test_task.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
}
