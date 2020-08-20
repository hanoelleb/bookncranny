package com.bookstore.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.models.UserPayment;
import com.bookstore.repository.UserPaymentRepository;
import com.bookstore.service.IUserPaymentService;


@Service
public class UserPaymentService implements IUserPaymentService {

	@Autowired
	UserPaymentRepository userPaymentRepository;	
	
	@Override
	public Optional<UserPayment> findById(Long id) {
		return userPaymentRepository.findById(id);
	}

}
