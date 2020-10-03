package top.smallpineapple.seckill.common.redisKey;

/**
 * 用户模块 Redis key
 *
 * @author zengzhijie
 * @since 2020/10/3 11:16
 * @version 1.0
 */
public class UserKey extends BasePrefix {

    /**
     * 构造函数私有化防止实例化
     * @param expireSeconds 过期时间
     * @param prefix 前缀
     */
    private UserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public UserKey(String prefix) {
        super(prefix);
    }

    /** ==============================获取用户的 Key============================== */
    /** 根据用户的 id 获取 value */
    public static final UserKey USER_ID_KEY = new UserKey("id");
    /** 根据用户的 name 获取 value */
    public static final UserKey USER_NAME_KEY = new UserKey("name");
    /** ==============================设置用户的 Key============================== */
    public static final UserKey USER_ID_KEY_EX_TEN = new UserKey(10, "id");
    

}
