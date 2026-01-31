package com.example.xianzhiyun.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.xianzhiyun.entity.DonationReview;
import com.example.xianzhiyun.mapper.DonationReviewMapper;
import com.example.xianzhiyun.mapper.DonationItemMapper;
import com.example.xianzhiyun.service.ReviewService;

import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private DonationReviewMapper reviewMapper;

    @Autowired
    private DonationItemMapper itemMapper;

    @Override
    public DonationReview getById(Long id) {
        return reviewMapper.selectById(id);
    }

    @Override
    public List<DonationReview> listByItemId(Long itemId) {
        return reviewMapper.selectByItemId(itemId);
    }

    @Override
    public List<DonationReview> list(Map<String, Object> params) {
        return reviewMapper.selectList(params);
    }

    @Override
    @Transactional
    public Long create(DonationReview review) {
        if (review == null || review.getItemId() == null) throw new IllegalArgumentException("invalid review");
        reviewMapper.insert(review);
        // 同步更新 donation_item 状态（如果需要）
        if ("APPROVED".equalsIgnoreCase(review.getReviewStatus())) {
            var item = itemMapper.selectById(review.getItemId());
            if (item != null) {
                item.setStatus("ON_SHELF");
                itemMapper.update(item);
            }
        } else if ("REJECTED".equalsIgnoreCase(review.getReviewStatus())) {
            var item = itemMapper.selectById(review.getItemId());
            if (item != null) {
                item.setStatus("REJECTED");
                itemMapper.update(item);
            }
        }
        return review.getId();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        reviewMapper.deleteById(id);
    }
}