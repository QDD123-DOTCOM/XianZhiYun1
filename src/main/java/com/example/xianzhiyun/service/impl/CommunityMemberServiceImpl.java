package com.example.xianzhiyun.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.xianzhiyun.mapper.CommunityMemberMapper;
import com.example.xianzhiyun.mapper.CommunityMapper;
import com.example.xianzhiyun.entity.CommunityMember;
import com.example.xianzhiyun.service.CommunityMemberService;

import java.sql.Timestamp;
import java.util.List;

@Service
public class CommunityMemberServiceImpl implements CommunityMemberService {

    @Autowired
    private CommunityMemberMapper memberMapper;

    @Autowired
    private CommunityMapper communityMapper;

    @Override
    public boolean isMember(Long communityId, Long userId) {
        if (communityId == null || userId == null) return false;
        return memberMapper.exists(communityId, userId) > 0;
    }

    @Override
    @Transactional
    public boolean join(Long communityId, Long userId) {
        if (communityId == null || userId == null) return false;
        if (isMember(communityId, userId)) return true;

        CommunityMember m = new CommunityMember();
        m.setCommunityId(communityId);
        m.setUserId(userId);
        m.setRole("MEMBER");
        m.setJoinTime(new Timestamp(System.currentTimeMillis()));
        int r = memberMapper.insert(m);
        if (r > 0) {
            communityMapper.incrementMemberCount(communityId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean leave(Long communityId, Long userId) {
        int del = memberMapper.delete(communityId, userId);
        if (del > 0) {
            communityMapper.decrementMemberCount(communityId);
            return true;
        }
        return false;
    }

    @Override
    public List<Long> listMemberIds(Long communityId, int limit) {
        return memberMapper.selectUserIdsByCommunity(communityId, limit);
    }
}