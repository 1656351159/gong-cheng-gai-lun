package com.example.smartmaterials.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Request {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long itemId;
    private Integer qty;
    private String purpose;
    private String projectNo;
    private String status;
    private String comment;
    private Long studentId;
    private Long reviewerId;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
}
