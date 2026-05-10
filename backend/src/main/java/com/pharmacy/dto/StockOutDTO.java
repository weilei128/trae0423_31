package com.pharmacy.dto;

import com.pharmacy.entity.StockOut;
import com.pharmacy.entity.StockOutItem;
import lombok.Data;
import java.util.List;

@Data
public class StockOutDTO {
    private StockOut stockOut;
    private List<StockOutItem> items;
}
