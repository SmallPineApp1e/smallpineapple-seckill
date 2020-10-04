package top.smallpineapple.seckill.service;

import top.smallpineapple.seckill.domain.CodeMsg;
import top.smallpineapple.seckill.domain.MiaoshaUser;
import top.smallpineapple.seckill.vo.LoginVo;

public interface MiaoshaUserService {

    MiaoshaUser getById(long id);

    CodeMsg login(LoginVo loginVo);

}
