package com.bookstore.service;

import com.bookstore.models.ShippingAddress;
import com.bookstore.models.UserShipping;

public interface IShippingAddressService {
	ShippingAddress setByUserShipping(UserShipping userShipping, ShippingAddress shippingAddress);
}
