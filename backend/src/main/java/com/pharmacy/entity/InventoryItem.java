package com.pharmacy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("inventory_item")
public class InventoryItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private Long drugId;
    private String batchNo;
    private Integer systemQuantity;
    private Integer actualQuantity;
    private Integer diffQuantity;
    private String diffReason;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
