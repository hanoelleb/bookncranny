package com.bookstore.repository;

import org.springframework.data.repository.CrudRepository;

import com.bookstore.models.ShoppingCart;

public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, Long>{

}
