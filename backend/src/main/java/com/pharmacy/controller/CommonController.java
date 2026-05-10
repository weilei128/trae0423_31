package com.pharmacy.controller;

import com.pharmacy.common.Result;
import com.pharmacy.entity.Department;
import com.pharmacy.entity.Supplier;
import com.pharmacy.entity.WarehouseLocation;
import com.pharmacy.service.DepartmentService;
import com.pharmacy.service.SupplierService;
import com.pharmacy.service.WarehouseLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/common")
public class CommonController {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private WarehouseLocationService warehouseLocationService;

    @GetMapping("/supplier/page")
    public Result<com.pharmacy.common.PageResult<Supplier>> listSupplierPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.success(supplierService.listPage(current, size, keyword));
    }

    @GetMapping("/supplier/list")
    public Result<List<Supplier>> listSupplier() {
        return Result.success(supplierService.listAll());
    }

    @PostMapping("/supplier")
    public Result<Boolean> saveSupplier(@RequestBody Supplier supplier) {
        return Result.success(supplierService.saveSupplier(supplier));
    }

    @PutMapping("/supplier")
    public Result<Boolean> updateSupplier(@RequestBody Supplier supplier) {
        return Result.success(supplierService.updateSupplier(supplier));
    }

    @DeleteMapping("/supplier/{id}")
    public Result<Boolean> deleteSupplier(@PathVariable Long id) {
        return Result.success(supplierService.deleteById(id));
    }

    @GetMapping("/department/list")
    public Result<List<Department>> listDepartment() {
        return Result.success(departmentService.listAll());
    }

    @PostMapping("/department")
    public Result<Boolean> saveDepartment(@RequestBody Department dept) {
        return Result.success(departmentService.saveDept(dept));
    }

    @PutMapping("/department")
    public Result<Boolean> updateDepartment(@RequestBody Department dept) {
        return Result.success(departmentService.updateDept(dept));
    }

    @DeleteMapping("/department/{id}")
    public Result<Boolean> deleteDepartment(@PathVariable Long id) {
        return Result.success(departmentService.deleteById(id));
    }

    @GetMapping("/location/list")
    public Result<List<WarehouseLocation>> listLocation() {
        return Result.success(warehouseLocationService.listAll());
    }

    @PostMapping("/location")
    public Result<Boolean> saveLocation(@RequestBody WarehouseLocation location) {
        return Result.success(warehouseLocationService.saveLocation(location));
    }

    @PutMapping("/location")
    public Result<Boolean> updateLocation(@RequestBody WarehouseLocation location) {
        return Result.success(warehouseLocationService.updateLocation(location));
    }

    @DeleteMapping("/location/{id}")
    public Result<Boolean> deleteLocation(@PathVariable Long id) {
        return Result.success(warehouseLocationService.deleteById(id));
    }
}
