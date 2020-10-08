package top.smallpineapple.seckill.service;


import top.smallpineapple.seckill.domain.MiaoshaOrder;
import top.smallpineapple.seckill.domain.MiaoshaUser;
import top.smallpineapple.seckill.domain.OrderInfo;
import top.smallpineapple.seckill.vo.GoodsVo;

public interface OrderService {


    MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(Long id, Long goodsId);

    OrderInfo createOrder(MiaoshaUser miaoshaUser, GoodsVo goods);
}
