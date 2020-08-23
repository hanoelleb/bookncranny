package com.bookstore.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bookstore.models.CartItem;
import com.bookstore.models.ShoppingCart;
import com.bookstore.models.User;
import com.bookstore.service.impl.CartItemService;
import com.bookstore.service.impl.ShoppingCartService;
import com.bookstore.service.impl.UserService;

@Controller
public class CartController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	CartItemService cartItemService;
	
	@Autowired
	ShoppingCartService shoppingCartService;
	
	@RequestMapping("/cart")
	public String cart(Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		ShoppingCart cart = user.getShoppingCart();
		List<CartItem> cartItemList = cartItemService.findShoppingCart(cart);
		
		shoppingCartService.updateShoppingCart(cart);
		
		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("shoppingCart", cart);
		
		return "shopping-cart";
	}
}
