package com.fjb.sunrise.enums;

public enum EStatus {
    ACTIVE,
    NOT_ACTIVE;

    public String toUpperCase() {
        return this.name(); // Trả về tên của enum ở dạng chữ hoa
    }
}
