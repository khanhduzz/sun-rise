package com.fjb.sunrise.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
public class Transaction {
    @Override
	public String toString() {
		return "Transaction [id=" + id + ", transactionDate=" + transactionDate + ", note=" + note + ", amount="
				+ amount + ", type=" + type + ", category=" + category + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	private Date transactionDate;
	private String note;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // Constructors, Getters, and Setters
}
