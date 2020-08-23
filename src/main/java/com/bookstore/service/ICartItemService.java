package com.bookstore.service;

import java.util.List;

import com.bookstore.models.CartItem;
import com.bookstore.models.ShoppingCart;

public interface ICartItemService {
	List<CartItem> findShoppingCart(ShoppingCart shoppingCart);
	
	CartItem updateCartItem(CartItem item);
}
