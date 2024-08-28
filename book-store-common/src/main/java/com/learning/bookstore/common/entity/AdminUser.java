package com.learning.bookstore.common.entity;

import com.learning.bookstore.common.constants.Role;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ADMIN_USERS", uniqueConstraints = {@UniqueConstraint(columnNames = {"EMAIL", "USERNAME"})})
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String email;
    private Role role;
}
