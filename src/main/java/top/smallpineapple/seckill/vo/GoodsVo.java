package top.smallpineapple.seckill.vo;

import lombok.Data;
import top.smallpineapple.seckill.domain.Goods;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class GoodsVo extends Goods {

    private BigDecimal miaoshaPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

}
