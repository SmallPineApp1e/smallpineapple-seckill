package top.smallpineapple.seckill.service;

import top.smallpineapple.seckill.domain.Goods;
import top.smallpineapple.seckill.domain.MiaoshaGoods;
import top.smallpineapple.seckill.vo.GoodsVo;

import java.util.List;

public interface GoodsService {

    List<GoodsVo> listGoodsVo();

    GoodsVo getGoodsVoByGoodsId(Long goodsId);

    int reduceStock(Goods good);
}
