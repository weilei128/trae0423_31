package com.pharmacy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.common.PageResult;
import com.pharmacy.entity.ExpiryAlert;
import com.pharmacy.entity.Stock;
import com.pharmacy.mapper.ExpiryAlertMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ExpiryAlertService extends ServiceImpl<ExpiryAlertMapper, ExpiryAlert> {

    @Autowired
    private StockService stockService;

    public PageResult<ExpiryAlert> listPage(int current, int size, Integer status, Integer alertLevel) {
        Page<ExpiryAlert> page = new Page<>(current, size);
        LambdaQueryWrapper<ExpiryAlert> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(ExpiryAlert::getStatus, status);
        }
        if (alertLevel != null) {
            wrapper.eq(ExpiryAlert::getAlertLevel, alertLevel);
        }
        wrapper.orderByAsc(ExpiryAlert::getExpiryDate);
        Page<ExpiryAlert> result = page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void generateAlerts() {
        List<Stock> stocks = stockService.getExpirySoon(60);
        LocalDate today = LocalDate.now();
        
        for (Stock stock : stocks) {
            long days = ChronoUnit.DAYS.between(today, stock.getExpiryDate());
            int alertLevel = days <= 30 ? 1 : 2;
            
            ExpiryAlert exist = getOne(new LambdaQueryWrapper<ExpiryAlert>()
                    .eq(ExpiryAlert::getDrugId, stock.getDrugId())
                    .eq(ExpiryAlert::getBatchNo, stock.getBatchNo())
                    .eq(ExpiryAlert::getStatus, 0));
            
            if (exist == null) {
                ExpiryAlert alert = new ExpiryAlert();
                alert.setDrugId(stock.getDrugId());
                alert.setBatchNo(stock.getBatchNo());
                alert.setStockId(stock.getId());
                alert.setExpiryDate(stock.getExpiryDate());
                alert.setRemainingDays((int) days);
                alert.setQuantity(stock.getQuantity());
                alert.setAlertLevel(alertLevel);
                alert.setStatus(0);
                save(alert);
            } else {
                exist.setRemainingDays((int) days);
                exist.setAlertLevel(alertLevel);
                updateById(exist);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean handleAlert(Long id, Integer status, String handler, String remark) {
        ExpiryAlert alert = getById(id);
        if (alert == null || alert.getStatus() != 0) return false;
        alert.setStatus(status);
        alert.setHandler(handler);
        alert.setHandleTime(java.time.LocalDateTime.now());
        alert.setRemark(remark);
        return updateById(alert);
    }

    public List<ExpiryAlert> getPendingAlerts() {
        return list(new LambdaQueryWrapper<ExpiryAlert>().eq(ExpiryAlert::getStatus, 0).orderByAsc(ExpiryAlert::getExpiryDate));
    }
}
