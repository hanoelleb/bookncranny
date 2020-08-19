package com.bookstore.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.models.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.IBookService;

@Service
public class BookService implements IBookService {

	@Autowired
	BookRepository bookRepository;
	
	@Override
	public List<Book> findAll() {
		System.out.println(((List<Book>) bookRepository.findAll()).toString());
		return (List<Book>) bookRepository.findAll();
	}

}
