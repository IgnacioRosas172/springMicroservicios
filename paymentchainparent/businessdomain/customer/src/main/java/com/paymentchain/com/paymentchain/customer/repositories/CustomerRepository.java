package com.paymentchain.com.paymentchain.customer.repositories;

import com.paymentchain.com.paymentchain.customer.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    @Query("SELECT c FROM Customer c Where c.code = ?1")
    public Customer findByCode(String code);
    
    @Query("SELECT c FROM Customer c Where c.lban = ?1")
    public Customer findByAccount(String lban);
    
}