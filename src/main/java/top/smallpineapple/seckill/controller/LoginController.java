package top.smallpineapple.seckill.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.smallpineapple.seckill.domain.CodeMsg;
import top.smallpineapple.seckill.domain.MiaoshaUser;
import top.smallpineapple.seckill.domain.Result;
import top.smallpineapple.seckill.service.MiaoshaUserService;
import top.smallpineapple.seckill.util.ValidatorUtil;
import top.smallpineapple.seckill.vo.LoginVo;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 用户登录控制类
 *
 * @author zengzhijie
 * @since 2020/10/4 11:33
 * @version 1.0
 */
@Controller
@RequestMapping("/login")
public class LoginController {

     public static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

     @Autowired
     private MiaoshaUserService miaoshaUserService;

     @RequestMapping("")
     public String toLogin() {
         return "login";
     }

     @RequestMapping("/do_login")
     @ResponseBody
     public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
         LOGGER.info("loginVo:{}", loginVo);
         boolean isSuccess = miaoshaUserService.login(response, loginVo);
         return isSuccess ? Result.success(null) : Result.error(CodeMsg.SERVER_ERROR);
     }

}
