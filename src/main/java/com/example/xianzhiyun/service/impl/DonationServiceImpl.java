package com.example.xianzhiyun.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.xianzhiyun.entity.DonationItem;
import com.example.xianzhiyun.mapper.DonationItemMapper;
import com.example.xianzhiyun.service.DonationService;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DonationServiceImpl implements DonationService {

    private static final Logger logger = LoggerFactory.getLogger(DonationServiceImpl.class);

    @Autowired
    private DonationItemMapper donationItemMapper;

    @Override
    public DonationItem getById(Long id) {
        return donationItemMapper.selectById(id);
    }

    @Override
    public List<DonationItem> list(Map<String, Object> params) {
        return donationItemMapper.selectList(params);
    }

    @Override
    public int count(Map<String, Object> params) {
        return donationItemMapper.selectCount(params);
    }

    @Autowired
    public DonationServiceImpl(DonationItemMapper donationItemMapper) {
        this.donationItemMapper = donationItemMapper;
    }

    @Override
    public long countDistinctEventsByUserId(Long userId) {
        if (userId == null) return 0L;
        Integer cnt = donationItemMapper.countDistinctEventsByUserId(userId);
        return cnt == null ? 0L : cnt.longValue();
    }

    @Override
    public int countByEventId(Long eventId) {
        if (eventId == null) return 0;
        Integer cnt = donationItemMapper.countByEventId(eventId);
        return cnt == null ? 0 : cnt;
    }

    @Override
    @Transactional
    public Long create(DonationItem item) {
        if (item == null) throw new IllegalArgumentException("item is null");
        if (item.getTitle() == null || item.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("title required");
        }
        // 默认状态
        if (item.getStatus() == null) item.setStatus("PENDING");
        donationItemMapper.insert(item);
        logger.info("create donation item id={}", item.getId());
        return item.getId();
    }
    @Override
    @Transactional
    public void updateStatus(Map<String, Object> params) {
        // params should contain id and status
        Object idObj = params.get("id");
        Object statusObj = params.get("status");
        if (idObj == null || statusObj == null) {
            throw new IllegalArgumentException("id or status required");
        }
        Long id = Long.parseLong(String.valueOf(idObj));
        String status = String.valueOf(statusObj);

        // 你现有 mapper 没有 updateStatus 单独方法，调用 select->update 或新增 mapper 方法
        DonationItem item = donationItemMapper.selectById(id);
        if (item == null) throw new IllegalArgumentException("item not found: " + id);
        item.setStatus(status);
        donationItemMapper.update(item);
    }

    @Override
    @Transactional
    public void update(DonationItem item) {
        if (item == null || item.getId() == null) throw new IllegalArgumentException("invalid item");
        DonationItem old = donationItemMapper.selectById(item.getId());
        if (old == null) throw new IllegalArgumentException("item not found: " + item.getId());
        donationItemMapper.update(item);
        logger.info("update donation item id={}", item.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        DonationItem old = donationItemMapper.selectById(id);
        if (old == null) throw new IllegalArgumentException("item not found: " + id);
        donationItemMapper.deleteById(id);
        logger.info("deleted donation item id={}", id);
    }
}