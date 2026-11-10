package com.study.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    @JsonIgnore
    private String passwordHash;

    private List<String> roles;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
