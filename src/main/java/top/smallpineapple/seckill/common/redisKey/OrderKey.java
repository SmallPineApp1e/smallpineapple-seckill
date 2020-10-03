package top.smallpineapple.seckill.common.redisKey;

/**
 * 订单模块 Redis Key
 *
 * @author zengzhijie
 * @since 2020/10/3 11:16
 * @version 1.0
 */
public class OrderKey extends BasePrefix {

    public OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);

    }
}
