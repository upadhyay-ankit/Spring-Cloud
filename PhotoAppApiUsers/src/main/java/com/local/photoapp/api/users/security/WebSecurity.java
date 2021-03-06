package com.local.photoapp.api.users.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.local.photoapp.api.users.service.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter{

	private Environment environment;
	private UserService userService;
	private BCryptPasswordEncoder pwdEncoder;
	
	@Autowired
	public WebSecurity(Environment environment, UserService userService,
			BCryptPasswordEncoder pwdEncoder) {
		this.environment = environment;
		this.userService = userService;
		this.pwdEncoder = pwdEncoder;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/users-ws/**").permitAll()
		.and()
		.addFilter(getAuthFilter());
		http.csrf().disable();
		http.headers().frameOptions().disable();
	}

	private AuthenticationFilter getAuthFilter() throws Exception {
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService,
				environment, authenticationManager());
		//authenticationFilter.setAuthenticationManager(authenticationManager());
		authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));
		return authenticationFilter;
	}
	
	@Override
	 protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		 auth.userDetailsService(userService).passwordEncoder(pwdEncoder);
	 }
}
