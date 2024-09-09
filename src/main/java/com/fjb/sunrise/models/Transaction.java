package com.fjb.sunrise.models;

import com.fjb.sunrise.enums.ETrans;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Transaction extends AuditEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Long id;

    @Column(columnDefinition = "DECIMAL(11,2)")
    private Double amount;

    @Enumerated(value = EnumType.STRING)
    private ETrans transactionType;

    @Column(columnDefinition = "TEXT")
    private String note;

    @ManyToOne
    private User user;

    @ManyToOne
    private Category category;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
