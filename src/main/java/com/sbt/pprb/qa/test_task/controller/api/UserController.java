package com.sbt.pprb.qa.test_task.controller.api;

import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import com.sbt.pprb.qa.test_task.model.exception.EntityNotFoundException;
import com.sbt.pprb.qa.test_task.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/users")
public class UserController {

    private UserService service;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok(service.getUsers());
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<AppUser> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(service.getUser(id));
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUser> createUser(@RequestBody AppUser user) throws URISyntaxException {
        AppUser created = service.createUser(user);
        return ResponseEntity.created(new URI("/api/users/" + created.getId())).body(created);
    }
}
