package com.bookstore.service;

import com.bookstore.models.BillingAddress;
import com.bookstore.models.Order;
import com.bookstore.models.Payment;
import com.bookstore.models.ShippingAddress;
import com.bookstore.models.ShoppingCart;
import com.bookstore.models.User;

public interface IOrderService {
	Order createOrder(ShoppingCart shoppingCart, ShippingAddress shippingAddress, BillingAddress billingAddress, 
			Payment payment, String shippingMethod, User user);
	
	Order findOne(Long id);
}
