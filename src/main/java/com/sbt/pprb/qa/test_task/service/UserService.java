package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import com.sbt.pprb.qa.test_task.model.exception.EntityNotFoundException;
import com.sbt.pprb.qa.test_task.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private UsersRepository repository;

    public List<AppUser> getUsers() {
        return repository.findAll();
    }

    public Optional<AppUser> getUser(String username) {
        return repository.findByUsername(username);
    }

    public AppUser getUser(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    public AppUser createUser(AppUser user) {
        return repository.save(user);
    }
}
