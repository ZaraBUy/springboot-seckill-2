package com.jesper.seckill;

import com.jesper.seckill.util.UUIDUtil;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            String uuid = UUIDUtil.uuid();
            System.out.println(String.format("INSERT INTO `sk_user` VALUES ('%s', '%s', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c4d', null, '2018-05-21 21:10:21', '2018-05-21 21:10:25', '1');", i, uuid.substring(1, 10)));
        }
    }
}
