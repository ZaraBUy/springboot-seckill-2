package com.jesper.seckill.rabbitmq;

import com.jesper.seckill.bean.OrderInfo;
import com.jesper.seckill.bean.SeckillOrder;
import com.jesper.seckill.bean.User;
import com.jesper.seckill.exception.ReduceInventoryException;
import com.jesper.seckill.redis.RedisService;
import com.jesper.seckill.service.GoodsService;
import com.jesper.seckill.service.OrderService;
import com.jesper.seckill.service.SeckillService;
import com.jesper.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by jiangyunxiong on 2018/5/29.
 */
@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);
    private final int retry_create = 5;
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message) {
        log.info("receive message:" + message);
        SeckillMessage m = RedisService.stringToBean(message, SeckillMessage.class);
        User user = m.getUser();
        long goodsId = m.getGoodsId();

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVo.getStockCount();
        if (stock <= 0) {
            return;
        }

        //减库存 下订单 写入秒杀订单
        int i = 0;
        while (true) {
            try {
                OrderInfo seckill = seckillService.seckill(user, goodsVo);
                if (seckill != null) {
                    log.info("-------> 第{}次处理成功，message:{}", i, message);
                    return;
                }
            } catch (ReduceInventoryException e) {
                log.error("第{}次乐观锁报错重试，message:{}:", i, message);
                i++;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {

                }
            } catch (Exception e) {
                log.error("处理出错，message:{} ,e:", message, e);
                return;
            }
        }
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        log.info(" topic  queue1 message:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        log.info(" topic  queue2 message:" + message);
    }
}
