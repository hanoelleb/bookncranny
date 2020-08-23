package com.bookstore.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.models.CartItem;
import com.bookstore.models.ShoppingCart;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.service.ICartItemService;

@Service
public class CartItemService implements ICartItemService {

	@Autowired
	CartItemRepository cartItemRepository;
	
	@Override
	public List<CartItem> findShoppingCart(ShoppingCart shoppingCart) {
		return cartItemRepository.findByShoppingCart(shoppingCart);
	}

	@Override
	public CartItem updateCartItem(CartItem item) {
		BigDecimal bd = new BigDecimal(item.getBook().getOurPrice()).multiply(new BigDecimal(item.getQty()));
		
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		item.setSubtotal(bd);
		
		cartItemRepository.save(item);
		
		return item;
	}

}
