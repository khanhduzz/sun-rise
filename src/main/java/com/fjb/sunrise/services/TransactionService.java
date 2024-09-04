package com.fjb.sunrise.services;

import com.example.demo.model.Category;
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionType;
import com.example.demo.repository.TransactionRepository;
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
