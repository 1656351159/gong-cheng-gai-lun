package com.example.smartmaterials.service;

import com.example.smartmaterials.mapper.MaterialItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
public class StockService {
    private final MaterialItemMapper materialItemMapper;

    public StockService(MaterialItemMapper materialItemMapper) {
        this.materialItemMapper = materialItemMapper;
    }

    public List<Map<String, Object>> list(String keyword) {
        String kw = null;
        if (StringUtils.hasText(keyword)) {
            kw = "%" + keyword + "%";
        }
        return materialItemMapper.selectStockSummary(kw);
    }
}
