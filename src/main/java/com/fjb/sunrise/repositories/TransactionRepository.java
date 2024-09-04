package com.fjb.sunrise.repositories;


import com.fjb.sunrise.models.Transaction;
import com.fjb.sunrise.models.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByType(TransactionType type);
    
}
