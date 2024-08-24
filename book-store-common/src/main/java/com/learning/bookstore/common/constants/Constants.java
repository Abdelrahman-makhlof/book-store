package com.learning.bookstore.common.constants;

public class Constants {

    public static String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$";
    public static String USERNAME_REGEX = "^[a-zA-Z0-9_-]{3,30}$";
    public static  String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
}
