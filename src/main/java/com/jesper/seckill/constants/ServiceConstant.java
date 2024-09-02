package com.jesper.seckill.constants;

public class ServiceConstant {

    /**
     * 秒杀LUA脚本，如果没有秒杀记录，才能创建，设置为-1待减库存，为0表示成功，减库存也成功
     * 返回-1表示失败，重复秒杀
     * 返回-2表示失败，无库存
     * 返回0表示成功
     */
    public final static String SECKILL_ORDER_LUA =
                    "if redis.call('EXISTS', KEYS[1]) == 0 then\n" +
                    "   redis.call('SET', KEYS[1], '-1')\n" +
                    "   if redis.call('DECR', KEYS[2]) >= 0 then\n" +
                    "       redis.call('SET', KEYS[1], '0')\n" +
                    "       return 0\n" +
                    "   else\n" +
                    "       return -2\n" +
                    "   end\n" +
                    "else\n" +
                    "   return -1\n" +
                    "end";
}