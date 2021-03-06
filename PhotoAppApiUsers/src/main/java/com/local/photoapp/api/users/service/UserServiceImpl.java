package com.local.photoapp.api.users.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.local.photoapp.api.users.data.AlbumsServiceClient;
import com.local.photoapp.api.users.data.UserEntity;
import com.local.photoapp.api.users.data.UserRepository;
import com.local.photoapp.api.users.shared.UserDto;
import com.local.photoapp.api.users.ui.model.AlbumResponseModel;

import feign.FeignException;

@Service
public class UserServiceImpl implements UserService{

	UserRepository userRepository;
	BCryptPasswordEncoder pwdEncoder;
	//RestTemplate restTemplate;
	Environment env;
	AlbumsServiceClient albumsClient;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository,
			BCryptPasswordEncoder pwdEncoder, AlbumsServiceClient albumsClient,
			Environment env) {
		this.userRepository = userRepository;
		this.pwdEncoder = pwdEncoder;
		this.albumsClient = albumsClient;
		this.env = env;
	}
	
	@Override
	public UserDto createUser(UserDto userDetails) {	
		userDetails.setUserId(UUID.randomUUID().toString());
		userDetails.setEncryptedPassword(pwdEncoder.encode(userDetails.getPassword()));
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);
		userRepository.save(userEntity);
		UserDto createdDto = modelMapper.map(userEntity, UserDto.class);
		return createdDto;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity entity = userRepository.findByEmail(username);
		if (entity == null) {
			throw new UsernameNotFoundException(username);
		}
		return new User(entity.getEmail(), entity.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
	}

	@Override
	public UserDto getUserDetailsByEmail(String email) {
		UserEntity entity = userRepository.findByEmail(email);
		if (entity == null) {
			throw new UsernameNotFoundException(email);
		}
		ModelMapper modelMapper = new ModelMapper();
		UserDto createdDto = modelMapper.map(entity, UserDto.class);
		return createdDto;
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserEntity entity = userRepository.findByUserId(userId);
		if (entity == null) {
			throw new UsernameNotFoundException(userId);	
		}
		UserDto userDto = new ModelMapper().map(entity, UserDto.class);
		/*
		 * String albumsUrl = String.format(env.getProperty("albums.url"), userId);
		 * ResponseEntity<List<AlbumResponseModel>> albumsListResponse = restTemplate
		 * .exchange(albumsUrl, HttpMethod.GET, null, new
		 * ParameterizedTypeReference<List<AlbumResponseModel>>() { });
		 * List<AlbumResponseModel> albumsList = albumsListResponse.getBody();
		 */
		List<AlbumResponseModel> albumsList = null;
		try {
			albumsList = albumsClient.getAlbums(userId);
		} catch (FeignException e) {
			logger.info(e.getLocalizedMessage());
		}
		userDto.setAlbums(albumsList);
		return userDto;
	}

}
