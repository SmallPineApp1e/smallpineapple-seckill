package top.smallpineapple.seckill.common.constant;

/**
 * 订单状态
 *
 * @author zengzhijie
 * @since 2020/10/7 17:07
 * @version 1.0
 */
public class OrderStatus {
    /** 未支付 */
    public static final int NOT_PAID = 0;
    /** 已支付 */
    public static final int ALREADY_PAID = 1;
    /** 已发货 */
    public static final int ALREADY_DELIVERY = 2;
    /** 已收货 */
    public static final int ALREADY_GET = 3;
    /** 已退货 */
    public static final int ALREADY_RETURN = 4;
    /** 已完成 */
    public static final int FINISHED = 5;

}
