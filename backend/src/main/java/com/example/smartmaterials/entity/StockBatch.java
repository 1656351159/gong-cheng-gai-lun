package com.example.smartmaterials.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StockBatch {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long itemId;
    private String batchCode;
    private Integer quantity;
    private LocalDate expireDate;
    private String barcode;
    private LocalDateTime createdAt;
}
