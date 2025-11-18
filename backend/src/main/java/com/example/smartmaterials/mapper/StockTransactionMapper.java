package com.example.smartmaterials.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smartmaterials.entity.StockTransaction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockTransactionMapper extends BaseMapper<StockTransaction> {
}
