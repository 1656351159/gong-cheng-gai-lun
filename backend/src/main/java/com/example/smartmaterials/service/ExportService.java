package com.example.smartmaterials.service;

import com.example.smartmaterials.mapper.MaterialItemMapper;
import com.example.smartmaterials.mapper.RequestMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Service
public class ExportService {
    private final RequestMapper requestMapper;
    private final MaterialItemMapper materialItemMapper;

    public ExportService(RequestMapper requestMapper, MaterialItemMapper materialItemMapper) {
        this.requestMapper = requestMapper;
        this.materialItemMapper = materialItemMapper;
    }

    public String exportRequests(String from, String to) {
        List<Map<String, Object>> rows = requestMapper.selectForExport();
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("ID,材料,数量,用途,项目号,状态,使用人,提交时间,审核时间");
        for (Map<String, Object> r : rows) {
            joiner.add(String.join(",",
                    v(r.get("id")),
                    v(r.get("material")),
                    v(r.get("qty")),
                    v(r.get("purpose")),
                    v(r.get("project_no")),
                    v(r.get("status")),
                    v(r.get("student")),
                    v(r.get("created_at")),
                    v(r.get("reviewed_at"))
            ));
        }
        return joiner.toString();
    }

    public String exportStock() {
        List<Map<String, Object>> rows = materialItemMapper.selectStockSummary(null);
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("ID,材料,规格,单价,币种,库存数量");
        for (Map<String, Object> r : rows) {
            joiner.add(String.join(",",
                    v(r.get("id")),
                    v(r.get("brand")) + " " + v(r.get("model")),
                    v(r.get("spec")),
                    v(r.get("unit_price")),
                    v(r.get("currency")),
                    v(r.get("quantity"))
            ));
        }
        return joiner.toString();
    }

    private String v(Object o) {
        return o == null ? "" : o.toString().replace(",", " ");
    }
}
