package com.bookstore.service.impl;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.models.BillingAddress;
import com.bookstore.models.Book;
import com.bookstore.models.CartItem;
import com.bookstore.models.Order;
import com.bookstore.models.Payment;
import com.bookstore.models.ShippingAddress;
import com.bookstore.models.ShoppingCart;
import com.bookstore.models.User;
import com.bookstore.repository.OrderRepository;
import com.bookstore.service.IOrderService;

@Service
public class OrderService implements IOrderService {

	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	CartItemService cartItemService;
	
	@Override
	public synchronized Order createOrder(ShoppingCart shoppingCart, ShippingAddress shippingAddress, BillingAddress billingAddress,
			Payment payment, String shippingMethod, User user) {
		Order order = new Order();
		order.setPayment(payment);
		order.setOrderStatus("created");
		order.setBillingAddress(billingAddress);
		order.setShippingAddress(shippingAddress);
		order.setShippingMethod(shippingMethod);
		order.setUser(user);
		
		List<CartItem> items = cartItemService.findShoppingCart(shoppingCart);
		
		for (CartItem item: items) {
			Book book = item.getBook();
			book.setInStockNumber(book.getInStockNumber() - item.getQty());
		}
		
		order.setCartItemList(items);
		order.setOrderDate(Calendar.getInstance().getTime());
		order.setOrderTotal(shoppingCart.getTotal());
		
		shippingAddress.setOrder(order);
		billingAddress.setOrder(order);
		payment.setOrder(order);
		
		orderRepository.save(order);
	
		return order;
	}

	@Override
	public Order findOne(Long id) {
		return orderRepository.findById(id).get();
	}

	
}
