package com.fjb.sunrise.controllers;


import com.fjb.sunrise.models.Category;
import com.fjb.sunrise.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("category", new Category());  // Thêm đối tượng category trống vào model
        return "category/list";
    }

    @PostMapping
    public String createCategory(@ModelAttribute Category category) {
        categoryService.createCategory(category);
        return "redirect:/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories";
    }
    @GetMapping("/search")
    public String searchCategories(@RequestParam("name") String name, Model model) {
        model.addAttribute("categories", categoryService.searchCategoriesByName(name));
        model.addAttribute("category", new Category());  // Thêm đối tượng category trống vào model
        return "category/list";
    }
}
