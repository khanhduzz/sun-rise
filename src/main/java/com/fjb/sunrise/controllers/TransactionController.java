package com.fjb.sunrise.controllers;

import static com.fjb.sunrise.utils.Constants.ApiConstant.CATEGORIES;
import static com.fjb.sunrise.utils.Constants.ApiConstant.TRANSACTION_INDEX;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CreateOrUpdateTransactionRequest;
import com.fjb.sunrise.dtos.responses.StatisticResponse;
import com.fjb.sunrise.dtos.responses.TransactionFullPageResponse;
import com.fjb.sunrise.mappers.TransactionMapper;
import com.fjb.sunrise.models.Transaction;
import com.fjb.sunrise.repositories.CategoryRepository;
import com.fjb.sunrise.services.TransactionService;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/transaction")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;

    @GetMapping("/index")
    public String index(@ModelAttribute("request") CreateOrUpdateTransactionRequest request, Model model) {
        model.addAttribute(CATEGORIES, categoryRepository.findAll());
        return TRANSACTION_INDEX;
    }

    @GetMapping("/create")
    public String getCreate(@ModelAttribute("request") CreateOrUpdateTransactionRequest request, Model model) {
        model.addAttribute(CATEGORIES, categoryRepository.findAll());
        return TRANSACTION_INDEX;
    }

    @PostMapping("/create")
    public String postCreate(@ModelAttribute("request") CreateOrUpdateTransactionRequest request, Model model)
        throws ParseException {
        model.addAttribute(CATEGORIES, categoryRepository.findAll());
        Transaction transaction = transactionService.create(request);
        return TRANSACTION_INDEX;
    }


    @PostMapping("/page")
    @ResponseBody
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
    public String postUpdate(@PathVariable Long id,
                             @ModelAttribute("request") CreateOrUpdateTransactionRequest request)
        throws ParseException {
        request.setId(id);
        transactionService.update(request);
        return "redirect:/transaction/create";
    }

    @GetMapping("/statistic")
    @ResponseBody
    public StatisticResponse getStatistic() {
        return transactionService.statistic();
    }
}
