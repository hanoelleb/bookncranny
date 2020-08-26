package com.bookstore.controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookstore.models.BillingAddress;
import com.bookstore.models.CartItem;
import com.bookstore.models.Order;
import com.bookstore.models.Payment;
import com.bookstore.models.ShippingAddress;
import com.bookstore.models.ShoppingCart;
import com.bookstore.models.User;
import com.bookstore.models.UserBilling;
import com.bookstore.models.UserPayment;
import com.bookstore.models.UserShipping;
import com.bookstore.service.impl.BillingAddressService;
import com.bookstore.service.impl.CartItemService;
import com.bookstore.service.impl.OrderService;
import com.bookstore.service.impl.PaymentService;
import com.bookstore.service.impl.ShippingAddressService;
import com.bookstore.service.impl.ShoppingCartService;
import com.bookstore.service.impl.UserPaymentService;
import com.bookstore.service.impl.UserService;
import com.bookstore.service.impl.UserShippingService;
import com.bookstore.utility.USConstants;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartItemService cartItemService;
	
	@Autowired
	private ShoppingCartService shoppingCartService;
	
	@Autowired
	private ShippingAddressService shippingAddressService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private BillingAddressService billingAddressService;
	
	@Autowired
	private UserShippingService userShippingService;
	
	@Autowired
	private UserPaymentService userPaymentService;
	
	@Autowired
	private OrderService orderService;
	
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
	
	@RequestMapping(value="/view", method=RequestMethod.POST)
	public String postCheckOut(
				@ModelAttribute("shippingAddress") ShippingAddress shippingAddress,
				@ModelAttribute("billingAddress") BillingAddress billingAddress,
				@ModelAttribute("payment") Payment payment,
				@ModelAttribute("sameAsShipping") String sameAsShipping, 
				@ModelAttribute("shippingMethod") String shippingMethod, 
				Principal principal,
				Model model
			) {
		
		ShoppingCart shoppingCart = userService.findByUsername(principal.getName()).getShoppingCart();
		List<CartItem> items = cartItemService.findShoppingCart(shoppingCart);
		model.addAttribute("cartItemList", items);
		
		if (sameAsShipping.equals("true")) {
			billingAddress.setBillingAddressName(shippingAddress.getShippingAddressName());
			billingAddress.setBillingAddressStreet1(shippingAddress.getShippingAddressStreet1());
			billingAddress.setBillingAddressStreet2(shippingAddress.getShippingAddressStreet2());
			billingAddress.setBillingAddressCity(shippingAddress.getShippingAddressCity());
			billingAddress.setBillingAddressState(shippingAddress.getShippingAddressState());
			billingAddress.setBillingAddressCountry(shippingAddress.getShippingAddressCountry());
			billingAddress.setBillingAddressZipcode(shippingAddress.getShippingAddressZipcode());
		}
		
		if (shippingAddress.getShippingAddressStreet1().isEmpty() 
				|| shippingAddress.getShippingAddressCity().isEmpty() 
				|| shippingAddress.getShippingAddressState().isEmpty() 
				|| shippingAddress.getShippingAddressName().isEmpty()
				|| shippingAddress.getShippingAddressZipcode().isEmpty()
				|| payment.getCardNumber().isEmpty()
				|| payment.getCvc() == 0
				|| billingAddress.getBillingAddressStreet1().isEmpty()
				|| billingAddress.getBillingAddressCity().isEmpty() 
				|| billingAddress.getBillingAddressState().isEmpty() 
				|| billingAddress.getBillingAddressName().isEmpty()
				|| billingAddress.getBillingAddressZipcode().isEmpty()
			) {
			return "redirect:/checkout/view?id=" + shoppingCart.getId() + "&missingRequiredField=true";
		}
			
		User user = userService.findByUsername(principal.getName());
		
		Order order = orderService.createOrder(shoppingCart, shippingAddress, billingAddress, payment, shippingMethod, user);
		
		shoppingCartService.clearShoppingCart(shoppingCart);
		
		LocalDate today = LocalDate.now();
		LocalDate estimated;
		
		if (shippingMethod.equals("groundShipping")) {
			estimated = today.plusDays(5);
		} else {
			estimated = today.plusDays(3);
		}
		
		model.addAttribute("estimatedDeliveryDate", estimated);
		
		return "order-submit";
	}
	
	@RequestMapping("/set-shipping")
	public String setShipping(@RequestParam("id") Long shippingId,
			Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		
		UserShipping userShipping = userShippingService.findById(shippingId).get();
		
		if (userShipping.getUser().getId() != user.getId())
			return "bad-request";
		else {
			shippingAddressService.setByUserShipping(userShipping, shippingAddress);
			List<CartItem> items = cartItemService.findShoppingCart(user.getShoppingCart());
			
			model.addAttribute("shippingAddress", shippingAddress);
			model.addAttribute("userPayment", payment);
			model.addAttribute("billingAddress", billingAddress);
			model.addAttribute("cartItemList", items);
			model.addAttribute("shoppingCart", user.getShoppingCart());
			
			List<String> stateList = USConstants.listOfUSStatesCode;
			Collections.sort(stateList);
			model.addAttribute("stateList", stateList);
			
			List<UserPayment> userPayments = user.getUserPaymentList();
			model.addAttribute("userPaymentList", userPayments);
			if (userPayments.size() == 0) {
				model.addAttribute("emptyPaymentList", true);
			} else {
				model.addAttribute("emptyPaymentList", false);
			}
			
			List<UserShipping> userShippingList = user.getUserShippingList();

			model.addAttribute("emptyShippingList", false);

			model.addAttribute("shippingAddressList", userShippingList);
			
			model.addAttribute("shippingAddress", shippingAddress);
			
			model.addAttribute("classActiveShipping", true);
		}
		
		
		
		return "checkout";
	}
	
	@RequestMapping("/set-payment")
	public String setPayment(@RequestParam("id") Long paymentId,
			Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		UserPayment userPayment = userPaymentService.findById(paymentId).get();		
		UserBilling userBilling = userPayment.getUserBilling();
		
		if (userPayment.getUser().getId() != user.getId())
			return "bad-request";
		else {
			paymentService.setByUserPayment(userPayment, payment);
			List<CartItem> items = cartItemService.findShoppingCart(user.getShoppingCart());
			
			billingAddressService.setByUserBilling(userBilling, billingAddress);

			model.addAttribute("shippingAddress", shippingAddress);
			model.addAttribute("userPayment", payment);
			model.addAttribute("billingAddress", billingAddress);
			model.addAttribute("cartItemList", items);
			model.addAttribute("shoppingCart", user.getShoppingCart());
			
			List<String> stateList = USConstants.listOfUSStatesCode;
			Collections.sort(stateList);
			model.addAttribute("stateList", stateList);
			
			List<UserPayment> userPayments = user.getUserPaymentList();
			model.addAttribute("userPaymentList", userPayments);

			model.addAttribute("emptyPaymentList", false);
			
			List<UserShipping> userShippingList = user.getUserShippingList();
			if (userShippingList.size() == 0) {
				model.addAttribute("emptyShippingList", true);
			} else {
				model.addAttribute("emptyShippingList", false);
			}
			
			model.addAttribute("shippingAddress", shippingAddress);
			
			model.addAttribute("classActivePayment", true);
		}
		
		return "checkout";
	}
 }
