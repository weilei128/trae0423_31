package com.pharmacy.controller;

import com.pharmacy.common.PageResult;
import com.pharmacy.common.Result;
import com.pharmacy.entity.InventoryItem;
import com.pharmacy.entity.InventoryTask;
import com.pharmacy.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/page")
    public Result<PageResult<InventoryTask>> listPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(inventoryService.listPage(current, size, keyword, status));
    }

    @GetMapping("/{id}")
    public Result<InventoryTask> getById(@PathVariable Long id) {
        return Result.success(inventoryService.getById(id));
    }

    @PostMapping
    public Result<Boolean> create(@RequestBody InventoryTask task) {
        return Result.success(inventoryService.createTask(task));
    }

    @PostMapping("/{id}/start")
    public Result<Boolean> start(@PathVariable Long id) {
        return Result.success(inventoryService.startTask(id));
    }

    @PostMapping("/{id}/complete")
    public Result<Boolean> complete(@PathVariable Long id) {
        return Result.success(inventoryService.completeTask(id));
    }

    @GetMapping("/{taskId}/items")
    public Result<List<InventoryItem>> getItems(@PathVariable Long taskId) {
        return Result.success(inventoryService.getItems(taskId));
    }

    @GetMapping("/items/page")
    public Result<PageResult<InventoryItem>> listItemsPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Integer status) {
        return Result.success(inventoryService.listItemsPage(current, size, taskId, status));
    }

    @PutMapping("/item")
    public Result<Boolean> updateItem(@RequestBody InventoryItem item) {
        return Result.success(inventoryService.updateItem(item));
    }
}
