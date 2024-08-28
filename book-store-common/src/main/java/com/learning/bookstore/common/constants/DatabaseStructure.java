package com.learning.bookstore.common.constants;

public class DatabaseStructure {

    public class CUSTOMER {
        public static String TABLE_NAME = "CUSTOMERS";
        public static String USER_NAME = "USER_NAME";
        public static String FULL_NAME = "FULL_NAME";
        public static String PASSWORD = "PASSWORD";
        public static String EMAIL = "EMAIL";
    }

    public class BOOK {

        public static String TABLE_NAME = "BOOKS";
        public static String ID = "ID";
        public static String TITLE = "TITLE";
        public static String AUTHOR = "AUTHOR";
        public static String PRICE = "PRICE";
        public static String CATEGORY = "CATEGORY_ID";

    }

    public class CATEGORY {

        public static String TABLE_NAME = "CATEGORIES";
        public static String NAME = "NAME";

    }
}
