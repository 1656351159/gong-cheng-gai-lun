package com.example.smartmaterials.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.smartmaterials.entity.MaterialItem;
import com.example.smartmaterials.entity.StockBatch;
import com.example.smartmaterials.entity.StockTransaction;
import com.example.smartmaterials.mapper.MaterialItemMapper;
import com.example.smartmaterials.mapper.StockBatchMapper;
import com.example.smartmaterials.mapper.StockTransactionMapper;
import com.example.smartmaterials.model.dto.InboundRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class InboundService {
    private final StockBatchMapper stockBatchMapper;
    private final StockTransactionMapper stockTransactionMapper;
    private final MaterialItemMapper materialItemMapper;

    public InboundService(StockBatchMapper stockBatchMapper,
                          StockTransactionMapper stockTransactionMapper,
                          MaterialItemMapper materialItemMapper) {
        this.stockBatchMapper = stockBatchMapper;
        this.stockTransactionMapper = stockTransactionMapper;
        this.materialItemMapper = materialItemMapper;
    }

    @Transactional
    public void create(InboundRequest req) {
        // 确认材料存在
        MaterialItem item = materialItemMapper.selectById(req.getItemId());
        if (item == null) {
            throw new IllegalArgumentException("材料不存在");
        }
        String batchCode = req.getBatchCode();
        if (!StringUtils.hasText(batchCode)) {
            batchCode = "BATCH-" + System.currentTimeMillis();
        }
        LocalDate expire = null;
        if (StringUtils.hasText(req.getExpireDate())) {
            expire = LocalDate.parse(req.getExpireDate());
        }
        StockBatch batch = new StockBatch();
        batch.setItemId(req.getItemId());
        batch.setBatchCode(batchCode);
        batch.setQuantity(req.getQuantity());
        batch.setExpireDate(expire);
        batch.setBarcode(req.getBarcode());
        stockBatchMapper.insert(batch);

        StockTransaction txn = new StockTransaction();
        txn.setBatchId(batch.getId());
        txn.setTxnType("IN");
        txn.setQty(req.getQuantity());
        txn.setUsage("inbound");
        stockTransactionMapper.insert(txn);
    }

    public List<Map<String, Object>> list(String from, String to) {
        java.time.LocalDateTime fromDt = null;
        java.time.LocalDateTime toDt = null;
        if (StringUtils.hasText(from)) {
            fromDt = java.time.LocalDate.parse(from).atStartOfDay();
        }
        if (StringUtils.hasText(to)) {
            toDt = java.time.LocalDate.parse(to).atTime(23, 59, 59);
        }
        return stockBatchMapper.selectWithItem(fromDt, toDt);
    }
}
