package com.example.smartmaterials.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockTransaction {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long batchId;
    private String txnType;
    private Integer qty;
    private Long userId;
    private String projectNo;
    private String usage;
    private LocalDateTime createdAt;
}
