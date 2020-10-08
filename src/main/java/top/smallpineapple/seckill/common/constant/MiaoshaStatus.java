package top.smallpineapple.seckill.common.constant;

/**
 * 秒杀状态枚举
 *
 * @author zengzhijie
 * @since 2020/10/7 16:10
 * @version 1.0
 */
public enum MiaoshaStatus {

    /** 还没开始秒杀 */
    NOT_START(0),
    /** 秒杀已结束 */
    END(1),
    /** 正在秒杀 */
    MIAOSHA_ING(2);


    private Integer status;

    MiaoshaStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
