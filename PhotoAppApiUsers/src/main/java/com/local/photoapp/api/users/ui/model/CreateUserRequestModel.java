package com.local.photoapp.api.users.ui.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateUserRequestModel {
	
	@NotNull(message="First Name is mandatory")
	@Size(min = 2, message="First name must not be less than 2 chars")
	private String firstName;
	@NotNull(message="Last Name is mandatory")
	@Size(min = 2, message="Last name must not be less than 2 chars")
	private String lastName;
	@NotNull(message="Password is mandatory")
	@Size(min = 8,max=16, message="Password must be >8 and <16")
	private String password;
	@NotNull(message="Email is mandatory")
	@Email
	private String email;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

}
