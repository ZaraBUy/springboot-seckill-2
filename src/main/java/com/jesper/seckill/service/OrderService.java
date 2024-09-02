package com.jesper.seckill.service;

import com.jesper.seckill.bean.OrderInfo;
import com.jesper.seckill.bean.SeckillOrder;
import com.jesper.seckill.bean.User;
import com.jesper.seckill.constants.ServiceConstant;
import com.jesper.seckill.mapper.OrderMapper;
import com.jesper.seckill.redis.GoodsKey;
import com.jesper.seckill.redis.OrderKey;
import com.jesper.seckill.redis.RedisService;
import com.jesper.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by jiangyunxiong on 2018/5/23.
 */
@Service
public class OrderService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    RedisService redisService;

    public Long getOrderByUserIdGoodsId(long userId, long goodsId) {
        return redisService.get(OrderKey.getSeckillOrderByUidGid, "_" + userId + "_" + goodsId, Long.class);
    }

    public Integer trySeckillOrder(long userId, long goodsId) {
        String key = OrderKey.getSeckillOrderByUidGid.getPrefix() + "_" + userId + "_" + goodsId;
        String stockKey = GoodsKey.getGoodsStock.getPrefix() + "_" + goodsId;
        List<String> keys = Arrays.asList(key, stockKey);
        return redisService.executeLua(ServiceConstant.SECKILL_ORDER_LUA, keys, new ArrayList<String>(), Integer.class);
    }

    public OrderInfo getOrderById(long orderId) {
        return orderMapper.getOrderById(orderId);
    }

    /**
     * 因为要同时分别在订单详情表和秒杀订单表都新增一条数据，所以要保证两个操作是一个事物
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo createOrder(User user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getGoodsPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderMapper.insert(orderInfo);

        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getId());
        orderMapper.insertSeckillOrder(seckillOrder);

        redisService.set(OrderKey.getSeckillOrderByUidGid, "_" + user.getId() + "_" + goods.getId(), seckillOrder.getOrderId());

        return orderInfo;
    }


}
