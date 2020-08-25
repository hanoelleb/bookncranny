package com.bookstore.service;

import com.bookstore.models.Payment;
import com.bookstore.models.UserPayment;

public interface IPaymentService {
	Payment setByUserPayment(UserPayment userPayment, Payment payment);
}
