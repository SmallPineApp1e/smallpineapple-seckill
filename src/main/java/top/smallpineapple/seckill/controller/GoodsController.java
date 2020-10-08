package top.smallpineapple.seckill.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import top.smallpineapple.seckill.common.constant.MiaoshaStatus;
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
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsVos);
        return "goods_list";
    }

    @GetMapping("/to_detail/{goodsId}")
    public String toDetail(Model model, MiaoshaUser user,
                           @PathVariable("goodsId") Long goodsId) {

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);

        int miaoshaStatus = MiaoshaStatus.NOT_START.getStatus();
        int remainSeconds = 0;

        long startTimeMillis = goodsVo.getStartDate().getTime();
        long endTimeMillis = goodsVo.getEndDate().getTime();
        long nowTimeMillis = System.currentTimeMillis();

        if (nowTimeMillis < startTimeMillis) {
            // 还没开始秒杀
            remainSeconds = (int)((startTimeMillis - nowTimeMillis) / 1000);
        } else if (nowTimeMillis > endTimeMillis) {
            // 秒杀已结束
            miaoshaStatus = MiaoshaStatus.END.getStatus();
            remainSeconds = -1;
        } else {
            // 正在秒杀
            miaoshaStatus = MiaoshaStatus.MIAOSHA_ING.getStatus();
            remainSeconds = 0;
        }

        model.addAttribute("seckillStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("goods", goodsVo);
        model.addAttribute("user", user);
        return "goods_detail";
    }

}
