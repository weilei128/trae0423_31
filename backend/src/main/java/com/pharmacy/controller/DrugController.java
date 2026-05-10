package com.pharmacy.controller;

import com.pharmacy.common.PageResult;
import com.pharmacy.common.Result;
import com.pharmacy.entity.DrugCategory;
import com.pharmacy.entity.DrugDict;
import com.pharmacy.service.DrugCategoryService;
import com.pharmacy.service.DrugDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drug")
public class DrugController {

    @Autowired
    private DrugDictService drugDictService;

    @Autowired
    private DrugCategoryService drugCategoryService;

    @GetMapping("/dict/page")
    public Result<PageResult<DrugDict>> listDictPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        return Result.success(drugDictService.listPage(current, size, keyword, categoryId));
    }

    @GetMapping("/dict/list")
    public Result<List<DrugDict>> listDictAll() {
        return Result.success(drugDictService.listAll());
    }

    @GetMapping("/dict/{id}")
    public Result<DrugDict> getDictById(@PathVariable Long id) {
        return Result.success(drugDictService.getById(id));
    }

    @PostMapping("/dict")
    public Result<Boolean> saveDict(@RequestBody DrugDict drug) {
        return Result.success(drugDictService.saveDrug(drug));
    }

    @PutMapping("/dict")
    public Result<Boolean> updateDict(@RequestBody DrugDict drug) {
        return Result.success(drugDictService.updateDrug(drug));
    }

    @DeleteMapping("/dict/{id}")
    public Result<Boolean> deleteDict(@PathVariable Long id) {
        return Result.success(drugDictService.deleteById(id));
    }

    @GetMapping("/category/list")
    public Result<List<DrugCategory>> listCategory() {
        return Result.success(drugCategoryService.listAll());
    }

    @PostMapping("/category")
    public Result<Boolean> saveCategory(@RequestBody DrugCategory category) {
        return Result.success(drugCategoryService.saveCategory(category));
    }

    @PutMapping("/category")
    public Result<Boolean> updateCategory(@RequestBody DrugCategory category) {
        return Result.success(drugCategoryService.updateCategory(category));
    }

    @DeleteMapping("/category/{id}")
    public Result<Boolean> deleteCategory(@PathVariable Long id) {
        return Result.success(drugCategoryService.deleteById(id));
    }
}
