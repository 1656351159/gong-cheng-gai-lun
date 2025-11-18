package com.example.smartmaterials.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.smartmaterials.entity.MaterialCategory;
import com.example.smartmaterials.model.dto.CategoryRequest;
import com.example.smartmaterials.mapper.MaterialCategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {
    private final MaterialCategoryMapper categoryMapper;

    public CategoryService(MaterialCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public List<MaterialCategory> listAll() {
        return categoryMapper.selectList(new LambdaQueryWrapper<MaterialCategory>().orderByAsc(MaterialCategory::getId));
    }

    @Transactional
    public void create(CategoryRequest req) {
        MaterialCategory category = new MaterialCategory();
        category.setParentId(req.getParentId());
        category.setName(req.getName());
        category.setSafeStock(req.getSafeStock());
        category.setUnit(req.getUnit());
        categoryMapper.insert(category);
    }

    @Transactional
    public void update(Long id, CategoryRequest req) {
        MaterialCategory category = new MaterialCategory();
        category.setId(id);
        category.setParentId(req.getParentId());
        category.setName(req.getName());
        category.setSafeStock(req.getSafeStock());
        category.setUnit(req.getUnit());
        categoryMapper.updateById(category);
    }

    @Transactional
    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }
}
