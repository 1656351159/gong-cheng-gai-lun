package com.example.smartmaterials.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.smartmaterials.entity.Request;
import com.example.smartmaterials.entity.StockBatch;
import com.example.smartmaterials.entity.StockTransaction;
import com.example.smartmaterials.entity.User;
import com.example.smartmaterials.mapper.RequestMapper;
import com.example.smartmaterials.mapper.StockBatchMapper;
import com.example.smartmaterials.mapper.StockTransactionMapper;
import com.example.smartmaterials.mapper.UserMapper;
import com.example.smartmaterials.model.dto.RequestApprove;
import com.example.smartmaterials.model.dto.RequestCreate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class RequestService {
    private final RequestMapper requestMapper;
    private final UserMapper userMapper;
    private final StockBatchMapper stockBatchMapper;
    private final StockTransactionMapper stockTransactionMapper;

    public RequestService(RequestMapper requestMapper,
                          UserMapper userMapper,
                          StockBatchMapper stockBatchMapper,
                          StockTransactionMapper stockTransactionMapper) {
        this.requestMapper = requestMapper;
        this.userMapper = userMapper;
        this.stockBatchMapper = stockBatchMapper;
        this.stockTransactionMapper = stockTransactionMapper;
    }

    @Transactional
    public void create(RequestCreate req, String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        Integer available = stockBatchMapper.selectList(new LambdaQueryWrapper<StockBatch>()
                        .eq(StockBatch::getItemId, req.getItemId()))
                .stream()
                .map(StockBatch::getQuantity)
                .filter(q -> q != null)
                .reduce(0, Integer::sum);
        if (available < req.getQuantity()) {
            throw new IllegalArgumentException("库存不足");
        }
        Request entity = new Request();
        entity.setItemId(req.getItemId());
        entity.setQty(req.getQuantity());
        entity.setPurpose(req.getPurpose());
        entity.setProjectNo(req.getProjectNo());
        entity.setStatus("pending");
        entity.setStudentId(user.getId());
        requestMapper.insert(entity);
    }

    public List<Map<String, Object>> listByUser(String username) {
        return requestMapper.selectByUsername(username);
    }

    public List<Map<String, Object>> listAll() {
        return requestMapper.selectAllWithUser();
    }

    @Transactional
    public void approve(Long id, RequestApprove req, String reviewer) {
        User reviewerUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, reviewer));
        if (reviewerUser == null) {
            throw new IllegalArgumentException("审核人不存在");
        }
        Request entity = requestMapper.selectById(id);
        if (entity == null) {
            throw new IllegalArgumentException("申请不存在");
        }
        if (!"pending".equals(entity.getStatus())) {
            throw new IllegalArgumentException("已处理的申请不可重复操作");
        }
        int available = stockBatchMapper.selectList(new LambdaQueryWrapper<StockBatch>()
                        .eq(StockBatch::getItemId, entity.getItemId())
                        .gt(StockBatch::getQuantity, 0)
                        .orderByAsc(StockBatch::getId))
                .stream()
                .map(StockBatch::getQuantity)
                .filter(q -> q != null)
                .reduce(0, Integer::sum);
        if (entity.getQty() > available) {
            throw new IllegalArgumentException("库存不足");
        }
        // 扣减库存，按批次 FIFO
        List<StockBatch> batches = stockBatchMapper.selectList(new LambdaQueryWrapper<StockBatch>()
                .eq(StockBatch::getItemId, entity.getItemId())
                .gt(StockBatch::getQuantity, 0)
                .orderByAsc(StockBatch::getId));
        int remaining = entity.getQty();
        for (StockBatch b : batches) {
            if (remaining <= 0) break;
            int qty = b.getQuantity() == null ? 0 : b.getQuantity();
            int deduct = Math.min(qty, remaining);
            StockBatch update = new StockBatch();
            update.setId(b.getId());
            update.setQuantity(qty - deduct);
            stockBatchMapper.updateById(update);

            StockTransaction txn = new StockTransaction();
            txn.setBatchId(b.getId());
            txn.setTxnType("OUT");
            txn.setQty(deduct);
            txn.setUsage("request approve");
            stockTransactionMapper.insert(txn);

            remaining -= deduct;
        }
        Request update = new Request();
        update.setId(id);
        update.setStatus("approved");
        update.setComment(req.getComment());
        update.setReviewerId(reviewerUser.getId());
        update.setReviewedAt(java.time.LocalDateTime.now());
        requestMapper.updateById(update);
    }

    @Transactional
    public void reject(Long id, RequestApprove req, String reviewer) {
        User reviewerUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, reviewer));
        if (reviewerUser == null) {
            throw new IllegalArgumentException("审核人不存在");
        }
        Request entity = requestMapper.selectById(id);
        if (entity == null) {
            throw new IllegalArgumentException("申请不存在");
        }
        if (!"pending".equals(entity.getStatus())) {
            throw new IllegalArgumentException("已处理的申请不可重复操作");
        }
        Request update = new Request();
        update.setId(id);
        update.setStatus("rejected");
        update.setComment(req.getComment());
        update.setReviewerId(reviewerUser.getId());
        update.setReviewedAt(java.time.LocalDateTime.now());
        requestMapper.updateById(update);
    }
}
