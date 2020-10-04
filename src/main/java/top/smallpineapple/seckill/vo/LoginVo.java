package top.smallpineapple.seckill.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.smallpineapple.seckill.common.validator.IsMobile;

import javax.validation.constraints.NotNull;

/**
 * 接收表单参数vo
 *
 * @author zengzhijie
 * @since 2020/10/4 11:37
 * @version 1.0
 */
@Getter
@Setter
@ToString
public class LoginVo {

    @NotNull(message = "手机号码不能为空")
    @IsMobile
    private String mobile;
    @NotNull(message = "密码不能为空")
    private String password;

}
