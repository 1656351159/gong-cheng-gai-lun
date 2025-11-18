package com.example.smartmaterials.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.smartmaterials.entity.MaterialItem;
import com.example.smartmaterials.model.dto.MaterialRequest;
import com.example.smartmaterials.mapper.MaterialItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MaterialService {
    private final MaterialItemMapper materialMapper;

    public MaterialService(MaterialItemMapper materialMapper) {
        this.materialMapper = materialMapper;
    }

    public List<MaterialItem> list(String keyword) {
        LambdaQueryWrapper<MaterialItem> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.like(MaterialItem::getBrand, keyword).or().like(MaterialItem::getModel, keyword);
        }
        qw.orderByAsc(MaterialItem::getId);
        return materialMapper.selectList(qw);
    }

    @Transactional
    public void create(MaterialRequest req) {
        MaterialItem item = new MaterialItem();
        item.setCategoryId(req.getCategoryId());
        item.setBrand(req.getBrand());
        item.setModel(req.getModel());
        item.setSpec(req.getSpec());
        item.setUnitPrice(req.getUnitPrice());
        item.setCurrency(req.getCurrency());
        materialMapper.insert(item);
    }

    @Transactional
    public void update(Long id, MaterialRequest req) {
        MaterialItem item = new MaterialItem();
        item.setId(id);
        item.setCategoryId(req.getCategoryId());
        item.setBrand(req.getBrand());
        item.setModel(req.getModel());
        item.setSpec(req.getSpec());
        item.setUnitPrice(req.getUnitPrice());
        item.setCurrency(req.getCurrency());
        materialMapper.updateById(item);
    }

    @Transactional
    public void delete(Long id) {
        materialMapper.deleteById(id);
    }
}
