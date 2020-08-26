package com.bookstore.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.models.Book;
import com.bookstore.models.BookToCartItem;
import com.bookstore.models.CartItem;
import com.bookstore.models.Order;
import com.bookstore.models.ShoppingCart;
import com.bookstore.models.User;
import com.bookstore.repository.BookToCartItemRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.service.ICartItemService;

@Service
public class CartItemService implements ICartItemService {

	@Autowired
	CartItemRepository cartItemRepository;
	
	@Autowired
	BookToCartItemRepository bookToCartItemRepository;
	
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

	@Override
	public CartItem addBookToCartItem(Book book, User user, int qty) {
		List<CartItem> cartItemList = findShoppingCart(user.getShoppingCart());
		
		for (CartItem item : cartItemList) {
			if (book.getId() == item.getBook().getId()) {
				item.setQty(item.getQty() + qty);
				item.setSubtotal(new BigDecimal(item.getBook().getOurPrice()).multiply(new BigDecimal(item.getQty())));
				cartItemRepository.save(item);
				return item;
			}
		}
		
		
		
		CartItem cartItem = new CartItem();
		cartItem.setShoppingCart(user.getShoppingCart());
		cartItem.setBook(book);
		cartItem.setQty(qty);
		cartItem.setSubtotal(new BigDecimal(cartItem.getBook().getOurPrice()).multiply(new BigDecimal(cartItem.getQty())));
		
		cartItemRepository.save(cartItem);
		
		BookToCartItem bookItem = new BookToCartItem();
		bookItem.setBook(book);
		bookItem.setCartItem(cartItem);
		bookToCartItemRepository.save(bookItem);
		
		return cartItem;
	}

	@Override
	public Optional<CartItem> findById(Long id) {
		return cartItemRepository.findById(id);
	}

	@Override
	public void removeCartItem(CartItem item) {
		List<BookToCartItem> books = item.getBookToCartItemList();
		for (BookToCartItem book : books) {
			if (book.getBook() == item.getBook()) {
				bookToCartItemRepository.delete(book);
			}
		}
		cartItemRepository.delete(item);
	}

	@Override
	public CartItem save(CartItem item) {
		return cartItemRepository.save(item);
	}

	@Override
	public List<CartItem> findByOrder(Order order) {
		return cartItemRepository.findByOrder(order);
	}

}
