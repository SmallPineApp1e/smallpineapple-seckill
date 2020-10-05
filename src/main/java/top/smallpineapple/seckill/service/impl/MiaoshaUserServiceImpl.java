package top.smallpineapple.seckill.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.smallpineapple.seckill.common.exception.GlobleException;
import top.smallpineapple.seckill.common.redisKey.MiaoshaUserKey;
import top.smallpineapple.seckill.dao.MiaoshaUserDao;
import top.smallpineapple.seckill.domain.CodeMsg;
import top.smallpineapple.seckill.domain.MiaoshaUser;
import top.smallpineapple.seckill.service.MiaoshaUserService;
import top.smallpineapple.seckill.util.MD5Util;
import top.smallpineapple.seckill.util.RedisUtil;
import top.smallpineapple.seckill.util.UUIDUtil;
import top.smallpineapple.seckill.vo.LoginVo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
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

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    private MiaoshaUserDao miaoshaUserDao;
    @Autowired
    private RedisUtil redisUtil;


    @Override
    public MiaoshaUser getById(long id) {
        return miaoshaUserDao.getById(id);
    }

    @Override
    public boolean login(HttpServletResponse response, LoginVo loginVo) {
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

        addCookie(response, miaoshaUser);

        return true;
    }

    @Override
    public MiaoshaUser getByToken(HttpServletResponse response, String token) {

        if (StringUtils.isEmpty(token)) {
            return null;
        }
        // 延长有效期
        MiaoshaUser miaoshaUser = (MiaoshaUser) redisUtil.get(MiaoshaUserKey.token.getPrefix() + token);
        if (miaoshaUser != null) {
            addCookie(response, miaoshaUser);
        }
        return miaoshaUser;

    }

    /**
     * 将 token 写回到客户端
     * @param response
     * @param miaoshaUser
     */
    private void addCookie(HttpServletResponse response, MiaoshaUser miaoshaUser) {
        // 生成 Cookie
        String token = UUIDUtil.createUUID();
        // 将该 token 写入到 Redis 当中, 标识当前用户
        redisUtil.set(MiaoshaUserKey.token.getPrefix() + token,
                miaoshaUser, MiaoshaUserKey.token.expireSeconds());
        // 将 token 写回到客户端中
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}
