package com.fjb.sunrise.dtos.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fjb.sunrise.enums.ETrans;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CreateOrUpdateTransactionRequest implements Serializable {
    private Long id;
    @NotBlank(message = "Amount must not be blank")
    private String amount;

    private ETrans transactionType;

    private Long category;

//    private Long user;

//    private String note;

    // yyyy-MM-dd'T'H:mm
    private static final String MY_TIME_ZONE = "Asia/Bangkok";
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss",
        timezone = MY_TIME_ZONE)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @PastOrPresent
    private LocalDateTime createdAt;


}
