package com.study.users.controllers;

import com.study.users.dto.ApiResponse;
import com.study.users.dto.Role;
import com.study.users.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<Role>>>> getRoles() {
        return Mono.fromCallable(roleService::getRoles)
                .subscribeOn(Schedulers.boundedElastic())
                .map(roles -> ResponseEntity.ok(ApiResponse.ok(roles)));
    }
}
