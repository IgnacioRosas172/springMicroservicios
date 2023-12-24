package com.paymentchain.com.paymentchain.customer.repositories;

import com.paymentchain.com.paymentchain.customer.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {



}