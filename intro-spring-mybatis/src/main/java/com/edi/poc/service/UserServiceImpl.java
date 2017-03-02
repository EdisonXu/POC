package com.edi.poc.service;

import java.util.List;

import com.edi.poc.domain.User;
import com.edi.poc.mapper.UserMapper;

public class UserServiceImpl implements UserService {

	private UserMapper userMapper;
	
	public void add(User user) {
		userMapper.saveUser(user);
	}

	public void update(User user) {
		userMapper.updateUser(user);
	}

	public void delete(User user) {
		userMapper.deleteUser(user);
	}

	public List<User> findAll() {
		return userMapper.getAllUsers();
	}

	public UserMapper getUserMapper() {
		return userMapper;
	}

	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	
}
