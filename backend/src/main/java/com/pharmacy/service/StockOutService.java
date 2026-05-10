package com.pharmacy.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.common.PageResult;
import com.pharmacy.entity.Stock;
import com.pharmacy.entity.StockOut;
import com.pharmacy.entity.StockOutItem;
import com.pharmacy.mapper.StockOutItemMapper;
import com.pharmacy.mapper.StockOutMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockOutService extends ServiceImpl<StockOutMapper, StockOut> {

    @Autowired
    private StockOutItemMapper stockOutItemMapper;

    @Autowired
    private StockService stockService;

    public PageResult<StockOut> listPage(int current, int size, String keyword, Integer status) {
        Page<StockOut> page = new Page<>(current, size);
        LambdaQueryWrapper<StockOut> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(StockOut::getOutNo, keyword)
                    .or().like(StockOut::getApplyDepartment, keyword));
        }
        if (status != null) {
            wrapper.eq(StockOut::getStatus, status);
        }
        wrapper.orderByDesc(StockOut::getCreateTime);
        Page<StockOut> result = page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveStockOut(StockOut stockOut, List<StockOutItem> items) {
        if (!StringUtils.hasText(stockOut.getOutNo())) {
            stockOut.setOutNo("SO" + IdUtil.getSnowflakeNextIdStr());
        }
        stockOut.setStatus(0);
        save(stockOut);
        if (items != null && !items.isEmpty()) {
            for (StockOutItem item : items) {
                item.setOutId(stockOut.getId());
                stockOutItemMapper.insert(item);
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean review(Long id, Integer status, String reviewer) {
        StockOut stockOut = getById(id);
        if (stockOut == null || stockOut.getStatus() != 0) return false;
        stockOut.setStatus(status);
        stockOut.setReviewer(reviewer);
        stockOut.setReviewTime(LocalDateTime.now());
        return updateById(stockOut);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean confirmStockOut(Long id) {
        StockOut stockOut = getById(id);
        if (stockOut == null || stockOut.getStatus() != 1) return false;
        
        List<StockOutItem> items = getItems(id);
        for (StockOutItem item : items) {
            if (StringUtils.hasText(item.getBatchNo())) {
                stockService.deductStock(item.getDrugId(), item.getBatchNo(), item.getQuantity());
            } else {
                List<Stock> stocks = stockService.getStockByDrug(item.getDrugId());
                int remain = item.getQuantity();
                for (Stock stock : stocks) {
                    if (remain <= 0) break;
                    int deduct = Math.min(stock.getQuantity(), remain);
                    stockService.deductStock(item.getDrugId(), stock.getBatchNo(), deduct);
                    remain -= deduct;
                }
            }
        }
        
        stockOut.setStatus(2);
        stockOut.setOutTime(LocalDateTime.now());
        return updateById(stockOut);
    }

    public List<StockOutItem> getItems(Long outId) {
        return stockOutItemMapper.selectList(new LambdaQueryWrapper<StockOutItem>().eq(StockOutItem::getOutId, outId));
    }
}
