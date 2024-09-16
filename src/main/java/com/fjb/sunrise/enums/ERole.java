package com.fjb.sunrise.enums;

public enum ERole {
    ADMIN,
    USER;

    public String toUpperCase() {
        return this.name(); // Trả về tên của enum ở dạng chữ hoa
    }
}
