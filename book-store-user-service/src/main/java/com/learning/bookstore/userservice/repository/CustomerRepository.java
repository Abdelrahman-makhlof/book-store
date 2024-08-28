package com.learning.bookstore.userservice.repository;

import com.learning.bookstore.common.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer getCustomerByUsername(String username);
    Customer getCustomerByEmail(String email);
    void deleteByUsernameOrEmail(String username ,String email);
}
