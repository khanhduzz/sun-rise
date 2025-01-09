package com.fjb.sunrise.utils;

public class Constants {

    private Constants() {}

    public static final class ErrorCode {

        private ErrorCode() {}

        public static final String ERROR = "error";
        public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
        public static final String USER_ALREADY_EXISTED = "USER_ALREADY_EXISTED";
        public static final String CATEGORY_NOT_FOUND = "CATEGORY_NOT_FOUND";
        public static final String CATEGORY_ALREADY_EXISTED = "CATEGORY_ALREADY_EXISTED";
        public static final String ACCOUNT_HAS_BEEN_BLOCKED = "ACCOUNT_HAS_BEEN_BLOCKED";
    }

    public static final class PageableConstant {

        private PageableConstant() {}

        public static final String DEFAULT_PAGE_SIZE = "10";
        public static final String DEFAULT_PAGE_NUMBER = "0";
    }

    public static final class ApiConstant {

        private ApiConstant() {}

        public static final String HEALTH_URL = "/health";
        public static final String REDIRECT_HEALTH_URL = "redirect:/health";

        public static final String CATEGORY_INDEX = "category/index";
        public static final String CATEGORY_REDIRECT = "redirect:/category/index";
        public static final String HOME_REDIRECT = "redirect:/transaction/create";


        public static final String TRANSACTION_INDEX = "transaction/index";
        public static final String CATEGORIES = "categories";
        public static final String USERS = "users";
        public static final String STATISTIC = "statistic";

        public static final String AUTH_REDIRECT_LOGIN = "redirect:/auth/login";
        public static final String AUTH_VIEW = "auth/loginAndRegister";
        public static final String REGISTER_OBJECT = "register";
        public static final String LOGIN_OBJECT = "login";
        public static final String ERROR_MESSAGE_OBJECT = "errorMessage";
        public static final String EMAIL_OBJECT = "email";
        public static final String NEW_PASS_WORD_OBJECT = "newPassword";
        public static final String VERIFICATION_BY_EMAIL_VIEW = "auth/verificationByEmail";
        public static final String CHANGE_PASS_WORD_VIEW = "auth/changePassword";

        public static final String ADMIN_VIEW = "user/admin-page";
        public static final String ADMIN_ADD_NEW_USER = "user/add-user-by-admin";
        public static final String ADMIN_DETAILS_AND_EDIT = "user/detail-and-edit-by-admin";
        public static final String ADMIN_REDIRECT = "redirect:/user/admin-page";
        public static final String USER_INFORMATION = "user/edit-infor";
        public static final String USER_CHANGE_INFO_SUCCESS = "redirect:/user/edit-infor?message=SuccessInfo";
        public static final String USER_CHANGE_PASS_WORD_SUCCESS = "redirect:/user/edit-infor?message=SuccessPassword";
        public static final String USER_CHANGE_PASS_WORD_FAIL = "redirect:/user/edit-infor";

        public static final String MESSAGE = "message";
        public static final String VALUE = "value";

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
