package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.model.AppUser;
import com.sbt.pprb.qa.test_task.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository repository;

    public List<AppUser> getUsers() {
        return repository.findAll();
    }

    public AppUser getUser(Long id) {
        return repository.getById(id);
    }

    public AppUser createUser(AppUser user) {
        return repository.save(user);
    }
}
