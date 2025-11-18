package com.example.smartmaterials.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smartmaterials.entity.StockBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StockBatchMapper extends BaseMapper<StockBatch> {
    @Select("""
            <script>
            SELECT sb.id, sb.batch_code, sb.quantity, sb.expire_date, sb.barcode,
                   sb.created_at, mi.brand, mi.model
            FROM stock_batch sb
            JOIN material_item mi ON mi.id = sb.item_id
            <where>
                <if test="from != null"> sb.created_at &gt;= #{from} </if>
                <if test="to != null"> sb.created_at &lt;= #{to} </if>
            </where>
            ORDER BY sb.id
            </script>
            """)
    List<Map<String, Object>> selectWithItem(@Param("from") java.time.LocalDateTime from,
                                             @Param("to") java.time.LocalDateTime to);
}
