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

    private static final Pattern MOBILE_PATTERN = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String src) {
        if (StringUtils.isEmpty(src)) return false;
        Matcher m = MOBILE_PATTERN.matcher(src);
        return m.matches();
    }

}
