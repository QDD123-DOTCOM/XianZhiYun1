package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.entity.OrderAddress;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderAddressMapper {
    void insert(OrderAddress address);
    OrderAddress selectById(Long id);
}