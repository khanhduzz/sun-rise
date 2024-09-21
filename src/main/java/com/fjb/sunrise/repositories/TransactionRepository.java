package com.fjb.sunrise.repositories;

import com.fjb.sunrise.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionRepository extends JpaRepository<Transaction, Long>,
    JpaSpecificationExecutor<Transaction> {

    Page<Transaction> findAll(Pageable pageable);
}
