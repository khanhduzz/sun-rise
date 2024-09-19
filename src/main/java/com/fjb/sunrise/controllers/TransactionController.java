package com.fjb.sunrise.controllers;

import static com.fjb.sunrise.utils.Constants.ApiConstant.CATEGORIES;
import static com.fjb.sunrise.utils.Constants.ApiConstant.TRANSACTION_INDEX;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CreateOrUpdateTransactionRequest;
import com.fjb.sunrise.dtos.responses.StatisticResponse;
import com.fjb.sunrise.dtos.responses.TransactionFullPageResponse;
import com.fjb.sunrise.mappers.TransactionMapper;
import com.fjb.sunrise.models.Transaction;
import com.fjb.sunrise.services.CategoryService;
import com.fjb.sunrise.services.TransactionService;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final TransactionMapper transactionMapper;

    @GetMapping("/create")
    public ModelAndView getCreate(@ModelAttribute("request") CreateOrUpdateTransactionRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(TRANSACTION_INDEX);
        modelAndView.addObject(CATEGORIES, categoryService.findCategoryByAdminAndUser());
        return modelAndView;
    }

    @PostMapping("/create")
    public ModelAndView postCreate(@ModelAttribute("request") CreateOrUpdateTransactionRequest request)
        throws ParseException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(TRANSACTION_INDEX);
        modelAndView.addObject(CATEGORIES, categoryService.findCategoryByAdminAndUser());
        Transaction transaction = transactionService.create(request);
        return modelAndView;
    }


    @PostMapping("/page")
    public TransactionFullPageResponse getPage(@RequestBody DataTableInputDTO payload) {
        Page<Transaction> transactionPage = transactionService.getTransactionList(payload);
        TransactionFullPageResponse response = new TransactionFullPageResponse();
        response.setData(transactionMapper.listTransactionToListTransactionPageResponse(
            transactionPage.stream().toList()
        ));
        response.setDraw(payload.getDraw());
        response.setRecordsFiltered(transactionPage.getTotalElements());
        response.setRecordsTotal(transactionPage.getTotalElements());
        return response;
    }

    @PostMapping("/update/{id}")
    public ModelAndView postUpdate(@PathVariable Long id,
                             @ModelAttribute("request") CreateOrUpdateTransactionRequest request)
        throws ParseException {
        request.setId(id);
        transactionService.update(request);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/transaction/create");
        return modelAndView;
    }

    @GetMapping("/statistic")
    public StatisticResponse getStatistic() {
        return transactionService.statistic();
    }
}
