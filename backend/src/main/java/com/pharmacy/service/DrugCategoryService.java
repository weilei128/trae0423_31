package com.pharmacy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.entity.DrugCategory;
import com.pharmacy.mapper.DrugCategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrugCategoryService extends ServiceImpl<DrugCategoryMapper, DrugCategory> {

    public List<DrugCategory> listAll() {
        return list(new LambdaQueryWrapper<DrugCategory>().orderByAsc(DrugCategory::getSortOrder));
    }

    public boolean saveCategory(DrugCategory category) {
        return save(category);
    }

    public boolean updateCategory(DrugCategory category) {
        return updateById(category);
    }

    public boolean deleteById(Long id) {
        return removeById(id);
    }
}
