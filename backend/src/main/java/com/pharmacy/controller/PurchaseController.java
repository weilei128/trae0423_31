package com.pharmacy.controller;

import com.pharmacy.common.PageResult;
import com.pharmacy.common.Result;
import com.pharmacy.dto.PurchasePlanDTO;
import com.pharmacy.entity.PurchasePlan;
import com.pharmacy.entity.PurchasePlanItem;
import com.pharmacy.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("/page")
    public Result<PageResult<PurchasePlan>> listPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(purchaseService.listPage(current, size, keyword, status));
    }

    @GetMapping("/{id}")
    public Result<PurchasePlan> getById(@PathVariable Long id) {
        return Result.success(purchaseService.getById(id));
    }

    @GetMapping("/{id}/items")
    public Result<List<PurchasePlanItem>> getItems(@PathVariable Long id) {
        return Result.success(purchaseService.getItems(id));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody PurchasePlanDTO dto) {
        return Result.success(purchaseService.savePlan(dto.getPlan(), dto.getItems()));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody PurchasePlanDTO dto) {
        return Result.success(purchaseService.updatePlan(dto.getPlan(), dto.getItems()));
    }

    @PostMapping("/{id}/submit")
    public Result<Boolean> submit(@PathVariable Long id) {
        return Result.success(purchaseService.submitPlan(id));
    }

    @PostMapping("/{id}/audit")
    public Result<Boolean> audit(@PathVariable Long id, @RequestParam Integer status) {
        return Result.success(purchaseService.auditPlan(id, status));
    }

    @PostMapping("/{id}/cancel")
    public Result<Boolean> cancel(@PathVariable Long id) {
        return Result.success(purchaseService.cancelPlan(id));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(purchaseService.deletePlan(id));
    }
}
