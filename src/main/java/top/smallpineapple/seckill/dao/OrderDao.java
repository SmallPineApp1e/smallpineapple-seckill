package top.smallpineapple.seckill.dao;

import org.apache.ibatis.annotations.*;
import top.smallpineapple.seckill.domain.MiaoshaOrder;
import top.smallpineapple.seckill.domain.OrderInfo;

/**
 * 订单持久层
 *
 * @author zengzhijie
 * @since 2020/10/7 16:52
 * @version 1.0
 */
public interface OrderDao {

    @Select("SELECT * FROM miaosha_order WHERE id = #{id} AND goods_id = #{goodsId}")
    MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(@Param("id") Long id, @Param("goodsId") Long goodsId);

    @Insert("INSERT INTO order_info (user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date) VALUES (" +
            "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel}, #{status}, #{createDate})")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = Long.class, before = false, statement = "SELECT last_insert_id()")
    Long insert(OrderInfo orderInfo);

    @Insert("INSERT INTO miaosha_order (user_id, order_id, goods_id) VALUES (#{userId}, #{orderId}, #{goodsId})")
    int insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);
}
