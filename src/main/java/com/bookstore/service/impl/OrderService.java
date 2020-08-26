package com.bookstore.service.impl;

import org.springframework.stereotype.Service;

import com.bookstore.models.BillingAddress;
import com.bookstore.models.Order;
import com.bookstore.models.Payment;
import com.bookstore.models.ShippingAddress;
import com.bookstore.models.ShoppingCart;
import com.bookstore.models.User;
import com.bookstore.service.IOrderService;

@Service
public class OrderService implements IOrderService {

	@Override
	public Order createOrder(ShoppingCart shoppingCart, ShippingAddress shippingAddress, BillingAddress billingAddress,
			Payment payment, String shippingMethod, User user) {
		// TODO Auto-generated method stub
		return null;
	}

}
