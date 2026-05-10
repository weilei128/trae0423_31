package com.pharmacy.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.common.PageResult;
import com.pharmacy.entity.Stock;
import com.pharmacy.entity.StockIn;
import com.pharmacy.entity.StockInItem;
import com.pharmacy.mapper.StockInItemMapper;
import com.pharmacy.mapper.StockInMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockInService extends ServiceImpl<StockInMapper, StockIn> {

    @Autowired
    private StockInItemMapper stockInItemMapper;

    @Autowired
    private StockService stockService;

    public PageResult<StockIn> listPage(int current, int size, String keyword, Integer status) {
        Page<StockIn> page = new Page<>(current, size);
        LambdaQueryWrapper<StockIn> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(StockIn::getInNo, keyword);
        }
        if (status != null) {
            wrapper.eq(StockIn::getStatus, status);
        }
        wrapper.orderByDesc(StockIn::getCreateTime);
        Page<StockIn> result = page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveStockIn(StockIn stockIn, List<StockInItem> items) {
        if (!StringUtils.hasText(stockIn.getInNo())) {
            stockIn.setInNo("SI" + IdUtil.getSnowflakeNextIdStr());
        }
        stockIn.setStatus(0);
        BigDecimal total = BigDecimal.ZERO;
        if (items != null) {
            for (StockInItem item : items) {
                if (item.getUnitPrice() != null && item.getQuantity() != null) {
                    total = total.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                }
            }
        }
        stockIn.setTotalAmount(total);
        save(stockIn);
        if (items != null && !items.isEmpty()) {
            for (StockInItem item : items) {
                item.setInId(stockIn.getId());
                stockInItemMapper.insert(item);
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean confirmStockIn(Long id) {
        StockIn stockIn = getById(id);
        if (stockIn == null || stockIn.getStatus() != 0) return false;
        
        List<StockInItem> items = getItems(id);
        for (StockInItem item : items) {
            Stock stock = new Stock();
            stock.setDrugId(item.getDrugId());
            stock.setBatchNo(item.getBatchNo());
            stock.setQuantity(item.getQuantity());
            stock.setUnitPrice(item.getUnitPrice());
            stock.setProductionDate(item.getProductionDate());
            stock.setExpiryDate(item.getExpiryDate());
            stock.setLocation(item.getLocation());
            stock.setStatus(1);
            stockService.addOrUpdateStock(stock);
        }
        
        stockIn.setStatus(1);
        stockIn.setInTime(LocalDateTime.now());
        return updateById(stockIn);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean cancelStockIn(Long id) {
        StockIn stockIn = getById(id);
        if (stockIn == null || stockIn.getStatus() != 0) return false;
        stockIn.setStatus(2);
        return updateById(stockIn);
    }

    public List<StockInItem> getItems(Long inId) {
        return stockInItemMapper.selectList(new LambdaQueryWrapper<StockInItem>().eq(StockInItem::getInId, inId));
    }
}
