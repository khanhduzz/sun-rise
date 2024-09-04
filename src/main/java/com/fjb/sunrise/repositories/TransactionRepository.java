package com.fjb.sunrise.repositories;

import com.example.demo.model.Category;
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByType(TransactionType type);
    
}
