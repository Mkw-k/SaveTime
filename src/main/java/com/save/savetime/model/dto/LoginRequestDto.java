package com.save.savetime.model.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
	private String username;
	private String password;
	private String userType;
	private String target;
}
