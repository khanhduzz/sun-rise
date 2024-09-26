package com.fjb.sunrise.controllers;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CreateOrUpdateTransactionRequest;
import com.fjb.sunrise.dtos.responses.TransactionFullPageResponse;
import com.fjb.sunrise.mappers.TransactionMapper;
import com.fjb.sunrise.models.Transaction;
import com.fjb.sunrise.repositories.CategoryRepository;
import com.fjb.sunrise.services.CategoryService;
import com.fjb.sunrise.services.TransactionService;
import com.fjb.sunrise.services.UserService;
import com.fjb.sunrise.utils.Constants;
import jakarta.validation.Valid;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/transaction")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final TransactionMapper transactionMapper;

    @GetMapping("/create")
    public String getCreate(@ModelAttribute("request") CreateOrUpdateTransactionRequest request, Model model) {
        model.addAttribute(Constants.ApiConstant.CATEGORIES, categoryService.findCategoryByAdminAndUser());
        model.addAttribute(Constants.ApiConstant.USERS, userService.findAllNormalUser());
        model.addAttribute(Constants.ApiConstant.STATISTIC, transactionService.statistic());
        return Constants.ApiConstant.TRANSACTION_INDEX;
    }

    @PostMapping("/create")
    public String postCreate(@ModelAttribute("request") @Valid CreateOrUpdateTransactionRequest request,
                             BindingResult result, Model model)
            throws ParseException {
        model.addAttribute(Constants.ApiConstant.CATEGORIES, categoryService.findCategoryByAdminAndUser());
        model.addAttribute(Constants.ApiConstant.USERS, userService.findAllNormalUser());
        model.addAttribute(Constants.ApiConstant.STATISTIC, transactionService.statistic());
        if (result.hasErrors()) {
            model.addAttribute(Constants.ApiConstant.CATEGORIES, categoryService.findCategoryByAdminAndUser());
            model.addAttribute(Constants.ApiConstant.USERS, userService.findAllNormalUser());
            model.addAttribute(Constants.ApiConstant.STATISTIC, transactionService.statistic());
            return Constants.ApiConstant.TRANSACTION_INDEX;
        }
        Transaction transaction = transactionService.create(request);
        return "redirect:/transaction/create";
    }

    @PostMapping("/page/{email}")
    @ResponseBody
    public TransactionFullPageResponse getPage(@PathVariable String email, @RequestBody DataTableInputDTO payload) {
        Page<Transaction> transactionPage = transactionService.getTransactionList(payload, email);
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

}
