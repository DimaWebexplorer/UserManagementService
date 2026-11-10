package com.study.users.controllers;

import com.study.users.dto.ApiResponse;
import com.study.users.dto.User;
import com.study.users.dto.UserRequest;
import com.study.users.services.UserService;
import com.study.users.validators.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final RequestValidator validator;
    private final UserService userService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<User>>>> getUsers() {
        return Mono.fromCallable(userService::getUsers)
                .subscribeOn(Schedulers.boundedElastic())
                .map(users -> ResponseEntity.ok(ApiResponse.ok(users)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<User>>> getUser(@PathVariable("id") int id) {
        return Mono.fromCallable(() -> userService.getUser(id))
                .subscribeOn(Schedulers.boundedElastic())
                .map(user -> ResponseEntity.ok(ApiResponse.ok(user)));
    }

    @GetMapping("/{id}/roles")
    public Mono<ResponseEntity<ApiResponse<List<String>>>> getUserRoles(@PathVariable("id") int id) {
        return Mono.fromCallable(() -> userService.getUserRoles(id))
                .subscribeOn(Schedulers.boundedElastic())
                .map(roles -> ResponseEntity.ok(ApiResponse.ok(roles)));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<ApiResponse<User>>> register(@RequestBody UserRequest body) {
        return Mono.fromCallable(() -> {
                    validator.validateRegister(body);
                    return userService.register(body);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(user)));
    }
}
