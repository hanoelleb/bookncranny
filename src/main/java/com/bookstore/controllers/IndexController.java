package com.bookstore.controllers;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.FormSubmitEvent.MethodType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookstore.controllers.security.Role;
import com.bookstore.controllers.security.UserRole;
import com.bookstore.models.Book;
import com.bookstore.models.CartItem;
import com.bookstore.models.Order;
import com.bookstore.models.User;
import com.bookstore.models.UserBilling;
import com.bookstore.models.UserPayment;
import com.bookstore.models.UserShipping;
import com.bookstore.service.impl.BookService;
import com.bookstore.service.impl.CartItemService;
import com.bookstore.service.impl.OrderService;
import com.bookstore.service.impl.UserPaymentService;
import com.bookstore.service.impl.UserSecurityService;
import com.bookstore.service.impl.UserService;
import com.bookstore.service.impl.UserShippingService;
import com.bookstore.utility.SecurityUtility;
import com.bookstore.utility.USConstants;

@Controller
public class IndexController {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserService userService;

	@Autowired
	private UserSecurityService userSecurityService;

	@Autowired
	private BookService bookService;

	@Autowired
	private UserPaymentService userPaymentService;

	@Autowired
	private UserShippingService userShippingService;

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CartItemService cartItemService;

	@RequestMapping("/")
	public String index() {
		return "index";
	}

	@RequestMapping("/account")
	public String account(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName(); // get logged in username
		User user = userService.findByUsername(name);

		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("orderList", user.getOrderList());

		UserShipping userShipping = new UserShipping();
		model.addAttribute("userShipping", userShipping);

		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("listOfShippingAddresses", true);

		List<String> stateList = USConstants.listOfUSStatesCode;
		Collections.sort(stateList);
		model.addAttribute("stateList", stateList);

		if (name != null) {

			model.addAttribute("user", user);
			model.addAttribute("classActiveEdit", true);
		}

		return "account";
	}

	@RequestMapping("/login")
	public String login(Model model) {
		model.addAttribute("classActiveLogin", true);
		return "login";
	}

	@RequestMapping(value = "/sign-up", method = RequestMethod.POST)
	public String signUpPost(HttpServletRequest request, @ModelAttribute("username") String username,
			@ModelAttribute("email") String email, @ModelAttribute("password") String password, Model model)
			throws Exception {

		LOG.info("reached sign_up POST");

		model.addAttribute("classActiveNewAccount", true);
		model.addAttribute("email", email);
		model.addAttribute("username", username);

		/*
		if (userService.findByUsername(username) != null) {
			model.addAttribute("usernameExists", true);

			return "sign-up";
		}

		if (userService.findByEmail(email) != null) {
			model.addAttribute("emailUsed", true);

			return "sign-up";
		}
		*/

		User newUser = new User();
		newUser.setUsername(username);
		newUser.setEmail(email);

		String encoded = SecurityUtility.passwordEncoder().encode(password);

		newUser.setPassword(encoded);

		Role role = new Role();
		role.setRoleId(1);
		role.setName("ROLE_USER");

		Set<UserRole> userRoles = new HashSet<>();
		userRoles.add(new UserRole(newUser, role));
		userService.createUser(newUser, userRoles);

		model.addAttribute("user", newUser);

		return "account";
	}

	@RequestMapping("/sign-up")
	public String signUp(Model model) {
		LOG.info("reached sign_up GET");
		return "sign-up";
	}

	@RequestMapping("/forgetPassword")
	public String forgetPassword(Model model) {
		model.addAttribute("classActiveForgetPassword", true);
		return "account";
	}

	@RequestMapping("/bookshelf")
	public String bookshelf(Model model) {
		List<Book> bookList = bookService.findAll();

		model.addAttribute("bookList", bookList);

		return "bookshelf";
	}

	@RequestMapping("/book/{id}")
	public String book(@PathVariable("id") Long id, Model model, Principal principal) {

		LOG.info("ID: " + id);

		if (principal != null) {
			String username = principal.getName();
			User user = userService.findByUsername(username);
			model.addAttribute("user", user);
		}

		Optional<Book> find = bookService.findOne(id);

		if (!find.isPresent()) {
			model.addAttribute("empty", true);
		} else {
			Book book = find.get();
			model.addAttribute("book", book);
		}

		List<Integer> qtys = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

		model.addAttribute("qtyList", qtys);
		model.addAttribute("qty", 1);

		return "book-detail";
	}

	@RequestMapping(value = "/update-info", method = RequestMethod.POST)
	public String updateInfo(@ModelAttribute("user") User user, @ModelAttribute("newPassword") String newPassword,
			Model model) {
		User current = userService.findById(user.getId());
		/*
		 * if (userService.findByEmail(user.getEmail())!=null) {
		 * System.out.println("flag7");
		 * if(userService.findByEmail(user.getEmail()).getId() != current.getId()) {
		 * model.addAttribute("emailExists", true); return "account"; } }
		 * 
		 * System.out.println("flag6");
		 * 
		 * if (userService.findByUsername(user.getUsername())!=null) {
		 * if(userService.findByUsername(user.getUsername()).getId() != current.getId())
		 * { model.addAttribute("usernameExists", true); return "account"; } }
		 * 
		 * System.out.println("flag5");
		 */
//		update password
		if (newPassword != null && !newPassword.isEmpty() && !newPassword.equals("")) {
			BCryptPasswordEncoder passwordEncoder = SecurityUtility.passwordEncoder();
			String dbPassword = current.getPassword();
			if (passwordEncoder.matches(user.getPassword(), dbPassword)) {
				current.setPassword(passwordEncoder.encode(newPassword));
			} else {
				model.addAttribute("incorrectPassword", true);

				return "account";
			}
		}

		current.setFirstName(user.getFirstName());
		current.setLastName(user.getLastName());
		current.setUsername(user.getUsername());
		current.setEmail(user.getEmail());

		userService.save(current);

		model.addAttribute("updateSuccess", true);
		model.addAttribute("user", current);
		model.addAttribute("classActiveEdit", true);

		UserDetails userDetails = userSecurityService.loadUserByUsername(current.getUsername());

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);

		return "account";
	}

	@RequestMapping("/card-list")
	public String cardList(Model model, Principal principal, HttpServletRequest request) {
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("orderList", user.getOrderList());

		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("listOfShippingAddresses", true);
		model.addAttribute("classActiveBilling", true);

		return "account";
	}

	@RequestMapping("/card/add")
	public String addCard(Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);

		UserBilling userBilling = new UserBilling();
		UserPayment userPayment = new UserPayment();

		model.addAttribute("userBilling", userBilling);
		model.addAttribute("userPayment", userPayment);

		List<String> stateList = USConstants.listOfUSStatesCode;
		model.addAttribute("stateList", stateList);

		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("orderList", user.getOrderList());

		model.addAttribute("classActiveBilling", true);
		model.addAttribute("addNewCreditCard", true);

		return "account";
	}

	@RequestMapping(value = "/card/add", method = RequestMethod.POST)
	public String createCard(@ModelAttribute("userPayment") UserPayment userPayment,
			@ModelAttribute("userBilling") UserBilling userBilling, Principal principal, Model model) {
		User user = userService.findByUsername(principal.getName());
		userService.updateUserBilling(userBilling, userPayment, user);

		model.addAttribute("user", user);
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("orderList", user.getOrderList());
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveBilling", true);
		model.addAttribute("listOfShippingAddresses", true);

		return "account";
	}

	@RequestMapping(value = "/card/edit")
	public String editCard(@RequestParam("id") Long creditCardId, Principal principal, Model model) {
		User user = userService.findByUsername(principal.getName());
		Optional<UserPayment> find = userPaymentService.findById(creditCardId);

		if (!find.isPresent()) {
			return "bad-request";
		}

		UserPayment userPayment = find.get();

		if (user.getId() != userPayment.getUser().getId()) {
			return "bad-request";
		} else {
			model.addAttribute("user", user);

			model.addAttribute("userPayment", userPayment);
			model.addAttribute("userBilling", userPayment.getUserBilling());

			List<String> stateList = USConstants.listOfUSStatesCode;
			model.addAttribute("stateList", stateList);

			model.addAttribute("userPaymentList", user.getUserPaymentList());
			model.addAttribute("userShippingList", user.getUserShippingList());
			model.addAttribute("orderList", user.getOrderList());

			model.addAttribute("addNewCreditCard", true);
			model.addAttribute("classActiveBilling", true);
			model.addAttribute("listOfShippingAddresses", true);

			return "account";
		}
	}

	@RequestMapping(value = "/card/remove")
	public String removeCard(@ModelAttribute("id") Long creditCardId, Principal principal, Model model) {
		User user = userService.findByUsername(principal.getName());
		Optional<UserPayment> find = userPaymentService.findById(creditCardId);

		if (!find.isPresent()) {
			return "bad-request";
		}

		UserPayment userPayment = find.get();

		if (user.getId() != userPayment.getUser().getId()) {
			return "bad-request";
		} else {
			model.addAttribute("user", user);
			userPaymentService.removeById(creditCardId);

			model.addAttribute("userPayment", userPayment);
			model.addAttribute("userBilling", userPayment.getUserBilling());

			List<String> stateList = USConstants.listOfUSStatesCode;
			model.addAttribute("stateList", stateList);

			model.addAttribute("userPaymentList", user.getUserPaymentList());
			model.addAttribute("userShippingList", user.getUserShippingList());
			model.addAttribute("orderList", user.getOrderList());

			model.addAttribute("listOfCreditCards", true);
			model.addAttribute("classActiveBilling", true);
			model.addAttribute("listOfShippingAddresses", true);

			return "account";
		}
	}

	@RequestMapping(value = "/set-default-payment", method = RequestMethod.POST)
	public String setDefaultPayment(@ModelAttribute("defaultUserPaymentId") Long defaultUserPaymentId,
			Principal principal, Model model) {

		User user = userService.findByUsername(principal.getName());
		userService.setUserDefaultPayment(defaultUserPaymentId, user);

		model.addAttribute("user", user);

		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveBilling", true);
		model.addAttribute("listOfShippingAddresses", true);

		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("orderList", user.getOrderList());

		return "account";
	}

	@RequestMapping("/shipping-list")
	public String shippingList(Model model, Principal principal, HttpServletRequest request) {
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("orderList", user.getOrderList());
		model.addAttribute("listOfShippingAddresses", true);
		model.addAttribute("classActiveShipping", true);
		

		return "account";
	}

	@RequestMapping("/shipping/add")
	public String addShipping(Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);

		model.addAttribute("addNewShippingAddress", true);
		model.addAttribute("classActiveShipping", true);

		UserShipping userShipping = new UserShipping();
		model.addAttribute("userShipping", userShipping);

		List<String> stateList = USConstants.listOfUSStatesCode;
		model.addAttribute("stateList", stateList);

		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("orderList", user.getOrderList());
		model.addAttribute("listOfCreditCards", true);

		return "account";
	}

	@RequestMapping(value = "/shipping/add", method = RequestMethod.POST)
	public String createShipping(@ModelAttribute("userShipping") UserShipping userShipping, Principal principal,
			Model model) {
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		userService.updateUserShipping(userShipping, user);

		model.addAttribute("classActiveShipping", true);

		List<String> stateList = USConstants.listOfUSStatesCode;
		model.addAttribute("stateList", stateList);

		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("listOfShippingAddresses", true);

		return "account";
	}

	@RequestMapping(value = "/shipping/edit")
	public String editShipping(@RequestParam("id") Long shippingId, Principal principal, Model model) {
		User user = userService.findByUsername(principal.getName());
		Optional<UserShipping> find = userShippingService.findById(shippingId);

		if (!find.isPresent()) {
			return "bad-request";
		}

		UserShipping userShipping = find.get();

		if (user.getId() != userShipping.getUser().getId()) {
			return "bad-request";
		} else {
			model.addAttribute("user", user);

			model.addAttribute("userShipping", userShipping);

			List<String> stateList = USConstants.listOfUSStatesCode;
			model.addAttribute("stateList", stateList);

			model.addAttribute("userShippingList", user.getUserShippingList());

			model.addAttribute("addNewShippingAddress", true);
			model.addAttribute("classActiveShipping", true);
			model.addAttribute("listOfCreditCards", true);

			return "account";
		}
	}

	@RequestMapping(value = "/shipping/remove")
	public String removeShipping(@ModelAttribute("id") Long shippingId, Principal principal, Model model) {
		User user = userService.findByUsername(principal.getName());
		Optional<UserShipping> find = userShippingService.findById(shippingId);

		if (!find.isPresent()) {
			return "bad-request";
		}

		UserShipping userShipping = find.get();

		if (user.getId() != userShipping.getUser().getId()) {
			return "bad-request";
		} else {
			model.addAttribute("user", user);
			userShippingService.removeById(shippingId);

			model.addAttribute("userShipping", userShipping);

			model.addAttribute("userPaymentList", user.getUserPaymentList());
			model.addAttribute("userShippingList", user.getUserShippingList());
			model.addAttribute("orderList", user.getOrderList());

			model.addAttribute("listOfShippingAddresses", true);
			model.addAttribute("classActiveShipping", true);
			;

			return "account";
		}
	}

	@RequestMapping(value = "/set-default-shipping", method = RequestMethod.POST)
	public String setDefaultShipping(@ModelAttribute("defaultUserShippingAddressId") Long defaultShippingId,
			Principal principal, Model model) {

		User user = userService.findByUsername(principal.getName());
		userService.setUserDefaultShipping(defaultShippingId, user);

		model.addAttribute("user", user);

		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveShipping", true);
		model.addAttribute("listOfShippingAddresses", true);

		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());
		model.addAttribute("orderList", user.getOrderList());

		return "account";
	}

	@RequestMapping("/order-details")
	public String orderDetails(@RequestParam("id") Long orderId, Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		Order order = orderService.findOne(orderId);

		if (order.getUser().getId() != user.getId()) {
			return "bad-request";
		} else {
			List<CartItem> items = cartItemService.findByOrder(order);
			System.out.println(items.size());
			model.addAttribute("cartItemList", items);
			
			model.addAttribute("user", user);
			model.addAttribute("order", order);
			
			model.addAttribute("userPaymentList", user.getUserPaymentList());
			model.addAttribute("userShippingList", user.getUserShippingList());
			model.addAttribute("orderList", user.getOrderList());

			UserShipping userShipping = new UserShipping();
			model.addAttribute("userShipping", userShipping);
			
			List<String> stateList = USConstants.listOfUSStatesCode;
			model.addAttribute("stateList", stateList);

			model.addAttribute("addNewShippingAddress", true);
			model.addAttribute("classActiveOrders", true);
			model.addAttribute("listOfCreditCards", true);
			model.addAttribute("displayOrderDetail", true);


			return "account";
		}
	}
}
