package com.fjb.sunrise.controllers;


import com.fjb.sunrise.services.CategoryService;
import com.fjb.sunrise.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("transactions", transactionService.getAllTransactions());
        return "dashboard";
    }
}
