package com.study.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserPage {
    private List<User> users;
    private long total;
    private int page;
    private int limit;
}
