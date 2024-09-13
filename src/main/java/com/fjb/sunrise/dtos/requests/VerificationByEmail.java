package com.fjb.sunrise.dtos.requests;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationByEmail {
    private String email;
    private LocalDateTime requestTime;

    public VerificationByEmail(String email, LocalDateTime requestTime) {
        this.email = email;
        this.requestTime = requestTime;
    }

    @Override
    public String toString() {
        return email + "///" + requestTime.toString();
    }

    public static VerificationByEmail fromString(String string) {
        String[] split = string.split("///");
        if (split.length != 2) {
            return null;
        }

        return new VerificationByEmail(split[0], LocalDateTime.parse(split[1]));
    }
}
