package com.pharmacy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.entity.WarehouseLocation;
import com.pharmacy.mapper.WarehouseLocationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseLocationService extends ServiceImpl<WarehouseLocationMapper, WarehouseLocation> {

    public List<WarehouseLocation> listAll() {
        return list(new LambdaQueryWrapper<WarehouseLocation>().eq(WarehouseLocation::getStatus, 1).orderByAsc(WarehouseLocation::getSortOrder));
    }

    public boolean saveLocation(WarehouseLocation location) {
        return save(location);
    }

    public boolean updateLocation(WarehouseLocation location) {
        return updateById(location);
    }

    public boolean deleteById(Long id) {
        return removeById(id);
    }
}
