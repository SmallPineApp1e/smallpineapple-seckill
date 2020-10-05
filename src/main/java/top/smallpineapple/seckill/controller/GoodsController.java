package top.smallpineapple.seckill.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import top.smallpineapple.seckill.domain.MiaoshaUser;
import top.smallpineapple.seckill.service.GoodsService;
import top.smallpineapple.seckill.vo.GoodsVo;

import java.util.List;

/**
 * 商品控制层
 *
 * @author zengzhijie
 * @since 2020/10/5 18:47
 * @version 1.0
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/to_list")
    public String toGoodList(Model model, MiaoshaUser user) {
        LOGGER.info("user:{}", user);
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsVos);
        return "goods_list";
    }

}
