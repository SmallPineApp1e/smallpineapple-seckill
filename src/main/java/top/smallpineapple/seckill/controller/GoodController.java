package top.smallpineapple.seckill.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.smallpineapple.seckill.common.redisKey.MiaoshaUserKey;
import top.smallpineapple.seckill.domain.MiaoshaUser;
import top.smallpineapple.seckill.service.MiaoshaUserService;
import top.smallpineapple.seckill.service.impl.MiaoshaUserServiceImpl;

import javax.servlet.http.HttpServletResponse;

/**
 * 商品控制层
 *
 * @author zengzhijie
 * @since 2020/10/5 18:47
 * @version 1.0
 */
@Controller
@RequestMapping("/goods")
public class GoodController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodController.class);

    @Autowired
    private MiaoshaUserService miaoshaUserService;

    /**
     *
     * @param model
     * @param cookieToken cookie 中的 token, 如果用户禁用cookie则采用url重写
     * @param paramToken 如果是移动端, 有可能不是存 cookie, 而是从路径中传token过来
     * @return
     */
    @GetMapping("/to_list")
    public String toGoodList(HttpServletResponse response,
                             Model model,
                             @CookieValue(value = MiaoshaUserServiceImpl.COOKIE_NAME_TOKEN, required = false) String cookieToken,
                             @RequestParam(value = MiaoshaUserServiceImpl.COOKIE_NAME_TOKEN, required = false) String paramToken) {
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return "login";
        }
        String token = StringUtils.isEmpty(cookieToken) ? paramToken : cookieToken ;
        MiaoshaUser user = miaoshaUserService.getByToken(response, token);
        LOGGER.info("user:{}", user);
        model.addAttribute("user", user);
        return "goods_list";
    }

}
