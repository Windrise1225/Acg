package org.example.acg.entity;


import lombok.Data;

import java.time.ZonedDateTime;


@Data
public class User {
    private Integer id;
    private String name;
    private String sex;
    private String phone;
    private String email;
    private String password;
    private ZonedDateTime createTime;
    private int isDelete;
}
