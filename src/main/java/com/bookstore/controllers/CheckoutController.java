package com.bookstore.controllers;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookstore.models.BillingAddress;
import com.bookstore.models.CartItem;
import com.bookstore.models.Payment;
import com.bookstore.models.ShippingAddress;
import com.bookstore.models.ShoppingCart;
import com.bookstore.models.User;
import com.bookstore.models.UserPayment;
import com.bookstore.models.UserShipping;
import com.bookstore.service.impl.BillingAddressService;
import com.bookstore.service.impl.CartItemService;
import com.bookstore.service.impl.PaymentService;
import com.bookstore.service.impl.ShippingAddressService;
import com.bookstore.service.impl.UserService;
import com.bookstore.utility.USConstants;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartItemService cartItemService;
	
	@Autowired
	private ShippingAddressService shippingAddressService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private BillingAddressService billingAddressService;
	
	private ShippingAddress shippingAddress = new ShippingAddress();
	private BillingAddress billingAddress = new BillingAddress();
	private Payment payment = new Payment();
	
	@RequestMapping("/view")
	public String checkoutPage(@RequestParam("id") Long cartId,
			@RequestParam(value="missingRequiredField", required=false) boolean missingRequiredField, 
			Model model, Principal principal) {
		
		User user = userService.findByUsername(principal.getName());
		
		if (cartId != user.getShoppingCart().getId())
			return "bad-request";
		
		List<CartItem> items = cartItemService.findShoppingCart(user.getShoppingCart());
		
		if (items.size() == 0) {
			model.addAttribute("emptyCart", true);
			return "forward:/cart/view";
		}
		
		for (CartItem item : items) {
			if (item.getBook().getInStockNumber() < item.getQty()) {
				model.addAttribute("notEnoughStock", true);
				return "forward:/cart/view";
			}
		}
		
		List<UserShipping> userShippingList = user.getUserShippingList();
		model.addAttribute("shippingAddressList", userShippingList);
		
		if (userShippingList.size() == 0) {
			model.addAttribute("emptyShippingList", true);
		} else {
			model.addAttribute("emptyShippingList", false);
		}
		
		for (UserShipping us : userShippingList) {
			if (us.isUserShippingDefault())
				shippingAddressService.setByUserShipping(us, shippingAddress);
		}
		
		List<UserPayment> userPayments = user.getUserPaymentList();
		model.addAttribute("userPaymentList", userPayments);
		
		if (userPayments.size() == 0) {
			model.addAttribute("emptyPaymentList", true);
		} else {
			model.addAttribute("emptyPaymentList", false);
		}
		
		for (UserPayment up: userPayments) {
			if (up.isDefaultPayment())
				paymentService.setByUserPayment(up, payment);
				billingAddressService.setByUserBilling(up.getUserBilling(), billingAddress);
		}
		
		ShoppingCart shoppingCart = user.getShoppingCart();
		
		model.addAttribute("shippingAddress", shippingAddress);
		model.addAttribute("userPayment", payment);
		model.addAttribute("billingAddress", billingAddress);
		
		System.out.println("\n" + items.size() + "\n");
		
		model.addAttribute("cartItemList", items);
		model.addAttribute("shoppingCart", shoppingCart);
		
		List<String> stateList = USConstants.listOfUSStatesCode;
		Collections.sort(stateList);
		model.addAttribute("stateList", stateList);
		
		model.addAttribute("classActiveShipping", true);
		
		if (missingRequiredField)
			model.addAttribute("missingRequiredField", true);
		
		return "checkout";
	}
}
