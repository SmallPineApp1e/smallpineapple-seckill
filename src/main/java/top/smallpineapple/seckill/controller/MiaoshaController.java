package top.smallpineapple.seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.smallpineapple.seckill.domain.CodeMsg;
import top.smallpineapple.seckill.domain.MiaoshaOrder;
import top.smallpineapple.seckill.domain.MiaoshaUser;
import top.smallpineapple.seckill.domain.OrderInfo;
import top.smallpineapple.seckill.service.GoodsService;
import top.smallpineapple.seckill.service.MiaoshaService;
import top.smallpineapple.seckill.service.OrderService;
import top.smallpineapple.seckill.vo.GoodsVo;

/**
 * 秒杀控制层
 *
 * @author zengzhijie
 * @since 2020/10/7 16:41
 * @version 1.0
 */
@Controller
@RequestMapping(value = "miaosha")
public class MiaoshaController {


    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MiaoshaService miaoshaService;

    @PostMapping("do_miaosha")
    public String list(Model model, MiaoshaUser miaoshaUser,
                       @RequestParam("goodsId") Long goodsId) {
        if (miaoshaUser == null) {
            return "login";
        }
        // 判断库存
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        Integer stock = goodsVo.getStockCount();
        if (stock <= 0) {
            model.addAttribute("errorMsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        // 判断之前是否秒杀过
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(miaoshaUser.getId(), goodsId);
        if (miaoshaOrder != null) {
            model.addAttribute("errorMsg", CodeMsg.REPEATE_SECKILL.getMsg());
            return "miaosha_fail";
        }
        OrderInfo orderInfo = miaoshaService.miaosha(miaoshaUser, goodsVo);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goodsVo);
        return "order_detail";
    }

}
