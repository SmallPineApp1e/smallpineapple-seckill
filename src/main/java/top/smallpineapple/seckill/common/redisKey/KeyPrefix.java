package top.smallpineapple.seckill.common.redisKey;

/**
 * Redis Key 前缀接口
 *
 * @author zengzhijie
 * @since 2020/10/3 11:12
 * @version
 */
public interface KeyPrefix {

    /**
     * Key 的过期时间
     * @return 过期时间
     */
    int expireSeconds();

    /**
     * 获取前缀
     * @return 前缀
     */
    String getPrefix();

}
