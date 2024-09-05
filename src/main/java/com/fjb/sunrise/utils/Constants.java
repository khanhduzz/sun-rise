package com.fjb.sunrise.utils;

public class Constants {

    private Constants() {}

    public static final class ErrorCode {

        private ErrorCode() {}

        public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
        public static final String USER_ALREADY_EXISTED = "USER_ALREADY_EXISTED";
        public static final String CATEGORY_NOT_FOUND = "CATEGORY_NOT_FOUND";
        public static final String CATEGORY_ALREADY_EXISTED = "CATEGORY_ALREADY_EXISTED";
    }

    public static final class PageableConstant {

        private PageableConstant() {}

        public static final String DEFAULT_PAGE_SIZE = "10";
        public static final String DEFAULT_PAGE_NUMBER = "0";
    }

    public static final class ApiConstant {

        private ApiConstant() {}

        public static final String HEALTH_URL = "/health";

        public static final String CATEGORY_INDEX = "category/index";
        public static final String CATEGORY_REDIRECT = "redirect:/category";
      
        public static final String AUTH_REDIRECT_LOGIN = "redirect:/auth/login";
        public static final String AUTH_VIEW = "/auth/loginAndRegister";
        public static final String REGISTER_ATTRIBUTE = "register";
        public static final String LOGIN_ATTRIBUTE = "login";
        public static final String ERROR_MESSAGE = "errorMessage";

        public static final String CODE_200 = "200";
        public static final String OK = "Ok";
        public static final String CODE_404 = "404";
        public static final String NOT_FOUND = "Not found";
        public static final String CODE_201 = "201";
        public static final String CREATED = "Created";
        public static final String CODE_400 = "400";
        public static final String BAD_REQUEST = "Bad request";
        public static final String CODE_204 = "204";
        public static final String NO_CONTENT = "No content";
    }
}
