package com.pharmacy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.common.PageResult;
import com.pharmacy.entity.Stock;
import com.pharmacy.mapper.StockMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class StockService extends ServiceImpl<StockMapper, Stock> {

    public PageResult<Stock> listPage(int current, int size, String keyword, Long drugId) {
        Page<Stock> page = new Page<>(current, size);
        LambdaQueryWrapper<Stock> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Stock::getBatchNo, keyword);
        }
        if (drugId != null) {
            wrapper.eq(Stock::getDrugId, drugId);
        }
        wrapper.orderByAsc(Stock::getExpiryDate);
        Page<Stock> result = page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent());
    }

    public List<Stock> getStockByDrug(Long drugId) {
        return list(new LambdaQueryWrapper<Stock>()
                .eq(Stock::getDrugId, drugId)
                .gt(Stock::getQuantity, 0)
                .orderByAsc(Stock::getExpiryDate));
    }

    public boolean addOrUpdateStock(Stock stock) {
        Stock exist = getOne(new LambdaQueryWrapper<Stock>()
                .eq(Stock::getDrugId, stock.getDrugId())
                .eq(Stock::getBatchNo, stock.getBatchNo()));
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + stock.getQuantity());
            return updateById(exist);
        } else {
            return save(stock);
        }
    }

    public boolean deductStock(Long drugId, String batchNo, int quantity) {
        return update(new LambdaUpdateWrapper<Stock>()
                .eq(Stock::getDrugId, drugId)
                .eq(Stock::getBatchNo, batchNo)
                .setSql("quantity = quantity - " + quantity));
    }

    public List<Stock> getExpirySoon(int days) {
        return list(new LambdaQueryWrapper<Stock>()
                .gt(Stock::getQuantity, 0)
                .apply("DATEDIFF(expiry_date, CURDATE()) <= " + days)
                .orderByAsc(Stock::getExpiryDate));
    }
}
