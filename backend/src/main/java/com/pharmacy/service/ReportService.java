package com.pharmacy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pharmacy.entity.StockIn;
import com.pharmacy.entity.StockInItem;
import com.pharmacy.entity.StockOut;
import com.pharmacy.entity.StockOutItem;
import com.pharmacy.mapper.StockInItemMapper;
import com.pharmacy.mapper.StockInMapper;
import com.pharmacy.mapper.StockOutItemMapper;
import com.pharmacy.mapper.StockOutMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReportService {

    @Autowired
    private StockInMapper stockInMapper;

    @Autowired
    private StockInItemMapper stockInItemMapper;

    @Autowired
    private StockOutMapper stockOutMapper;

    @Autowired
    private StockOutItemMapper stockOutItemMapper;

    public List<Map<String, Object>> getConsumptionRanking(int topN, String startDate, String endDate) {
        LocalDateTime start = startDate != null ? LocalDate.parse(startDate).atStartOfDay() : LocalDate.now().minusMonths(1).atStartOfDay();
        LocalDateTime end = endDate != null ? LocalDate.parse(endDate).plusDays(1).atStartOfDay() : LocalDate.now().plusDays(1).atStartOfDay();

        List<StockOut> stockOuts = stockOutMapper.selectList(new LambdaQueryWrapper<StockOut>()
                .eq(StockOut::getStatus, 2)
                .between(StockOut::getOutTime, start, end));

        Map<Long, Integer> consumptionMap = new HashMap<>();
        for (StockOut out : stockOuts) {
            List<StockOutItem> items = stockOutItemMapper.selectList(new LambdaQueryWrapper<StockOutItem>().eq(StockOutItem::getOutId, out.getId()));
            for (StockOutItem item : items) {
                consumptionMap.merge(item.getDrugId(), item.getQuantity(), Integer::sum);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        consumptionMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(topN > 0 ? topN : 10)
                .forEach(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("drugId", entry.getKey());
                    map.put("quantity", entry.getValue());
                    result.add(map);
                });

        return result;
    }

    public Map<String, Object> getPurchaseAnalysis(String startDate, String endDate) {
        LocalDateTime start = startDate != null ? LocalDate.parse(startDate).atStartOfDay() : LocalDate.now().minusMonths(1).atStartOfDay();
        LocalDateTime end = endDate != null ? LocalDate.parse(endDate).plusDays(1).atStartOfDay() : LocalDate.now().plusDays(1).atStartOfDay();

        List<StockIn> stockIns = stockInMapper.selectList(new LambdaQueryWrapper<StockIn>()
                .eq(StockIn::getStatus, 1)
                .between(StockIn::getInTime, start, end));

        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalCount = 0;
        Map<Long, BigDecimal> supplierAmount = new HashMap<>();

        for (StockIn in : stockIns) {
            if (in.getTotalAmount() != null) {
                totalAmount = totalAmount.add(in.getTotalAmount());
            }
            totalCount++;
            if (in.getSupplierId() != null && in.getTotalAmount() != null) {
                supplierAmount.merge(in.getSupplierId(), in.getTotalAmount(), BigDecimal::add);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalAmount", totalAmount);
        result.put("totalCount", totalCount);
        result.put("supplierAmount", supplierAmount);

        return result;
    }

    public Map<String, Object> getTurnoverAnalysis() {
        List<StockIn> stockIns = stockInMapper.selectList(new LambdaQueryWrapper<StockIn>().eq(StockIn::getStatus, 1));
        List<StockOut> stockOuts = stockOutMapper.selectList(new LambdaQueryWrapper<StockOut>().eq(StockOut::getStatus, 2));

        BigDecimal inAmount = stockIns.stream()
                .map(s -> s.getTotalAmount() != null ? s.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long inCount = stockIns.stream().mapToLong(s -> {
            List<StockInItem> items = stockInItemMapper.selectList(new LambdaQueryWrapper<StockInItem>().eq(StockInItem::getInId, s.getId()));
            return items.stream().mapToLong(StockInItem::getQuantity).sum();
        }).sum();

        long outCount = stockOuts.stream().mapToLong(s -> {
            List<StockOutItem> items = stockOutItemMapper.selectList(new LambdaQueryWrapper<StockOutItem>().eq(StockOutItem::getOutId, s.getId()));
            return items.stream().mapToLong(StockOutItem::getQuantity).sum();
        }).sum();

        Map<String, Object> result = new HashMap<>();
        result.put("totalInAmount", inAmount);
        result.put("totalInCount", inCount);
        result.put("totalOutCount", outCount);

        return result;
    }
}
