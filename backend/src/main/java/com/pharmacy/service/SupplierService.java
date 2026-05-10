package com.pharmacy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.common.PageResult;
import com.pharmacy.entity.Supplier;
import com.pharmacy.mapper.SupplierMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SupplierService extends ServiceImpl<SupplierMapper, Supplier> {

    public PageResult<Supplier> listPage(int current, int size, String keyword) {
        Page<Supplier> page = new Page<>(current, size);
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Supplier::getSupplierName, keyword)
                    .or().like(Supplier::getSupplierCode, keyword)
                    .or().like(Supplier::getContactPerson, keyword));
        }
        wrapper.orderByDesc(Supplier::getCreateTime);
        Page<Supplier> result = page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent());
    }

    public List<Supplier> listAll() {
        return list(new LambdaQueryWrapper<Supplier>().eq(Supplier::getStatus, 1));
    }

    public boolean saveSupplier(Supplier supplier) {
        return save(supplier);
    }

    public boolean updateSupplier(Supplier supplier) {
        return updateById(supplier);
    }

    public boolean deleteById(Long id) {
        return removeById(id);
    }
}
