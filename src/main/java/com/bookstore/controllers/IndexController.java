package com.bookstore.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bookstore.controllers.security.Role;
import com.bookstore.controllers.security.UserRole;
import com.bookstore.models.Book;
import com.bookstore.models.User;
import com.bookstore.service.impl.BookService;
import com.bookstore.service.impl.UserSecurityService;
import com.bookstore.service.impl.UserService;
import com.bookstore.utility.SecurityUtility;

@Controller
public class IndexController {
	
	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserService userService;

	@Autowired
	private UserSecurityService userSecurityService;
	
	@Autowired
	private BookService bookService;

	@RequestMapping("/")
	public String index() {
		return "index";
	}

	@RequestMapping("/account")
	public String account(Model model) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String name = auth.getName(); //get logged in username
	    LOG.info("USER " + name + " IS LOGGED IN ");
	    
	    if (name != null) {
	    	User user = userService.findByUsername(name);
	    	model.addAttribute("user", user);
	    }
		
		return "account";
	}

	@RequestMapping("/login")
	public String login(Model model) {
		model.addAttribute("classActiveLogin", true);
		return "login";
	}

	@RequestMapping(value = "/sign-up", method = RequestMethod.POST)
	public String signUpPost(HttpServletRequest request, @ModelAttribute("username") String username,
			@ModelAttribute("email") String email, @ModelAttribute("password") String password, Model model) throws Exception {

		LOG.info("reached sign_up POST");
		
		model.addAttribute("classActiveNewAccount", true);
		model.addAttribute("email", email);
		model.addAttribute("username", username);

		if (userService.findByUsername(username) != null) {
			model.addAttribute("usernameExists", true);

			return "sign-up";
		}

		if (userService.findByEmail(email) != null) {
			model.addAttribute("emailUsed", true);

			return "sign-up";
		}
		
		User newUser = new User();
		newUser.setUsername(username);
		newUser.setEmail(email);
		
		String encoded = SecurityUtility.passwordEncoder().encode(password);
		
		newUser.setPassword(encoded);
		
		Role role = new Role();
		role.setRoleId(1);
		role.setName("ROLE_USER");
		
		Set<UserRole> userRoles = new HashSet<>();
		userRoles.add(new UserRole(newUser,role));
		userService.createUser(newUser, userRoles);
		
		model.addAttribute("user", newUser);
		
		return "account";
	}
	
	@RequestMapping("/sign-up")
	public String signUp(Model model) {
		LOG.info("reached sign_up GET");
		return "sign-up";
	}

	@RequestMapping("/forgetPassword")
	public String forgetPassword(Model model) {
		model.addAttribute("classActiveForgetPassword", true);
		return "account";
	}
	
	@RequestMapping("/bookshelf")
	public String bookshelf(Model model) {
		List<Book> bookList = bookService.findAll();
		
		LOG.info("SIZE: " + bookList.size());
		
		model.addAttribute("bookList", bookList);
		
		return "bookshelf";
	}

}
