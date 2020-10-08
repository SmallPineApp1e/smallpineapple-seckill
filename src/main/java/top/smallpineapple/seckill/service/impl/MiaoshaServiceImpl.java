package top.smallpineapple.seckill.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.smallpineapple.seckill.domain.MiaoshaUser;
import top.smallpineapple.seckill.domain.OrderInfo;
import top.smallpineapple.seckill.service.GoodsService;
import top.smallpineapple.seckill.service.MiaoshaService;
import top.smallpineapple.seckill.service.OrderService;
import top.smallpineapple.seckill.vo.GoodsVo;

@Service
public class MiaoshaServiceImpl implements MiaoshaService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsService goodsService;

    @Override
    @Transactional
    public OrderInfo miaosha(MiaoshaUser miaoshaUser, GoodsVo goods) {
        // 1. 减库存
        int row = goodsService.reduceStock(goods);
        // 2. 写入秒杀订单 order_info 和 miaosha_order
        OrderInfo orderInfo = orderService.createOrder(miaoshaUser, goods);
        return orderInfo;
    }

}
