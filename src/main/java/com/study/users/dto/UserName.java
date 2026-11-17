package com.study.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserName {
    private Integer id;
    private String firstName;
    private String lastName;
}
