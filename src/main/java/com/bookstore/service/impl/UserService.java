package com.bookstore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.controllers.security.PasswordResetToken;
import com.bookstore.models.User;
import com.bookstore.repository.PasswordTokenResetRepository;
import com.bookstore.service.IUserService;

@Service
public class UserService implements IUserService {

	@Autowired
	private PasswordTokenResetRepository passwordTokenResetRepository;
	
	@Override
	public PasswordResetToken getPasswordResetToken(String token) {
		return passwordTokenResetRepository.findByToken(token);
	}

	@Override
	public void createPasswordResetTokenForUser(User user, String token) {
		final PasswordResetToken myToken = new PasswordResetToken(token, user);
		passwordTokenResetRepository.save(myToken);
	}

}
