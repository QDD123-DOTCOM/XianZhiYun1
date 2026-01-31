package com.example.xianzhiyun.service.impl;

import com.example.xianzhiyun.entity.DonationEvent;
import com.example.xianzhiyun.mapper.DonationEventMapper;
import com.example.xianzhiyun.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    private final DonationEventMapper eventMapper;

    @Autowired
    public EventServiceImpl(DonationEventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    // donation_event 相关（保持原有行为）
    @Override
    public DonationEvent getById(Long id) {
        return eventMapper.selectById(id);
    }

    @Override
    public List<DonationEvent> list(Map<String, Object> params) {
        return eventMapper.selectList(params);
    }

    @Override
    @Transactional
    public Long create(DonationEvent event) {
        eventMapper.insert(event);
        return event.getId();
    }

    @Override
    @Transactional
    public void update(DonationEvent event) {
        eventMapper.update(event);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        eventMapper.deleteById(id);
    }
    @Override
    public long countParticipantsByEventId(Long eventId) {
        if (eventId == null) return 0L;
        try {
            // 依赖 donation_item 表统计不同 donor_id 的数量
            // 需要在 DonationItemMapper 中增加 countParticipantsByEventId 方法：
            // <select id="countParticipantsByEventId" resultType="int" parameterType="long">
            //   SELECT COUNT(DISTINCT donor_id) FROM donation_item WHERE event_id = #{eventId}
            // </select>
            Integer cnt = eventMapper.countExchangeByEventId(eventId) != null
                    ? (Integer) eventMapper.countExchangeByEventId(eventId).getOrDefault("exchangeCount", 0)
                    : null;
            // 如果你已经添加了 DonationItemMapper.countParticipantsByEventId，请改为调用它：
            // Integer cnt = donationItemMapper.countParticipantsByEventId(eventId);
            return cnt == null ? 0L : cnt.longValue();
        } catch (Exception e) {
            logger.warn("countParticipantsByEventId failed for eventId={}: {}", eventId, e.getMessage());
            return 0L;
        }
    }

    @Override
    public List<DonationEvent> listAdmin(Map<String, Object> params) {
        return eventMapper.selectAdminList(params);
    }

    @Override
    public int countAdmin(Map<String, Object> params) {
        return eventMapper.countAdmin(params);
    }

    @Override
    public List<DonationEvent> listPending(Map<String, Object> params) {
        return eventMapper.selectPendingList(params);
    }

    @Override
    public int countPending(Map<String, Object> params) {
        return eventMapper.countPending(params);
    }

    @Override
    @Transactional
    public void applyReviewResult(Long eventId, String action, String comment) {
        DonationEvent e = eventMapper.selectById(eventId);
        if (e == null) return;
        if ("APPROVED".equalsIgnoreCase(action) || "APPROVE".equalsIgnoreCase(action)) {
            e.setStatus("PUBLISHED");
        } else if ("REJECTED".equalsIgnoreCase(action) || "REJECT".equalsIgnoreCase(action)) {
            e.setStatus("DRAFT");
        }
        eventMapper.update(e);
    }

    @Override
    public Map<String, Object> countExchangeByEventId(Long eventId) {
        Map<String,Object> m = eventMapper.countExchangeByEventId(eventId);
        if (m == null) {
            m = new HashMap<>();
            m.put("exchangeCount", 0);
        } else {
            m.putIfAbsent("exchangeCount", 0);
        }
        return m;
    }

    // 新增：兼容接口（不依赖 Activity），直接返回 DonationEvent(s)
    @Override
    public DonationEvent getActivityById(Long id) {
        return eventMapper.selectById(id);
    }

    @Override
    public List<DonationEvent> selectOngoing() {
        return eventMapper.selectOngoing();
    }
}