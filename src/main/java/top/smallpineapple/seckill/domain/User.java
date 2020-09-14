package top.smallpineapple.seckill.domain;

import lombok.Data;
import lombok.ToString;

/**
 * 用户
 * 
 * @author zengzhijie
 * @since 2020/9/14 13:33
 * @version 1.0.0
 */
@Data
@ToString
public class User {

    /** 用户主键 */
    private Integer id;
    /** 用户名字 */
    private String name;

}
