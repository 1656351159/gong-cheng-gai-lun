package com.example.smartmaterials.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smartmaterials.entity.Request;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface RequestMapper extends BaseMapper<Request> {
    @Select("""
            SELECT r.id, r.qty, r.purpose, r.project_no, r.status, r.comment,
                   r.created_at, r.reviewed_at, mi.brand, mi.model
            FROM request r
            JOIN sys_user u ON u.id = r.student_id
            JOIN material_item mi ON mi.id = r.item_id
            WHERE u.username=#{username}
            ORDER BY r.created_at DESC
            """)
    List<Map<String, Object>> selectByUsername(@Param("username") String username);

    @Select("""
            SELECT r.id, r.qty, r.purpose, r.project_no, r.status, r.comment,
                   r.created_at, r.reviewed_at, mi.brand, mi.model, u.username AS student
            FROM request r
            JOIN sys_user u ON u.id = r.student_id
            JOIN material_item mi ON mi.id = r.item_id
            ORDER BY r.created_at DESC
            """)
    List<Map<String, Object>> selectAllWithUser();

    @Select("""
            SELECT r.id,
                   mi.brand || ' ' || mi.model AS material,
                   r.qty, r.purpose, r.project_no,
                   r.status, u.username AS student,
                   r.created_at, r.reviewed_at
            FROM request r
            JOIN material_item mi ON mi.id = r.item_id
            JOIN sys_user u ON u.id = r.student_id
            ORDER BY r.created_at DESC
            """)
    List<Map<String, Object>> selectForExport();
}
