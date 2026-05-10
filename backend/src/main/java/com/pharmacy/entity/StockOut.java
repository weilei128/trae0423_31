package com.pharmacy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("stock_out")
public class StockOut {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String outNo;
    private Integer outType;
    private String applyDepartment;
    private String applicant;
    private String reviewer;
    private LocalDateTime reviewTime;
    private LocalDateTime outTime;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
