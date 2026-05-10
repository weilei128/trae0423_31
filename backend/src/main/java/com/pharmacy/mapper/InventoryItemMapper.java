package com.pharmacy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pharmacy.entity.InventoryItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InventoryItemMapper extends BaseMapper<InventoryItem> {
}
