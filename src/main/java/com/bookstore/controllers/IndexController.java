package com.bookstore.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
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
	public String signUp() {
		return "sign-up";
	}
}
