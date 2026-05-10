package com.pharmacy.controller;

import com.pharmacy.common.Result;
import com.pharmacy.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/consumption")
    public Result<List<Map<String, Object>>> getConsumptionRanking(
            @RequestParam(defaultValue = "10") int topN,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(reportService.getConsumptionRanking(topN, startDate, endDate));
    }

    @GetMapping("/purchase")
    public Result<Map<String, Object>> getPurchaseAnalysis(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(reportService.getPurchaseAnalysis(startDate, endDate));
    }

    @GetMapping("/turnover")
    public Result<Map<String, Object>> getTurnoverAnalysis() {
        return Result.success(reportService.getTurnoverAnalysis());
    }
}
