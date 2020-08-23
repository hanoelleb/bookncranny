package com.bookstore.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bookstore.models.Book;
import com.bookstore.models.CartItem;
import com.bookstore.models.ShoppingCart;
import com.bookstore.models.User;
import com.bookstore.service.impl.BookService;
import com.bookstore.service.impl.CartItemService;
import com.bookstore.service.impl.ShoppingCartService;
import com.bookstore.service.impl.UserService;

import ch.qos.logback.classic.Logger;

@Controller
@RequestMapping("/cart")
public class CartController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	CartItemService cartItemService;
	
	@Autowired
	ShoppingCartService shoppingCartService;
	
	@Autowired
	BookService bookService;
	
	@RequestMapping("/view")
	public String cart(Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		ShoppingCart cart = user.getShoppingCart();
		List<CartItem> cartItemList = cartItemService.findShoppingCart(cart);
		
		shoppingCartService.updateShoppingCart(cart);
		
		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("shoppingCart", cart);
		
		return "shopping-cart";
	}
	
	@RequestMapping("/add")
	public String addBook(@ModelAttribute("book") Book book,
			@ModelAttribute("qty") String qty, 
			Model model, 
			Principal principal) {
		User user = userService.findByUsername(principal.getName());
		Optional<Book> find = bookService.findOne(book.getId());
		
		if (find.isPresent()) {
			book = find.get();
		} else {
			return "bad-request";
		}
		
		if (Integer.parseInt(qty) > book.getInStockNumber()) {
			model.addAttribute("notEnoughStock", true);
			return "forward:/book/" + book.getId();
		}
		
		CartItem cartItem = cartItemService.addBookToCartItem(book, user, Integer.parseInt(qty));
		model.addAttribute("addBookSuccess", true);
		
		return "forward:/book/" + book.getId();
	}
}
