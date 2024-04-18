package com.save.savetime.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String id;
    private String name;
    private String email;
    private int age;
    private String password;
    private List<String> roles;
    private String roleName; //FIXME 나중에 리스트로 그냥 바로 받아오는거로 변경할것
}


