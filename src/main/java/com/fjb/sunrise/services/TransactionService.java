package com.fjb.sunrise.services;


import com.fjb.sunrise.models.Transaction;
import com.fjb.sunrise.models.TransactionType;
import com.fjb.sunrise.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public List<Transaction> getTransactionsByType(TransactionType type) {
        return transactionRepository.findByType(type);
    }
}
