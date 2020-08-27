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
public class BookstoreApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(BookstoreApplication.class, args);
	}

}
