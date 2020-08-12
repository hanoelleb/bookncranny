package com.bookstore.service;

import com.bookstore.controllers.security.PasswordResetToken;
import com.bookstore.models.User;

public interface IUserService {
	PasswordResetToken getPasswordResetToken(final String otken);
	
	void createPasswordResetTokenForUser(final User user, final String token);
}
