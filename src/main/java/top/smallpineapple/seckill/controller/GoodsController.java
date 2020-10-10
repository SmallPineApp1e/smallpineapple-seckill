package top.smallpineapple.seckill.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import top.smallpineapple.seckill.common.constant.MiaoshaStatus;
import top.smallpineapple.seckill.common.redisKey.GoodsKey;
import top.smallpineapple.seckill.domain.MiaoshaUser;
import top.smallpineapple.seckill.service.GoodsService;
import top.smallpineapple.seckill.util.RedisUtil;
import top.smallpineapple.seckill.vo.GoodsVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @GetMapping("/to_list")
    @ResponseBody
    public String toGoodList(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user) {
        // 先去缓存中查找已经之前渲染好的 HTML 页面
        String html = (String) redisUtil.get(GoodsKey.getGoodsList.getPrefix());
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        // 手动渲染
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsVos);
        model.addAttribute("user", user);
        WebContext context = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", context);
        if (!StringUtils.isEmpty(html)) {
            // 将初次渲染好的 HTML 页面保存到 Redis 中
            redisUtil.set(GoodsKey.getGoodsList.getPrefix(), html, GoodsKey.getGoodsList.expireSeconds());
        }
        return html;
    }

    @GetMapping("/to_detail/{goodsId}")
    @ResponseBody
    public String toDetail(Model model,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           MiaoshaUser user,
                           @PathVariable("goodsId") Long goodsId) {

        // 先去缓存中查找已经之前渲染好的 HTML 页面
        String html = (String) redisUtil.get(GoodsKey.getGoodsDetail.getPrefix() + goodsId);

        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        // 手动渲染
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        // 获取秒杀状态
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
        WebContext context = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", context);
        if (!StringUtils.isEmpty(html)) {
            // 将初次渲染好的 HTML 页面保存到 Redis 中
            redisUtil.set(GoodsKey.getGoodsDetail.getPrefix() + goodsId, html, GoodsKey.getGoodsDetail.expireSeconds());
        }
        return html;
    }

}
