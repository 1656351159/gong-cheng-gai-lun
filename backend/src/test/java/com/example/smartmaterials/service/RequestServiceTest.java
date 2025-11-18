package com.example.smartmaterials.service;

import com.example.smartmaterials.entity.*;
import com.example.smartmaterials.mapper.*;
import com.example.smartmaterials.model.dto.RequestApprove;
import com.example.smartmaterials.model.dto.RequestCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RequestServiceTest {

    @Autowired
    private RequestService requestService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MaterialCategoryMapper categoryMapper;
    @Autowired
    private MaterialItemMapper itemMapper;
    @Autowired
    private StockBatchMapper batchMapper;
    @Autowired
    private RequestMapper requestMapper;

    private Long itemId;

    @BeforeEach
    void setup() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPasswordHash("123456");
        admin.setRole("admin");
        admin.setEnabled(true);
        userMapper.insert(admin);

        User stu = new User();
        stu.setUsername("stu");
        stu.setPasswordHash("123456");
        stu.setRole("student");
        stu.setEnabled(true);
        userMapper.insert(stu);

        MaterialCategory cat = new MaterialCategory();
        cat.setName("Chem");
        categoryMapper.insert(cat);

        MaterialItem item = new MaterialItem();
        item.setCategoryId(cat.getId());
        item.setBrand("BrandA");
        item.setModel("M1");
        item.setSpec("Spec");
        item.setUnitPrice(BigDecimal.valueOf(10));
        item.setCurrency("CNY");
        itemMapper.insert(item);
        itemId = item.getId();

        StockBatch batch = new StockBatch();
        batch.setItemId(itemId);
        batch.setBatchCode("B1");
        batch.setQuantity(5);
        batchMapper.insert(batch);
    }

    @Test
    void approveShouldDeductStockAndMarkApproved() {
        RequestCreate create = new RequestCreate();
        create.setItemId(itemId);
        create.setQuantity(3);
        create.setPurpose("test");
        requestService.create(create, "stu");

        Request req = requestMapper.selectList(null).get(0);
        assertNotNull(req);
        assertEquals("pending", req.getStatus());

        RequestApprove approve = new RequestApprove();
        approve.setComment("ok");
        requestService.approve(req.getId(), approve, "admin");

        Request updated = requestMapper.selectById(req.getId());
        assertEquals("approved", updated.getStatus());
        StockBatch batch = batchMapper.selectById( batchMapper.selectList(null).get(0).getId());
        assertEquals(2, batch.getQuantity());
    }

    @Test
    void rejectShouldNotChangeStock() {
        RequestCreate create = new RequestCreate();
        create.setItemId(itemId);
        create.setQuantity(2);
        create.setPurpose("test");
        requestService.create(create, "stu");

        Request req = requestMapper.selectList(null).get(0);
        RequestApprove approve = new RequestApprove();
        approve.setComment("no");
        requestService.reject(req.getId(), approve, "admin");

        Request updated = requestMapper.selectById(req.getId());
        assertEquals("rejected", updated.getStatus());
        StockBatch batch = batchMapper.selectList(null).get(0);
        assertEquals(5, batch.getQuantity());
    }

    @Test
    void createShouldFailWhenInsufficientStock() {
        RequestCreate create = new RequestCreate();
        create.setItemId(itemId);
        create.setQuantity(10);
        create.setPurpose("too much");
        assertThrows(IllegalArgumentException.class, () -> requestService.create(create, "stu"));
    }

    @Test
    void approveShouldFailWhenAlreadyHandled() {
        RequestCreate create = new RequestCreate();
        create.setItemId(itemId);
        create.setQuantity(2);
        create.setPurpose("first");
        requestService.create(create, "stu");
        Request req = requestMapper.selectList(null).get(0);

        RequestApprove approve = new RequestApprove();
        approve.setComment("ok");
        requestService.approve(req.getId(), approve, "admin");

        // second approval should fail
        RequestApprove approveAgain = new RequestApprove();
        approveAgain.setComment("again");
        assertThrows(IllegalArgumentException.class, () -> requestService.approve(req.getId(), approveAgain, "admin"));
    }

    @Test
    void rejectShouldFailWhenAlreadyHandled() {
        RequestCreate create = new RequestCreate();
        create.setItemId(itemId);
        create.setQuantity(1);
        create.setPurpose("reject first");
        requestService.create(create, "stu");
        Request req = requestMapper.selectList(null).get(0);

        RequestApprove reject = new RequestApprove();
        reject.setComment("nope");
        requestService.reject(req.getId(), reject, "admin");

        RequestApprove rejectAgain = new RequestApprove();
        rejectAgain.setComment("second");
        assertThrows(IllegalArgumentException.class, () -> requestService.reject(req.getId(), rejectAgain, "admin"));
    }
}
