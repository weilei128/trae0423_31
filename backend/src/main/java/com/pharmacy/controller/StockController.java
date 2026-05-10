package com.pharmacy.controller;

import com.pharmacy.common.PageResult;
import com.pharmacy.common.Result;
import com.pharmacy.dto.StockInDTO;
import com.pharmacy.dto.StockOutDTO;
import com.pharmacy.entity.*;
import com.pharmacy.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockInService stockInService;

    @Autowired
    private StockOutService stockOutService;

    @GetMapping("/page")
    public Result<PageResult<Stock>> listPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long drugId) {
        return Result.success(stockService.listPage(current, size, keyword, drugId));
    }

    @GetMapping("/drug/{drugId}")
    public Result<List<Stock>> getByDrug(@PathVariable Long drugId) {
        return Result.success(stockService.getStockByDrug(drugId));
    }

    @GetMapping("/in/page")
    public Result<PageResult<StockIn>> listInPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(stockInService.listPage(current, size, keyword, status));
    }

    @GetMapping("/in/{id}")
    public Result<StockIn> getInById(@PathVariable Long id) {
        return Result.success(stockInService.getById(id));
    }

    @GetMapping("/in/{id}/items")
    public Result<List<StockInItem>> getInItems(@PathVariable Long id) {
        return Result.success(stockInService.getItems(id));
    }

    @PostMapping("/in")
    public Result<Boolean> saveIn(@RequestBody StockInDTO dto) {
        return Result.success(stockInService.saveStockIn(dto.getStockIn(), dto.getItems()));
    }

    @PostMapping("/in/{id}/confirm")
    public Result<Boolean> confirmIn(@PathVariable Long id) {
        return Result.success(stockInService.confirmStockIn(id));
    }

    @PostMapping("/in/{id}/cancel")
    public Result<Boolean> cancelIn(@PathVariable Long id) {
        return Result.success(stockInService.cancelStockIn(id));
    }

    @GetMapping("/out/page")
    public Result<PageResult<StockOut>> listOutPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(stockOutService.listPage(current, size, keyword, status));
    }

    @GetMapping("/out/{id}")
    public Result<StockOut> getOutById(@PathVariable Long id) {
        return Result.success(stockOutService.getById(id));
    }

    @GetMapping("/out/{id}/items")
    public Result<List<StockOutItem>> getOutItems(@PathVariable Long id) {
        return Result.success(stockOutService.getItems(id));
    }

    @PostMapping("/out")
    public Result<Boolean> saveOut(@RequestBody StockOutDTO dto) {
        return Result.success(stockOutService.saveStockOut(dto.getStockOut(), dto.getItems()));
    }

    @PostMapping("/out/{id}/review")
    public Result<Boolean> reviewOut(@PathVariable Long id, @RequestParam Integer status, @RequestParam(required = false) String reviewer) {
        return Result.success(stockOutService.review(id, status, reviewer != null ? reviewer : "system"));
    }

    @PostMapping("/out/{id}/confirm")
    public Result<Boolean> confirmOut(@PathVariable Long id) {
        return Result.success(stockOutService.confirmStockOut(id));
    }
}
