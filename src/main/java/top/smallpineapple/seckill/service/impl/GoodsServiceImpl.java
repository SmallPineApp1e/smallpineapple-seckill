package top.smallpineapple.seckill.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.smallpineapple.seckill.dao.GoodsDao;
import top.smallpineapple.seckill.domain.Goods;
import top.smallpineapple.seckill.domain.MiaoshaGoods;
import top.smallpineapple.seckill.service.GoodsService;
import top.smallpineapple.seckill.vo.GoodsVo;

import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Override
    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    @Override
    public GoodsVo getGoodsVoByGoodsId(Long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    @Override
    public int reduceStock(Goods good) {
        MiaoshaGoods miaoshaGoods = new MiaoshaGoods();
        miaoshaGoods.setGoodsId(good.getId());
        int row = goodsDao.reduceStock(miaoshaGoods);
        return row;
    }

}
