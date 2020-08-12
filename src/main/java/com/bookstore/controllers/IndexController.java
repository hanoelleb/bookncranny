package com.bookstore.controllers;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookstore.controllers.security.PasswordResetToken;
import com.bookstore.models.User;
import com.bookstore.service.impl.UserSecurityService;
import com.bookstore.service.impl.UserService;

@Controller
public class IndexController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserSecurityService userSecurityService;
	
	@RequestMapping("/")
	public String index() {
		return "index";
	}
	
	@RequestMapping("/account")
	public String account() {
		return "account";
	}
	
	@RequestMapping("/login")
	public String login(Model model) {
		model.addAttribute("classActiveLogin", true);
		return "login";
	}
	
	@RequestMapping("/sign-up")
	public String signUp(Locale locale, Model model) {
		/*
		  @RequestParam("token") String token
		  PasswordResetToken passToken = userService.getPasswordResetToken(token);
		
		if (passToken == null) {
			String message = "Invalid Token.";
			model.addAttribute("message", message);
			return "redirect:/badRequest";
		}
		
		User user = passToken.getUser();
		String username = user.getUsername();
		
		UserDetails userDetails = userSecurityService.loadUserByUsername(username);
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		*/
		model.addAttribute("classActiveNewUser", true);
		return "sign-up";
	}
	
	@RequestMapping("/forgetPassword")
	public String forgetPassword(Model model) {
		model.addAttribute("classActiveForgetPassword", true);
		return "account";
	}
	
}
