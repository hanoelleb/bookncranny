package com.bookstore;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bookstore.controllers.security.Role;
import com.bookstore.controllers.security.UserRole;
import com.bookstore.models.User;
import com.bookstore.service.impl.UserService;
import com.bookstore.utility.SecurityUtility;

@SpringBootApplication
public class BookstoreApplication implements CommandLineRunner {
	
	@Autowired
	private UserService userService;

	
	@Override
	public void run(String... args) throws Exception {
		User demo = new User();
		demo.setUsername("demo");
		demo.setFirstName("Example");
		demo.setLastName("User");
		demo.setEmail("test@test.com");
		demo.setPassword(SecurityUtility.passwordEncoder().encode("test"));
		
		Set<UserRole> userRoles = new HashSet<>();
		Role role = new Role();
		role.setRoleId(1);
		role.setName("ROLE_USER");
		userRoles.add(new UserRole(demo, role));
		
		userService.createUser(demo,  userRoles);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(BookstoreApplication.class, args);
	}

	
}
