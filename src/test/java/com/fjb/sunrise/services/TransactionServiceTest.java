package com.fjb.sunrise.services;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CategoryCreateDto;
import com.fjb.sunrise.dtos.requests.CategoryUpdateDto;
import com.fjb.sunrise.dtos.requests.CreateOrUpdateTransactionRequest;
import com.fjb.sunrise.dtos.responses.CategoryResponseDto;
import com.fjb.sunrise.enums.ERole;
import com.fjb.sunrise.enums.EStatus;
import com.fjb.sunrise.enums.ETrans;
import com.fjb.sunrise.models.Category;
import com.fjb.sunrise.models.Transaction;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.repositories.CategoryRepository;
import com.fjb.sunrise.repositories.TransactionRepository;
import com.fjb.sunrise.repositories.UserRepository;
import com.fjb.sunrise.services.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

public class TransactionServiceTest {
    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;

    private Transaction transaction;
    private Category category;
    private User user;
    private CreateOrUpdateTransactionRequest create;
    private CreateOrUpdateTransactionRequest update;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        category = Category.builder()
                .id(1L)
                .name("Category-Test")
                .status(EStatus.ACTIVE)
                .build();
        user = User.builder()
                .id(1L)
                .email("an@gmail.com")
                .status(EStatus.ACTIVE)
                .role(ERole.USER)
                .build();

        transaction = new Transaction();
        transaction.setUpdatedAt(LocalDateTime.now());
        transaction.setTransactionType(ETrans.IN);
        transaction.setAmount(100000.0);
        transaction.setCategory(category);
        transaction.setUser(user);
        transaction.setId(1L);

        create = new CreateOrUpdateTransactionRequest();
        create.setCreatedAt(LocalDateTime.now());
        create.setAmount("100000");
        create.setCategory(1L);
        create.setTransactionType(ETrans.IN);

        update = new CreateOrUpdateTransactionRequest();
        update.setId(1l);
        update.setCreatedAt(LocalDateTime.now());
        update.setAmount("200000");
        update.setCategory(1L);
        update.setTransactionType(ETrans.IN);
    }

//    @Nested
//    class CreateCategoryTest {
//        @Test
//        void create_shouldReturnTransaction() {
//
//        }
//    }

}
