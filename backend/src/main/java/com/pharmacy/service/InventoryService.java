package com.pharmacy.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.common.PageResult;
import com.pharmacy.entity.InventoryItem;
import com.pharmacy.entity.InventoryTask;
import com.pharmacy.entity.Stock;
import com.pharmacy.mapper.InventoryItemMapper;
import com.pharmacy.mapper.InventoryTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class InventoryService extends ServiceImpl<InventoryTaskMapper, InventoryTask> {

    @Autowired
    private InventoryItemMapper inventoryItemMapper;

    @Autowired
    private StockService stockService;

    public PageResult<InventoryTask> listPage(int current, int size, String keyword, Integer status) {
        Page<InventoryTask> page = new Page<>(current, size);
        LambdaQueryWrapper<InventoryTask> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(InventoryTask::getTaskNo, keyword)
                    .or().like(InventoryTask::getTaskName, keyword));
        }
        if (status != null) {
            wrapper.eq(InventoryTask::getStatus, status);
        }
        wrapper.orderByDesc(InventoryTask::getCreateTime);
        Page<InventoryTask> result = page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createTask(InventoryTask task) {
        if (!StringUtils.hasText(task.getTaskNo())) {
            task.setTaskNo("IT" + IdUtil.getSnowflakeNextIdStr());
        }
        task.setStatus(0);
        save(task);
        
        List<Stock> stocks = stockService.list(new LambdaQueryWrapper<Stock>().gt(Stock::getQuantity, 0));
        for (Stock stock : stocks) {
            InventoryItem item = new InventoryItem();
            item.setTaskId(task.getId());
            item.setDrugId(stock.getDrugId());
            item.setBatchNo(stock.getBatchNo());
            item.setSystemQuantity(stock.getQuantity());
            item.setStatus(0);
            inventoryItemMapper.insert(item);
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean startTask(Long id) {
        InventoryTask task = getById(id);
        if (task == null || task.getStatus() != 0) return false;
        task.setStatus(1);
        return updateById(task);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateItem(InventoryItem item) {
        if (item.getActualQuantity() != null && item.getSystemQuantity() != null) {
            item.setDiffQuantity(item.getActualQuantity() - item.getSystemQuantity());
        }
        if (item.getStatus() == null) {
            item.setStatus(1);
        }
        return inventoryItemMapper.updateById(item) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean completeTask(Long id) {
        InventoryTask task = getById(id);
        if (task == null || task.getStatus() != 1) return false;
        task.setStatus(2);
        return updateById(task);
    }

    public List<InventoryItem> getItems(Long taskId) {
        return inventoryItemMapper.selectList(new LambdaQueryWrapper<InventoryItem>().eq(InventoryItem::getTaskId, taskId));
    }

    public PageResult<InventoryItem> listItemsPage(int current, int size, Long taskId, Integer status) {
        Page<InventoryItem> page = new Page<>(current, size);
        LambdaQueryWrapper<InventoryItem> wrapper = new LambdaQueryWrapper<>();
        if (taskId != null) {
            wrapper.eq(InventoryItem::getTaskId, taskId);
        }
        if (status != null) {
            wrapper.eq(InventoryItem::getStatus, status);
        }
        Page<InventoryItem> result = inventoryItemMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent());
    }
}
