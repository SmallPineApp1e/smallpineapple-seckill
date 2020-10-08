package top.smallpineapple.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import top.smallpineapple.seckill.domain.Goods;
import top.smallpineapple.seckill.domain.MiaoshaGoods;
import top.smallpineapple.seckill.vo.GoodsVo;

import java.util.List;

/**
 * 商品持久层
 *
 * @author zengzhijie
 * @since 2020/10/5 20:03
 * @version 1.0
 */
public interface GoodsDao {

    @Select("SELECT g.*, mg.miaosha_price, mg.stock_count, mg.start_date, mg.end_date FROM miaosha_goods mg LEFT OUTER JOIN goods g ON mg.goods_id = g.id")
    List<GoodsVo> listGoodsVo();

    @Select("SELECT g.*, mg.miaosha_price, mg.stock_count, mg.start_date, mg.end_date FROM miaosha_goods mg LEFT OUTER JOIN goods g ON mg.goods_id = g.id WHERE goods_id = #{goodsId} ")
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") Long goodsId);

    @Update("UPDATE miaosha_goods SET stock_count = stock_count - 1 WHERE id = #{goodsId}")
    int reduceStock(MiaoshaGoods good);

}
