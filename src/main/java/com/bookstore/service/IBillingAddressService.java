package com.bookstore.service;

import com.bookstore.models.BillingAddress;
import com.bookstore.models.UserBilling;

public interface IBillingAddressService {
	BillingAddress setByUserBilling(UserBilling userBilling, BillingAddress billingAddress);
}
