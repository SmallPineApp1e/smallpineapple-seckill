package top.smallpineapple.seckill.service;


import top.smallpineapple.seckill.domain.MiaoshaUser;
import top.smallpineapple.seckill.domain.OrderInfo;
import top.smallpineapple.seckill.vo.GoodsVo;

public interface MiaoshaService {


    OrderInfo miaosha(MiaoshaUser miaoshaUser, GoodsVo goodsVo);

}
