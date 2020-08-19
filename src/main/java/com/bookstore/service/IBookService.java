package com.bookstore.service;

import java.util.List;
import java.util.Optional;

import com.bookstore.models.Book;

public interface IBookService {
	List<Book> findAll();
	
	Optional<Book> findOne(Long id);
}
