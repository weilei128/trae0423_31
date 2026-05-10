package com.pharmacy.dto;

import com.pharmacy.entity.PurchasePlan;
import com.pharmacy.entity.PurchasePlanItem;
import lombok.Data;
import java.util.List;

@Data
public class PurchasePlanDTO {
    private PurchasePlan plan;
    private List<PurchasePlanItem> items;
}
