package com.fjb.sunrise.repositories;

import com.fjb.sunrise.dtos.base.CategoryAndTotalAmountPerCategory;
import com.fjb.sunrise.dtos.base.DayAndTotalAmountPerDay;
import com.fjb.sunrise.enums.ETrans;
import com.fjb.sunrise.models.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction, Long>,
    JpaSpecificationExecutor<Transaction> {

    Page<Transaction> findAll(Pageable pageable);

    @Query("select sum(t.amount) from Transaction t "
        + "where t.updatedAt between ?1 and ?2")
    Double sumAmountInRange(LocalDateTime start, LocalDateTime end);

    @Query("select new com.fjb.sunrise.dtos.base.DayAndTotalAmountPerDay(t.updatedAt,sum(t.amount)) from Transaction t"
        + "where t.updatedAt between ?1 and ?2 group by t.updatedAt")
    List<DayAndTotalAmountPerDay> sumAmountPerDayIn3Month(LocalDateTime start, LocalDateTime endOf3Month);

    @Query("select sum(t.amount) from Transaction t "
        + "where t.transactionType = ?1 and t.updatedAt between ?2 and ?3 ")
    Double sumTransactionTypeINInThisYear(ETrans transactionType, LocalDateTime start, LocalDateTime end);

    @Query("select new com.fjb.sunrise.dtos.base.CategoryAndTotalAmountPerCategory(t.category.name,sum(t.amount)) "
        + "from Transaction t "
        + "group by t.category order by sum(t.amount) desc limit 4")
    List<CategoryAndTotalAmountPerCategory> sumAmountPerCategory();
}
