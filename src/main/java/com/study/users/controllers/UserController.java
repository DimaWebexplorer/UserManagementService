package com.study.users.controllers;

import com.study.users.dto.ApiResponse;
import com.study.users.dto.User;
import com.study.users.dto.UserRequest;
import com.study.users.dto.LoginRequest;
import com.study.users.dto.LoginResponse;
import com.study.users.dto.RoleRequest;
import com.study.users.dto.UpdateUserRequest;
import com.study.users.dto.UserName;
import com.study.users.dto.UserPage;
import com.study.users.services.RoleService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final RequestValidator validator;
    private final UserService userService;
    private final RoleService roleService;

    @GetMapping(params = "!ids")
    public Mono<ResponseEntity<ApiResponse<UserPage>>> getUsers(
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        return Mono.fromCallable(() -> {
                    validator.validatePage(role, page, limit);
                    return userService.getUsers(role, page, limit);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(users -> ResponseEntity.ok(ApiResponse.ok(users)));
    }

    @GetMapping(params = "ids")
    public Mono<ResponseEntity<ApiResponse<List<UserName>>>> getUserNames(
            @RequestParam("ids") String ids, @RequestParam Map<String, String> parameters) {
        return Mono.fromCallable(() -> {
                    List<Integer> userIds = validator.parseIds(ids, parameters);
                    return userService.getUserNames(userIds);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(users -> ResponseEntity.ok(ApiResponse.ok(users)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<User>>> getUser(@PathVariable("id") int id) {
        return Mono.fromCallable(() -> {
                    validator.validateId(id, "id");
                    return userService.getUser(id);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(user -> ResponseEntity.ok(ApiResponse.ok(user)));
    }

    @GetMapping("/{id}/roles")
    public Mono<ResponseEntity<ApiResponse<List<String>>>> getUserRoles(@PathVariable("id") int id) {
        return Mono.fromCallable(() -> {
                    validator.validateId(id, "id");
                    return userService.getUserRoles(id);
                })
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

    @PostMapping("/login")
    public Mono<ResponseEntity<ApiResponse<LoginResponse>>> login(@RequestBody LoginRequest body) {
        return Mono.fromCallable(() -> {
                    validator.validateLogin(body);
                    return userService.login(body);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(login -> ResponseEntity.ok(ApiResponse.ok(login)));
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<User>>> updateUser(@PathVariable("id") int id,
            @AuthenticationPrincipal Jwt jwt, @RequestBody UpdateUserRequest body) {
        return Mono.fromCallable(() -> {
                    validator.validateId(id, "id");
                    validator.validateOwnProfile(id, Integer.parseInt(jwt.getSubject()));
                    validator.validateUpdate(body);
                    return userService.updateUser(id, body);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(user -> ResponseEntity.ok(ApiResponse.ok(user)));
    }

    @PostMapping("/{id}/roles")
    public Mono<ResponseEntity<ApiResponse<List<String>>>> assignRole(@PathVariable("id") int id,
                                                                    @RequestBody RoleRequest body) {
        return Mono.fromCallable(() -> {
                    validator.validateId(id, "id");
                    validator.validateId(body.getRoleId(), "role_id");
                    return roleService.assignRole(id, body.getRoleId());
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(roles -> ResponseEntity.ok(ApiResponse.ok(roles)));
    }

    @DeleteMapping("/{id}/roles/{role_id}")
    public Mono<ResponseEntity<ApiResponse<List<String>>>> removeRole(@PathVariable("id") int id,
                                                                   @PathVariable("role_id") int roleId) {
        return Mono.fromCallable(() -> {
                    validator.validateId(id, "id");
                    validator.validateId(roleId, "role_id");
                    return roleService.removeRole(id, roleId);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(roles -> ResponseEntity.ok(ApiResponse.ok(roles)));
    }
}
