package com.fjb.sunrise.controllers;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CreateOrUpdateTransactionRequest;
import com.fjb.sunrise.dtos.responses.TransactionFullPageResponse;
import com.fjb.sunrise.mappers.TransactionMapper;
import com.fjb.sunrise.models.Transaction;
import com.fjb.sunrise.services.CategoryService;
import com.fjb.sunrise.services.TransactionService;
import com.fjb.sunrise.services.UserService;
import com.fjb.sunrise.utils.Constants;
import jakarta.validation.Valid;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
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
public class TransactionController {
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final TransactionMapper transactionMapper;

    @GetMapping("/create")
    public ModelAndView getCreate(@ModelAttribute("request") CreateOrUpdateTransactionRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(Constants.ApiConstant.CATEGORIES, categoryService.findCategoryByAdminAndUser());
        modelAndView.addObject(Constants.ApiConstant.USERS, userService.findAllNormalUser());
        modelAndView.addObject(Constants.ApiConstant.STATISTIC, transactionService.statistic());
        modelAndView.setViewName(Constants.ApiConstant.TRANSACTION_INDEX);
        return modelAndView;
    }

    @PostMapping("/create")
    public ModelAndView postCreate(@ModelAttribute("request") @Valid CreateOrUpdateTransactionRequest request,
                             BindingResult result)
            throws ParseException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(Constants.ApiConstant.CATEGORIES, categoryService.findCategoryByAdminAndUser());
        modelAndView.addObject(Constants.ApiConstant.USERS, userService.findAllNormalUser());
        modelAndView.addObject(Constants.ApiConstant.STATISTIC, transactionService.statistic());
        if (result.hasErrors()) {
            modelAndView.addObject(Constants.ApiConstant.CATEGORIES, categoryService.findCategoryByAdminAndUser());
            modelAndView.addObject(Constants.ApiConstant.USERS, userService.findAllNormalUser());
            modelAndView.addObject(Constants.ApiConstant.STATISTIC, transactionService.statistic());
            modelAndView.setViewName(Constants.ApiConstant.TRANSACTION_INDEX);
            return modelAndView;
        }
        transactionService.create(request);
        modelAndView.setViewName("redirect:/transaction/create");
        return modelAndView;
    }

    @PostMapping("/page/{email}")
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
    public ModelAndView postUpdate(@PathVariable Long id,
                             @ModelAttribute("request") CreateOrUpdateTransactionRequest request)
            throws ParseException {
        ModelAndView modelAndView = new ModelAndView();
        request.setId(id);
        transactionService.update(request);
        modelAndView.setViewName("redirect:/transaction/create");
        return modelAndView;
    }

}
