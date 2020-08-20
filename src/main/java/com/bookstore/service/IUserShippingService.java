package com.bookstore.service;

import java.util.Optional;

import com.bookstore.models.UserShipping;

public interface IUserShippingService {
	Optional<UserShipping> findById(Long id);
	
	void removeById(Long id);
}
