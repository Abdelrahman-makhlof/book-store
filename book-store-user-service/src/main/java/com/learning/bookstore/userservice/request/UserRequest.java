package com.learning.bookstore.userservice.request;

import com.learning.bookstore.common.interfaces.ValidEmail;
import com.learning.bookstore.common.interfaces.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequest {

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
