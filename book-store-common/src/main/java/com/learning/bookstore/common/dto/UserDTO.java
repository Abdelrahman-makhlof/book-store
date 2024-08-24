package com.learning.bookstore.common.dto;

import com.learning.bookstore.common.interfaces.ValidEmail;
import com.learning.bookstore.common.interfaces.ValidPassword;
import com.learning.bookstore.common.interfaces.ValidUsername;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDTO {

    @NotNull(message = "Enter user name")
    @NotBlank(message = "Enter user name")
    @ValidUsername
    private String username;
    @NotNull(message = "Enter full name")
    @NotBlank(message = "Enter full name")
    private String fullName;
    @ValidPassword
    @NotNull(message = "Enter password")
    private String password;
    @NotNull(message = "Enter email")
    @NotBlank(message = "Enter email")
    @ValidEmail
    private String email;
}
