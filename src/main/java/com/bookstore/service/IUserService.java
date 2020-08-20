package com.bookstore.service;

import java.util.Set;

import com.bookstore.controllers.security.PasswordResetToken;
import com.bookstore.controllers.security.UserRole;
import com.bookstore.models.User;
import com.bookstore.models.UserBilling;
import com.bookstore.models.UserPayment;

public interface IUserService {
	PasswordResetToken getPasswordResetToken(final String otken);
	
	void createPasswordResetTokenForUser(final User user, final String token);
	
	User findByUsername(String username);
	
	User findByEmail(String email);
	
	User createUser(User user, Set<UserRole> userRoles) throws Exception;
	
	User save(User user);
	
	void updateUserBilling(UserBilling userBilling, UserPayment userPayment, User user);
	
	void setUserDefaultPayment(Long id, User user);
}
