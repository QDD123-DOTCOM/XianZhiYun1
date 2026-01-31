package com.example.xianzhiyun.service;

import com.example.xianzhiyun.entity.DonationReview;

import java.util.List;
import java.util.Map;

public interface ReviewService {
    DonationReview getById(Long id);

    List<DonationReview> listByItemId(Long itemId);

    List<DonationReview> list(Map<String, Object> params);

    Long create(DonationReview review);

    void delete(Long id);
}