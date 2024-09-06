package com.fjb.sunrise.mappers;

import com.fjb.sunrise.dtos.responses.TransactionPageResponse;
import com.fjb.sunrise.models.Transaction;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    List<TransactionPageResponse> listTransactionToListTransactionPageResponse(List<Transaction> transactions);

    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "createdAt",
        expression = "java(transaction.getUpdatedAt() == null ? "
            + "transaction.getCreatedAt() : transaction.getUpdatedAt())")
    TransactionPageResponse transactionToTransactionPageResponse(Transaction transaction);
}
