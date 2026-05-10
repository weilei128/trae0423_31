package com.pharmacy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("drug_dict")
public class DrugDict {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String drugCode;
    private String genericName;
    private String tradeName;
    private String specification;
    private String dosageForm;
    private String unit;
    private String manufacturer;
    private String approvalNumber;
    private Long categoryId;
    private String storageCondition;
    private Integer warningLevel;
    private Integer minimumStock;
    private Integer maximumStock;
    private BigDecimal price;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
