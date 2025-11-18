package com.example.smartmaterials.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smartmaterials.entity.MaterialItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface MaterialItemMapper extends BaseMapper<MaterialItem> {
    @Select("""
            <script>
            SELECT mi.id, mi.brand, mi.model, mi.spec, mi.unit_price, mi.currency,
                   COALESCE(SUM(sb.quantity),0) AS quantity
            FROM material_item mi
            LEFT JOIN stock_batch sb ON sb.item_id = mi.id
            <where>
                <if test="keyword != null and keyword != ''">
                    (mi.brand ILIKE #{keyword} OR mi.model ILIKE #{keyword})
                </if>
            </where>
            GROUP BY mi.id, mi.brand, mi.model, mi.spec, mi.unit_price, mi.currency
            ORDER BY mi.id
            </script>
            """)
    List<Map<String, Object>> selectStockSummary(@Param("keyword") String keyword);
}
