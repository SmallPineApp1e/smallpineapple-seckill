package top.smallpineapple.seckill.common.redisKey;

/**
 * Key Prefix 的通用行为抽象类
 *
 * @author zengzhijie
 * @since 2020/10/3 11:14
 * @version 1.0
 */
public abstract class BasePrefix implements KeyPrefix {

    /** 过期时间 0表示永不过期 单位:秒 */
    private int expireSeconds;
    /** 前缀 */
    private String prefix;

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public BasePrefix(String prefix) {
        this(0, prefix);
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        // 通过调用者的类名就可以区分出不同的模块, 保证了前缀不相同, Key 也不相同
        String clazzName = getClass().getSimpleName();
        return clazzName + ":" + prefix;
    }
}
