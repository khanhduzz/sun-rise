package com.fjb.sunrise.services;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.*;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CategoryCreateDto;
import com.fjb.sunrise.dtos.requests.CategoryUpdateDto;
import com.fjb.sunrise.dtos.requests.CreateOrUpdateTransactionRequest;
import com.fjb.sunrise.dtos.responses.CategoryResponseDto;
import com.fjb.sunrise.dtos.responses.StatisticResponse;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TransactionServiceTest {
    @Autowired
    TransactionServiceImpl transactionService;

    @MockBean
    TransactionRepository transactionRepository;
    @MockBean
    CategoryRepository categoryRepository;
    @MockBean
    UserRepository userRepository;

    private Transaction transaction;
    private Category category;
    private User user;
    private CreateOrUpdateTransactionRequest create;
    private CreateOrUpdateTransactionRequest update;
    private StatisticResponse statisticResponse;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
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

        statisticResponse = new StatisticResponse();
        statisticResponse.setTotalThisMonth("100000");
        statisticResponse.setTotalInputThisYear("200000");
        statisticResponse.setTotalThisYear("100000");
    }

    @DisplayName("Junit test for createTransaction method")
    @Test
    public void createTransaction_whenCreateOrUpdateDto_returnTransaction() throws ParseException {
        UserDetails appUserDetails = new org.springframework.security.core.userdetails.User("an@gmail.com", "123",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        Authentication auth = new UsernamePasswordAuthenticationToken(appUserDetails, null);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        Mockito.when(userRepository.findByEmailOrPhone(anyString())).thenReturn(user);
        Mockito.when(transactionRepository.save(any())).thenReturn(transaction);

        Transaction transactionTest = transactionService.create(create);
        Assertions.assertEquals(transactionTest, transaction);
    }

    @DisplayName("Junit test for getTransactionList method")
    @Test
    public void getTransactionList_whenDataTableInputDTO_returnPageOfTransaction() {
        List<Map<String, String>> orders = new ArrayList<>();
        Map<String, String> order = new HashMap<>();
        order.put("column","2");
        order.put("dir","asc");
        order.put("colName","category");
        orders.add(order);
        Map<String,String> search = new HashMap<>();
        search.put("value","");
        search.put("regex","false");
        DataTableInputDTO dataTable = new DataTableInputDTO(1,null,orders,10,10,search);
        List<Transaction> transactions = Instancio.ofList(Transaction.class).create();
        Page<Transaction> page = new PageImpl<Transaction>(transactions);
        Mockito.when(userRepository.findByEmailOrPhone(anyString())).thenReturn(user);

        Mockito.when(transactionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<Transaction> transactionPage = transactionService.getTransactionList(dataTable, "an@gmail.com");

        Assertions.assertEquals(transactionPage, page);
    }

    @DisplayName("Junit test for update method")
    @Test
    public void updateTransaction_whenCreateOrUpdateDto_returnTransaction() throws ParseException {
        UserDetails appUserDetails = new org.springframework.security.core.userdetails.User("an@gmail.com", "123",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        Authentication auth = new UsernamePasswordAuthenticationToken(appUserDetails, null);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(transactionRepository.getReferenceById(anyLong())).thenReturn(transaction);
        Mockito.when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        Mockito.when(userRepository.findByEmailOrPhone(anyString())).thenReturn(user);
        Mockito.when(transactionRepository.save(any())).thenReturn(transaction);

        Transaction transactionTest = transactionService.update(update);
        Assertions.assertEquals(transactionTest, transaction);
    }

    @DisplayName("Junit test for statistic method")
    @Test
    public void statistic_returnStatisticResponse() throws ParseException {
        Mockito.when(transactionRepository.sumAmountInRange(any(LocalDateTime.class),any(LocalDateTime.class)))
                        .thenReturn(100000.0);
        Mockito.when(transactionRepository.sumTransactionTypeINInThisYear(any(ETrans.class),any(LocalDateTime.class),any(LocalDateTime.class)))
                        .thenReturn(200000.0);
        StatisticResponse response = transactionService.statistic();
        Assertions.assertEquals(statisticResponse, response);
    }
}
