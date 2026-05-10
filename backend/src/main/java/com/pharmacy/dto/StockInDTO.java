package com.pharmacy.dto;

import com.pharmacy.entity.StockIn;
import com.pharmacy.entity.StockInItem;
import lombok.Data;
import java.util.List;

@Data
public class StockInDTO {
    private StockIn stockIn;
    private List<StockInItem> items;
}
