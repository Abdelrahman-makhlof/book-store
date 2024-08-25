package com.learning.bookstore.userservice.repository;

import com.learning.bookstore.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User getUserByUsername(String username);
    User getUserByEmail(String email);
    void deleteByUsernameOrEmail(String username ,String email);
}
