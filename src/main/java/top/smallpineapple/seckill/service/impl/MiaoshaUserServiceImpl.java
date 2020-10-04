package top.smallpineapple.seckill.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.smallpineapple.seckill.common.exception.GlobleException;
import top.smallpineapple.seckill.dao.MiaoshaUserDao;
import top.smallpineapple.seckill.domain.CodeMsg;
import top.smallpineapple.seckill.domain.MiaoshaUser;
import top.smallpineapple.seckill.service.MiaoshaUserService;
import top.smallpineapple.seckill.util.MD5Util;
import top.smallpineapple.seckill.vo.LoginVo;

import java.util.Objects;

/**
 * 秒杀用户业务层
 *
 * @author zengzhijie
 * @since 2020/10/4 15:11
 * @version 1.0
 */
@Service
public class MiaoshaUserServiceImpl implements MiaoshaUserService {

    @Autowired
    private MiaoshaUserDao miaoshaUserDao;

    @Override
    public MiaoshaUser getById(long id) {
        return miaoshaUserDao.getById(id);
    }

    @Override
    public boolean login(LoginVo loginVo) {
        if (Objects.isNull(loginVo)) {
            throw new GlobleException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPassword = loginVo.getPassword();
        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
        if (Objects.isNull(miaoshaUser)) {
            throw new GlobleException(CodeMsg.MOBILE_NOT_EXIST);
        }
        // 验证密码
        String dbPassword = miaoshaUser.getPassword();
        String dbSalt = miaoshaUser.getSalt();
        String calcPass = MD5Util.formPass2DbPass(formPassword, dbSalt);
        if (!dbPassword.equals(calcPass)) {
            throw new GlobleException(CodeMsg.PASSWORD_ERROR);
        }
        return true;
    }
}
