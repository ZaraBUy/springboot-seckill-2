package com.jesper.seckill.service;

import com.jesper.seckill.bean.OrderInfo;
import com.jesper.seckill.bean.SeckillOrder;
import com.jesper.seckill.bean.User;
import com.jesper.seckill.exception.ReduceInventoryException;
import com.jesper.seckill.redis.RedisService;
import com.jesper.seckill.redis.SeckillKey;
import com.jesper.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiangyunxiong on 2018/5/23.
 */
@Service
public class SeckillService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    //保证这三个操作，减库存 下订单 写入秒杀订单是一个事物
    @Transactional
    public OrderInfo seckill(User user, GoodsVo goods){
        //减库存
        if (goodsService.reduceStock(goods)){
            //下订单 写入秒杀订单
            return orderService.createOrder(user, goods);
        } else {
            throw new ReduceInventoryException(404, "减库存失败");
        }
    }

    public long getSeckillResult(long userId, long goodsId){
        Long orderId = orderService.getOrderByUserIdGoodsId(userId, goodsId);
        if (orderId != null){
            return orderId;
        }else{
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {
                return -1;
            }else {
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver, "_"+goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver, "_"+goodsId);
    }
}
