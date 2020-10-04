package top.smallpineapple.seckill.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验工具类
 *
 * @author zengzhijie
 * @since 2020/10/4 14:59
 * @version 1.0
 */
public class ValidatorUtil {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\\\d{8}$");

    public static boolean isMobile(String src) {
        if (StringUtils.isEmpty(src)) return false;
        Matcher m = MOBILE_PATTERN.matcher(src);
        return m.matches();
    }

}
