package com.edi.poc.service;

import java.util.List;

import com.edi.poc.domain.User;

public interface UserService {

	void add(User user);
	void update(User user);
	void delete(User user);
	List<User> findAll();
}
