package top.smallpineapple.seckill.common.exception;

import lombok.Getter;
import top.smallpineapple.seckill.domain.CodeMsg;

/**
 * 业务产生的自定义异常
 *
 * @author zengzhijie
 * @since 2020/10/4 16:04
 * @version 1.0
 */
@Getter
public class GlobleException extends RuntimeException {

    private CodeMsg cm;

    public GlobleException(CodeMsg cm) {

        super(cm.toString());
        this.cm = cm;

    }
}
