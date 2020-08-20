package com.bookstore.service;

import java.util.Optional;

import com.bookstore.models.UserPayment;

public interface IUserPaymentService {
	Optional<UserPayment> findById(Long id);
	
	void removeById(Long id);
}
