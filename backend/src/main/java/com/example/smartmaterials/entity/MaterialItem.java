package com.example.smartmaterials.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MaterialItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String brand;
    private String model;
    private String spec;
    private BigDecimal unitPrice;
    private String currency;
    private String extra;
    private LocalDateTime createdAt;
}
