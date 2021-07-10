package com.local.photoapp.api.users.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.local.photoapp.api.users.shared.UserDto;

public interface UserService extends UserDetailsService {

	UserDto createUser(UserDto userDetils);
	
	UserDto getUserDetailsByEmail(String email);
	
	UserDto getUserByUserId(String userId);
}
