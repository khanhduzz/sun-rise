package com.fjb.sunrise.services;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CreateOrUpdateTransactionRequest;
import com.fjb.sunrise.models.Transaction;
import java.text.ParseException;
import org.springframework.data.domain.Page;

public interface TransactionService {
    Transaction create(CreateOrUpdateTransactionRequest request) throws ParseException;
    Transaction findById(Long id);
    Page<Transaction> findByPage(Integer page, Integer pageSize, String sort, boolean isAscending);
    Page<Transaction> getTransactionList(DataTableInputDTO payload);
    Transaction update(CreateOrUpdateTransactionRequest request) throws ParseException;
}
