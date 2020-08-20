package com.bookstore.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.models.UserShipping;
import com.bookstore.repository.UserShippingRepository;
import com.bookstore.service.IUserShippingService;

@Service
public class UserShippingService implements IUserShippingService {

	@Autowired
	UserShippingRepository userShippingRepository;	
	
	@Override
	public Optional<UserShipping> findById(Long id) {
		return userShippingRepository.findById(id);
	}

	@Override
	public void removeById(Long id) {
		userShippingRepository.deleteById(id);
	}

}
