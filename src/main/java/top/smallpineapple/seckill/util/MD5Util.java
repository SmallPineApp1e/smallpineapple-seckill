package top.smallpineapple.seckill.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5 加密工具类
 *
 * @author zengzhijie
 * @since 2020/10/4 11:12
 * @version 1.0
 */
public class MD5Util {

    /** 固定 salt 值, 与前端沟通 */
    public static final String SALT = "1a2b3c";

    /**
     * 用户
     * @param src
     * @return
     */
    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    /**
     * 将用户端传递过来的密码加第一次盐
     * @param formPass 表单提交的密码 MD5(明文 + 盐)
     * @return 第一次加盐且md5散列的密码串
     */
    public static String inputPassFormPass(String formPass) {
        return md5("" + SALT.charAt(0) + SALT.charAt(2) + formPass + SALT.charAt(5) + SALT.charAt(4));
    }

    /**
     * 将第一次加盐MD5散列后的密码传入, 返回第二次加盐散列的密码串
     * @param formPass 第一次加盐MD5散列的密码串
     * @param salt 随机 salt, 不是固定 salt
     * @return 第二次加盐散列后的密码串
     */
    public static String formPass2DbPass(String formPass, String salt) {
        return md5("" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4));
    }

    /**
     * 将用户输入的密码直接转化成数据库需要存储的密码串
     * @param src 原始密码串
     * @param saltDb 随机盐值
     * @return 数据库需要存储的密码串
     */
    public static String inputPass2DbPass(String src, String saltDb) {
        return formPass2DbPass(inputPassFormPass(src), saltDb);
    }

}
