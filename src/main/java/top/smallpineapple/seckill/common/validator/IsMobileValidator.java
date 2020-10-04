package top.smallpineapple.seckill.common.validator;

import org.springframework.util.StringUtils;
import top.smallpineapple.seckill.util.ValidatorUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 手机号码校验器
 * 
 * @author zengzhijie
 * @since 2020/10/4 15:31
 * @version 1.0
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {
    /** 接收注解中的 required 值 */
    private Boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (required) {
            return ValidatorUtil.isMobile(s);
        } else if (StringUtils.isEmpty(s)) {
            return false;
        } else {
            return ValidatorUtil.isMobile(s);
        }
    }

}
