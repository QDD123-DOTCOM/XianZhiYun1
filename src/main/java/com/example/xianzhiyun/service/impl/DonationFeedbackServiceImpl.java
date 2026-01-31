package com.example.xianzhiyun.service.impl;

import com.example.xianzhiyun.entity.DonationFeedback;
import com.example.xianzhiyun.mapper.DonationFeedbackMapper;
import com.example.xianzhiyun.service.DonationFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DonationFeedbackServiceImpl implements DonationFeedbackService {

    @Autowired
    private DonationFeedbackMapper feedbackMapper;

    @Override
    public List<DonationFeedback> listByEventId(Long eventId, Integer limit) {
        Map<String,Object> params = new HashMap<>();
        params.put("eventId", eventId);
        if (limit != null) params.put("limit", limit);
        return feedbackMapper.selectList(params);
    }

    @Override
    @Transactional
    public Long create(DonationFeedback feedback) {
        feedbackMapper.insert(feedback);
        return feedback.getId();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        feedbackMapper.deleteById(id);
    }
}