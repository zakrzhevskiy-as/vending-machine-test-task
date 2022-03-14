package com.sbt.pprb.qa.test_task.service;

import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import com.sbt.pprb.qa.test_task.model.exception.UsernameAlreadyTakenException;
import com.sbt.pprb.qa.test_task.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UsersRepository repository;

    public Optional<AppUser> getUser(String username) {
        return repository.findByUsername(username);
    }

    public AppUser createUser(AppUser user) {
        if (repository.findByUsername(user.getUsername()).isPresent()) {
            throw new UsernameAlreadyTakenException(user.getUsername());
        }

        return repository.save(user);
    }
}
