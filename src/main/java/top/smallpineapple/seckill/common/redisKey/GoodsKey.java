package top.smallpineapple.seckill.common.redisKey;

public class GoodsKey extends BasePrefix {

    public GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60, "goodsList");

    public static GoodsKey getGoodsDetail = new GoodsKey(60, "goodsDetail");

}
