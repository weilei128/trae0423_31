package com.pharmacy.controller;

import com.pharmacy.common.PageResult;
import com.pharmacy.common.Result;
import com.pharmacy.entity.ExpiryAlert;
import com.pharmacy.service.ExpiryAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expiry")
public class ExpiryAlertController {

    @Autowired
    private ExpiryAlertService expiryAlertService;

    @GetMapping("/page")
    public Result<PageResult<ExpiryAlert>> listPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer alertLevel) {
        return Result.success(expiryAlertService.listPage(current, size, status, alertLevel));
    }

    @GetMapping("/pending")
    public Result<List<ExpiryAlert>> getPending() {
        return Result.success(expiryAlertService.getPendingAlerts());
    }

    @PostMapping("/generate")
    public Result<Boolean> generate() {
        expiryAlertService.generateAlerts();
        return Result.success(true);
    }

    @PostMapping("/{id}/handle")
    public Result<Boolean> handle(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        Integer status = (Integer) data.get("status");
        String handler = (String) data.getOrDefault("handler", "system");
        String remark = (String) data.get("remark");
        return Result.success(expiryAlertService.handleAlert(id, status, handler, remark));
    }
}
