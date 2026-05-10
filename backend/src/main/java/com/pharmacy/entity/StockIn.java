package com.pharmacy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("stock_in")
public class StockIn {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String inNo;
    private Integer inType;
    private Integer sourceType;
    private Long sourceId;
    private Long supplierId;
    private BigDecimal totalAmount;
    private String warehouseKeeper;
    private LocalDateTime inTime;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
