package com.pharmacy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.entity.Department;
import com.pharmacy.mapper.DepartmentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService extends ServiceImpl<DepartmentMapper, Department> {

    public List<Department> listAll() {
        return list(new LambdaQueryWrapper<Department>().eq(Department::getStatus, 1).orderByAsc(Department::getSortOrder));
    }

    public boolean saveDept(Department dept) {
        return save(dept);
    }

    public boolean updateDept(Department dept) {
        return updateById(dept);
    }

    public boolean deleteById(Long id) {
        return removeById(id);
    }
}
