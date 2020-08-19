package com.bookstore.service;

import java.util.List;

import com.bookstore.models.Book;

public interface IBookService {
	List<Book> findAll();
}
