package com.fjb.sunrise.dtos.responses;

import com.fjb.sunrise.models.User;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class UserFullPageResponse implements Serializable {
    @Transient
    private List<User> data;
    private Long recordsTotal;
    private Long recordsFiltered;
    private Integer draw;

    public void setData(List<UserResponseDTO> list) {
    }
}
