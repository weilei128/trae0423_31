package com.pharmacy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pharmacy.entity.StockOutItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockOutItemMapper extends BaseMapper<StockOutItem> {
}
