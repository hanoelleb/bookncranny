package com.bookstore.service.impl;

import org.springframework.stereotype.Service;

import com.bookstore.models.Payment;
import com.bookstore.models.UserPayment;
import com.bookstore.service.IPaymentService;

@Service
public class PaymentService implements IPaymentService {

	@Override
	public Payment setByUserPayment(UserPayment userPayment, Payment payment) {
		payment.setType(userPayment.getType());
		payment.setCardName(userPayment.getCardName());
		payment.setCardNumber(userPayment.getCardNumber());
		payment.setHolderName(userPayment.getHolderName());
		payment.setCvc(userPayment.getCvc());
		payment.setExpiryMonth(userPayment.getExpiryMonth());
		payment.setExpiryYear(userPayment.getExpiryYear());

		return payment;
	}

}
