package com.fjb.sunrise.services.impl;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CreateOrUpdateTransactionRequest;
import com.fjb.sunrise.mappers.TransactionMapper;
import com.fjb.sunrise.models.Category;
import com.fjb.sunrise.models.Transaction;
import com.fjb.sunrise.repositories.CategoryRepository;
import com.fjb.sunrise.repositories.TransactionRepository;
import com.fjb.sunrise.repositories.UserRepository;
import com.fjb.sunrise.services.TransactionService;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public Transaction create(CreateOrUpdateTransactionRequest request) throws ParseException {
        Transaction transaction = new Transaction();
        transaction.setAmount(convertMoneyStringWithCommaToDouble(request.getAmount()));
        transaction.setTransactionType(request.getTransactionType());
        transaction.setCategory(categoryRepository.findById(request.getCategory()).orElse(null));
        transaction.setUser(userRepository.findByUsername(getCurrentUserName()));
        transaction.setCreatedAt(request.getCreatedAt() == null
            ? LocalDateTime.now() : request.getCreatedAt());
        return transactionRepository.save(transaction);
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Invalid transaction id")
        );
    }

    @Override
    public Page<Transaction> findByPage(Integer page, Integer pageSize, String sort, boolean isAscending) {
        Pageable pageable = null;
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(Sort.Order.asc("id"));
        Sort sortOpt = Sort.by(orders);

        if (isAscending) {
            pageable = PageRequest.of(page - 1, pageSize, sortOpt); //Sort.by(sort)
        } else {
            pageable = PageRequest.of(page - 1, pageSize, sortOpt);
        }


        return transactionRepository.findAll(pageable);
    }

    @Override
    public Page<Transaction> getTransactionList(DataTableInputDTO payload) {
        Sort sortOpt = Sort.by(Sort.Direction.ASC, "id");
        if (!payload.getOrder().isEmpty()) {
            sortOpt = Sort.by(
                Sort.Direction.fromString(payload.getOrder().get(0).get("dir").toUpperCase()),
                payload.getOrder().get(0).get("colName"));
        }
        int pageNumber = payload.getStart() / 10;
        if (payload.getStart() % 10 != 0) {
            pageNumber = pageNumber - 1;
        }

        Pageable pageable = PageRequest.of(pageNumber, payload.getLength(), sortOpt);
        final String keyword = payload.getSearch().getOrDefault("value", "");
        Specification<Transaction> specs = null;
        if (Strings.isNotBlank(keyword)) {
            // "%" + keyword + "%"
            specs = Specification.where((
                (root, query, builder) ->
                    builder.like(builder.lower(root.get("transactionType")),
                        String.format("%%%s%%",
                            payload.getSearch().getOrDefault("value", "").toLowerCase()
                        ))));

            specs = specs.or((root, query, builder) -> {
                Join<Transaction, Category> categoryJoin = root.join("category");
                return builder.like(builder.lower(categoryJoin.get("name")),
                    String.format("%%%s%%",
                        payload.getSearch().getOrDefault("value", "").toLowerCase()
                    ));
            });
        }

        return transactionRepository.findAll(specs, pageable);
    }

    @Override
    @Transactional
    public Transaction update(CreateOrUpdateTransactionRequest request) throws ParseException {
        Transaction transaction = transactionRepository.getReferenceById(request.getId());
        transaction.setUpdatedAt(request.getCreatedAt());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setAmount(convertMoneyStringWithCommaToDouble(request.getAmount()));
        transaction.setUser(userRepository.findByUsername(getCurrentUserName()));
        transaction.setCategory(categoryRepository.findById(request.getCategory()).orElse(null));

        log.error("update: {}", transaction.toString());
        Transaction transaction1 = transactionRepository.save(transaction);
        log.error("updated: {}", transaction1.toString());
        return transaction1;
    }

    private Double convertMoneyStringWithCommaToDouble(String money) throws ParseException {
        DecimalFormat df = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        df.setDecimalFormatSymbols(symbols);
        return df.parse(money).doubleValue();
    }

    private String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return user.getUsername();
    }
}
