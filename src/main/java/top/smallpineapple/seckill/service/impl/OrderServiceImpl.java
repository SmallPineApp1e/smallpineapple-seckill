package top.smallpineapple.seckill.service.impl;

import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.smallpineapple.seckill.common.constant.OrderStatus;
import top.smallpineapple.seckill.dao.OrderDao;
import top.smallpineapple.seckill.domain.MiaoshaOrder;
import top.smallpineapple.seckill.domain.MiaoshaUser;
import top.smallpineapple.seckill.domain.OrderInfo;
import top.smallpineapple.seckill.service.OrderService;
import top.smallpineapple.seckill.vo.GoodsVo;

import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Override
    public MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(Long id, Long goodsId) {
        return orderDao.getMiaoshaOrderByUserIdAndGoodsId(id, goodsId);
    }

    @Override
    @Transactional
    public OrderInfo createOrder(MiaoshaUser miaoshaUser, GoodsVo goods) {
        // 写入 order_info
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setCreateDate(new Date());
        orderInfo.setUserId(miaoshaUser.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(OrderStatus.NOT_PAID);
        Long orderId = orderDao.insert(orderInfo);
        // 写入 miaosha_order
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderId);
        miaoshaOrder.setUserId(miaoshaUser.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        return orderInfo;
    }
}
