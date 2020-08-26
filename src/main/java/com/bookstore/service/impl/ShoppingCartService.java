package com.bookstore.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.models.CartItem;
import com.bookstore.models.ShoppingCart;
import com.bookstore.repository.ShoppingCartRepository;
import com.bookstore.service.IShoppingCartService;

@Service
public class ShoppingCartService implements IShoppingCartService  {

	@Autowired
	ShoppingCartRepository shoppingCartRepository;
	
	@Autowired
	CartItemService cartItemService;
	
	@Override
	public ShoppingCart updateShoppingCart(ShoppingCart shoppingCart) {
		BigDecimal total = new BigDecimal(0);
		List<CartItem> cartItemList = cartItemService.findShoppingCart(shoppingCart);
		
		for (CartItem item : cartItemList) {
			cartItemService.updateCartItem(item);
			total = total.add(item.getSubtotal());
		}
		
		shoppingCart.setTotal(total);
		
		shoppingCartRepository.save(shoppingCart);
		
		return shoppingCart;
	}

	@Override
	public void clearShoppingCart(ShoppingCart shoppingCart) {
		List<CartItem> cartItemList = cartItemService.findShoppingCart(shoppingCart);
		
		for (CartItem item : cartItemList) {
			item.setShoppingCart(null);
			cartItemService.save(item);
		}
		
		shoppingCart.setTotal(new BigDecimal(0));
		
		shoppingCartRepository.save(shoppingCart);
	}

}
