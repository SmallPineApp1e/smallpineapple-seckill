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

    @GetMapping("/to_list")
    public String toGoodList(Model model, MiaoshaUser user) {
        LOGGER.info("user:{}", user);
        model.addAttribute("user", user);
        return "goods_list";
    }

}
