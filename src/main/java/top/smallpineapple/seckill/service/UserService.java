package top.smallpineapple.seckill.service;

import top.smallpineapple.seckill.domain.User;

/**
 * 用户 Service 接口
 *
 * @author zengzhijie
 * @since 2020/9/14 13:36
 * @version 1.0.0
 */
public interface UserService {

    User getById(int id);

}
