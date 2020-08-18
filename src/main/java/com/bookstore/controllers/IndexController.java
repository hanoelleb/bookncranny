package com.bookstore.controllers;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bookstore.controllers.security.Role;
import com.bookstore.controllers.security.UserRole;
import com.bookstore.models.User;
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
		return "login";
	}
	
	@RequestMapping("/sign-up")
	public String signUp(Model model) {
		LOG.info("reached sign_up GET");
		return "sign-up";
	}


	/*
	 * @RequestMapping("/sign-up") public String signUp(Locale
	 * locale, @RequestParam("token") String token, Model model){
	 * 
	 * PasswordResetToken passToken = userService.getPasswordResetToken(token); if
	 * (passToken == null) { String message = "Invalid Token.";
	 * model.addAttribute("message", message); return "redirect:/badRequest"; }
	 * 
	 * User user = passToken.getUser(); String username = user.getUsername();
	 * 
	 * //Set current logging session to user UserDetails userDetails =
	 * userSecurityService.loadUserByUsername(username);
	 * 
	 * Authentication authentication = new
	 * UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
	 * userDetails.getAuthorities());
	 * 
	 * SecurityContextHolder.getContext().setAuthentication(authentication);
	 * 
	 * model.addAttribute("classActiveEdit", true);
	 * 
	 * return "account"; }
	 */

	@RequestMapping("/forgetPassword")
	public String forgetPassword(Model model) {
		model.addAttribute("classActiveForgetPassword", true);
		return "account";
	}

}
