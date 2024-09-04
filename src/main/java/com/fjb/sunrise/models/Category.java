package com.fjb.sunrise.models;

import jakarta.persistence.*;

@Entity
public class Category {
    @Override
	public String toString() {
		return "Category [id=" + id + ", name=" + name + ", type=" + type + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    // Constructors, Getters, and Setters
}
