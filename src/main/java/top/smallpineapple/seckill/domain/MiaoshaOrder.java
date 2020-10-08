package top.smallpineapple.seckill.domain;

import lombok.Data;

/**
 * 秒杀订单
 *
 * @author zengzhijie
 * @since 2020/10/5 20:02
 * @version 1.0
 */
@Data
public class MiaoshaOrder {

    private Long id;
    private Long userId;
    private Long orderId;
    private Long goodsId;

}
