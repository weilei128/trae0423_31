package com.pharmacy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("stock_in_item")
public class StockInItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long inId;
    private Long drugId;
    private String batchNo;
    private Integer quantity;
    private BigDecimal unitPrice;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private String location;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
