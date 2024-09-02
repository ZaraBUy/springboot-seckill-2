package com.jesper.seckill.service;

import com.jesper.seckill.bean.SeckillGoods;
import com.jesper.seckill.exception.GlobalException;
import com.jesper.seckill.mapper.GoodsMapper;
import com.jesper.seckill.rabbitmq.MQReceiver;
import com.jesper.seckill.result.CodeMsg;
import com.jesper.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jiangyunxiong on 2018/5/22.
 */
@Service
public class GoodsService {

    private static Logger log = LoggerFactory.getLogger(GoodsService.class);

    //乐观锁冲突最大重试次数
    private static final int DEFAULT_MAX_RETRIES = 5;

    @Autowired
    GoodsMapper goodsMapper;

    /**
     * 查询商品列表
     *
     * @return
     */
    public List<GoodsVo> listGoodsVo() {
        return goodsMapper.listGoodsVo();
    }

    /**
     * 根据id查询指定商品
     *
     * @return
     */
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }

    /**
     * 尝试1次，乐观锁更新
     *
     * @return
     */
    public boolean reduceStock(GoodsVo goods) {
        int ret = 0;
        SeckillGoods sg = new SeckillGoods();
        sg.setGoodsId(goods.getId());
        try {
            sg.setVersion(goodsMapper.getVersionByGoodsId(goods.getId()));
            ret = goodsMapper.reduceStockByVersion(sg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret > 0;
    }
}
