package com.fjb.sunrise.services.impl;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CreateOrUpdateTransactionRequest;
import com.fjb.sunrise.enums.ETrans;
import com.fjb.sunrise.models.Category;
import com.fjb.sunrise.models.Transaction;
import com.fjb.sunrise.repositories.CategoryRepository;
import com.fjb.sunrise.repositories.TransactionRepository;
import com.fjb.sunrise.repositories.UserRepository;
import com.fjb.sunrise.services.TransactionService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
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
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public Transaction create(CreateOrUpdateTransactionRequest request) throws ParseException {
        Transaction transaction = new Transaction();
        transaction.setAmount(convertMoneyStringWithCommaToDouble(request.getAmount()));
        transaction.setTransactionType(request.getTransactionType());
        transaction.setCategory(categoryRepository.findById(request.getCategory()).orElse(null));
        transaction.setUser(userRepository.findByEmailOrPhone(getCurrentUserName()));
        LocalDateTime now = request.getCreatedAt() == null ? LocalDateTime.now() : request.getCreatedAt();
        transaction.setCreatedAt(now);
        transaction.setUpdatedAt(now);
        return transactionRepository.save(transaction);
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Invalid transaction id")
        );
    }

    @Override
    public Page<Transaction> getTransactionList(DataTableInputDTO payload, String email) {
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
        specs = Specification.where((root, query, builder) -> {
            com.fjb.sunrise.models.User dbUser = null;
            if (email.equals("null")) {
                dbUser = userRepository.findByEmailOrPhone(getCurrentUserName());
            } else {
                dbUser = userRepository.findByEmailOrPhone(email);
            }
            return builder.equal(root.get("user"), dbUser);
        });
        if (Strings.isNotBlank(keyword)) {
            // "%" + keyword + "%"
            specs = specs.and(((root, query, builder) -> {
                Join<Transaction, Category> categoryJoin = root.join("category");
                String search = payload.getSearch().getOrDefault("value", "").toLowerCase();
                if(search.equals("thu")) {
                    search = "IN";
                } else if(search.equals("chi")) {
                    search = "OUT";
                }
                Predicate predictTransactionType =
//                        builder.like(builder.lower(root.get("transactionType")), String.format("%%%s%%",
//                                payload.getSearch().getOrDefault("value", "").toLowerCase()));
                        builder.like(builder.lower(root.get("transactionType")), String.format("%%%s%%",
                                search.toLowerCase()));
                Predicate predictCategory = builder.like(builder.lower(categoryJoin.get("name")),
                        String.format("%%%s%%",
                                payload.getSearch().getOrDefault("value", "").toLowerCase()
                        ));
                return builder.or(predictTransactionType, predictCategory);

            }));
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
//        transaction.setUser(userRepository.findByEmailOrPhone(getCurrentUserName()));
        transaction.setCategory(categoryRepository.findById(request.getCategory()).orElse(null));

        log.error("update: {}", transaction.toString());
        Transaction transaction1 = transactionRepository.save(transaction);
        log.error("updated: {}", transaction1.toString());
        return transaction1;
    }

    private String changeFormatFromFullDateToMonthDate(LocalDateTime dateTime) throws ParseException {
        SimpleDateFormat monthDate = new SimpleDateFormat("dd-MM", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        Date date = sdf.parse(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        monthDate.format(date);
        return monthDate.format(date);
    }

    private String convertDoubleWithScientificNotationToDouble(Double amount) {
        DecimalFormat df = new DecimalFormat("#.###########");
        return df.format(amount);
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

    private LocalDateTime getFirstOrLastDateOfThisYear(boolean isLast) {
        final LocalDateTime now = LocalDateTime.now();
        final int year = now.getYear();
        Month month = LocalDateTime.MIN.getMonth();
        int dayOfMonth = 1;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (isLast) {
            month = now.getMonth();
            dayOfMonth = now.getDayOfMonth();
            hour = 23;
            minute = 59;
            second = 59;
        }
        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second).atOffset(ZoneOffset.UTC)
                .toLocalDateTime();
    }

    private LocalDateTime getFirstDayOfThisMonth() {
        LocalDateTime now = LocalDateTime.now();

        return LocalDateTime.of(Year.now().getValue(), now.getMonth(), 1, 0, 0, 0).atOffset(ZoneOffset.UTC)
                .toLocalDateTime();
    }

}
