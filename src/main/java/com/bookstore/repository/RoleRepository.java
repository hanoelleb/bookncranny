package com.bookstore.repository;

import org.springframework.data.repository.CrudRepository;

import com.bookstore.controllers.security.Role;

public interface RoleRepository extends CrudRepository<Role, Long>{
	Role findByName(String name);
}
