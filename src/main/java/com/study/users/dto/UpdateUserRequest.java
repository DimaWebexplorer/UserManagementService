package com.study.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;

    @JsonIgnore
    private final Set<String> fields = new HashSet<>();

    @JsonSetter("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        fields.add("first_name");
    }

    @JsonSetter("last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
        fields.add("last_name");
    }

    @JsonSetter("phone_number")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        fields.add("phone_number");
    }

    @JsonSetter("password")
    public void setPassword(String password) {
        this.password = password;
        fields.add("password");
    }

    public boolean hasField(String field) {
        return fields.contains(field);
    }
}
