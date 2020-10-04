package top.smallpineapple.seckill.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.Date;

/**
 * 秒杀用户类
 *
 * @author zengzhijie
 * @since 2020/10/4 15:01
 * @version 1.0
 */
@Data
public class MiaoshaUser {
    /** id */
    private Long id;
    /** 用户名字 */
    private String nickname;
    /** 用户密码 */
    private String password;
    /** 盐值 */
    private String salt;

    private String head;
    /** 注册日期 */
    private Date registerDate;
    /** 上次登录日期 */
    private Date lastLoginDate;
    /** 登录次数 */
    private Integer loginCount;

}
