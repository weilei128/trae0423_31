package com.pharmacy.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.common.PageResult;
import com.pharmacy.entity.PurchasePlan;
import com.pharmacy.entity.PurchasePlanItem;
import com.pharmacy.mapper.PurchasePlanItemMapper;
import com.pharmacy.mapper.PurchasePlanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PurchaseService extends ServiceImpl<PurchasePlanMapper, PurchasePlan> {

    @Autowired
    private PurchasePlanItemMapper purchasePlanItemMapper;

    public PageResult<PurchasePlan> listPage(int current, int size, String keyword, Integer status) {
        Page<PurchasePlan> page = new Page<>(current, size);
        LambdaQueryWrapper<PurchasePlan> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(PurchasePlan::getPlanNo, keyword)
                    .or().like(PurchasePlan::getPlanName, keyword));
        }
        if (status != null) {
            wrapper.eq(PurchasePlan::getStatus, status);
        }
        wrapper.orderByDesc(PurchasePlan::getCreateTime);
        Page<PurchasePlan> result = page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean savePlan(PurchasePlan plan, List<PurchasePlanItem> items) {
        if (!StringUtils.hasText(plan.getPlanNo())) {
            plan.setPlanNo("PO" + IdUtil.getSnowflakeNextIdStr());
        }
        plan.setStatus(0);
        save(plan);
        if (items != null && !items.isEmpty()) {
            for (PurchasePlanItem item : items) {
                item.setPlanId(plan.getId());
                purchasePlanItemMapper.insert(item);
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updatePlan(PurchasePlan plan, List<PurchasePlanItem> items) {
        updateById(plan);
        if (items != null && !items.isEmpty()) {
            purchasePlanItemMapper.delete(new LambdaQueryWrapper<PurchasePlanItem>().eq(PurchasePlanItem::getPlanId, plan.getId()));
            for (PurchasePlanItem item : items) {
                item.setId(null);
                item.setPlanId(plan.getId());
                purchasePlanItemMapper.insert(item);
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean submitPlan(Long id) {
        PurchasePlan plan = getById(id);
        if (plan == null) return false;
        plan.setStatus(1);
        return updateById(plan);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean auditPlan(Long id, Integer status) {
        PurchasePlan plan = getById(id);
        if (plan == null) return false;
        plan.setStatus(status);
        return updateById(plan);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean cancelPlan(Long id) {
        PurchasePlan plan = getById(id);
        if (plan == null) return false;
        plan.setStatus(3);
        return updateById(plan);
    }

    public List<PurchasePlanItem> getItems(Long planId) {
        return purchasePlanItemMapper.selectList(new LambdaQueryWrapper<PurchasePlanItem>().eq(PurchasePlanItem::getPlanId, planId));
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deletePlan(Long id) {
        purchasePlanItemMapper.delete(new LambdaQueryWrapper<PurchasePlanItem>().eq(PurchasePlanItem::getPlanId, id));
        return removeById(id);
    }
}
