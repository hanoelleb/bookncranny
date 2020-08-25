package com.bookstore.service;

import java.util.List;
import java.util.Optional;

import com.bookstore.models.Book;
import com.bookstore.models.CartItem;
import com.bookstore.models.ShoppingCart;
import com.bookstore.models.User;

public interface ICartItemService {
	List<CartItem> findShoppingCart(ShoppingCart shoppingCart);
	
	CartItem updateCartItem(CartItem item);
	
	CartItem addBookToCartItem(Book book, User user, int qty);
	
	Optional<CartItem> findById(Long id);
	
	void removeCartItem(CartItem item);
}
