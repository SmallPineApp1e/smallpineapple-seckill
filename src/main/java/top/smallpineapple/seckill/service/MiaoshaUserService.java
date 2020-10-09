package top.smallpineapple.seckill.service;

import top.smallpineapple.seckill.domain.MiaoshaUser;
import top.smallpineapple.seckill.vo.LoginVo;

import javax.servlet.http.HttpServletResponse;

public interface MiaoshaUserService {

    MiaoshaUser getById(long id);

    String login(HttpServletResponse response, LoginVo loginVo);

    MiaoshaUser getByToken(HttpServletResponse response, String token);
}
