package com.pharmacy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.common.PageResult;
import com.pharmacy.entity.DrugDict;
import com.pharmacy.mapper.DrugDictMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class DrugDictService extends ServiceImpl<DrugDictMapper, DrugDict> {

    public PageResult<DrugDict> listPage(int current, int size, String keyword, Long categoryId) {
        Page<DrugDict> page = new Page<>(current, size);
        LambdaQueryWrapper<DrugDict> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(DrugDict::getGenericName, keyword)
                    .or().like(DrugDict::getTradeName, keyword)
                    .or().like(DrugDict::getDrugCode, keyword));
        }
        if (categoryId != null) {
            wrapper.eq(DrugDict::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(DrugDict::getCreateTime);
        Page<DrugDict> result = page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent());
    }

    public List<DrugDict> listAll() {
        return list(new LambdaQueryWrapper<DrugDict>().eq(DrugDict::getDeleted, 0));
    }

    public DrugDict getById(Long id) {
        return super.getById(id);
    }

    public boolean saveDrug(DrugDict drug) {
        return save(drug);
    }

    public boolean updateDrug(DrugDict drug) {
        return updateById(drug);
    }

    public boolean deleteById(Long id) {
        return removeById(id);
    }
}
