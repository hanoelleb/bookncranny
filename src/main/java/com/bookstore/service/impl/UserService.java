package com.bookstore.service.impl;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.controllers.security.PasswordResetToken;
import com.bookstore.controllers.security.UserRole;
import com.bookstore.models.User;
import com.bookstore.models.UserBilling;
import com.bookstore.models.UserPayment;
import com.bookstore.models.UserShipping;
import com.bookstore.repository.PasswordTokenResetRepository;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.UserPaymentRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.repository.UserShippingRepository;
import com.bookstore.service.IUserService;

@Service
public class UserService implements IUserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordTokenResetRepository passwordTokenResetRepository;
	
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	UserPaymentRepository userPaymentRepository;
	
	@Autowired
	UserShippingRepository userShippingRepository;
	
	@Override
	public PasswordResetToken getPasswordResetToken(String token) {
		return passwordTokenResetRepository.findByToken(token);
	}

	@Override
	public void createPasswordResetTokenForUser(User user, String token) {
		final PasswordResetToken myToken = new PasswordResetToken(token, user);
		passwordTokenResetRepository.save(myToken);
	}

	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	@Override
	public User createUser(User user, Set<UserRole> userRoles) throws Exception {
		User localUser = userRepository.findByUsername(user.getUsername());
		
		if (localUser != null) {
			LOG.info("User already exists. Will not be added");
		} else {
			for (UserRole ur : userRoles ) {
				roleRepository.save(ur.getRole());
			}
			
			user.getUserRoles().addAll(userRoles);
			localUser = userRepository.save(user);
		}
		
		return localUser;
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}
	
	@Override
	public void updateUserBilling(UserBilling userBilling, UserPayment userPayment, User user) {
		userPayment.setUser(user);
		userPayment.setUserBilling(userBilling);
		userPayment.setDefaultPayment(true);
		userBilling.setUserPayment(userPayment);
		user.getUserPaymentList().add(userPayment);
		save(user);
	}


	@Override
	public void updateUserShipping(UserShipping userShipping, User user) {
		userShipping.setUser(user);
		userShipping.setUserShippingDefault(true);
		user.getUserShippingList().add(userShipping);
		save(user);
	}
	
	@Override
	public void setUserDefaultPayment(Long id, User user) {
		List<UserPayment> userPayments = (List<UserPayment>) userPaymentRepository.findAll();
		
		for (UserPayment u : userPayments) {
			if (u.getId() == id) {
				u.setDefaultPayment(true);
				userPaymentRepository.save(u);
			} else {
				u.setDefaultPayment(false);
				userPaymentRepository.save(u);
			}
		}
	}
	
	public void setUserDefaultShipping(Long id, User user) {
		List<UserShipping> userShippings = (List<UserShipping>) userShippingRepository.findAll();
		
		for (UserShipping u : userShippings) {
			if (u.getId() == id) {
				u.setUserShippingDefault(true);
				userShippingRepository.save(u);
			} else {
				u.setUserShippingDefault(false);
				userShippingRepository.save(u);
			}
		}
	}
}
