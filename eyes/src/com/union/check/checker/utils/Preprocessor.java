package com.union.check.checker.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Preprocessor to extract expression in container {{}}
 *
 * @author v_chenqianming
 * @time 2015/10/26
 */

public class Preprocessor {

    /**
     * preprocess document which contains {{}}
     * @param str target document in string type
     * @return document after preprocessing
     */
    public static String process(String str) {
        Pattern pattern = Pattern.compile("\\{\\{(.+?)\\}\\}");
        while (true) {
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                StringBuffer sb = new StringBuffer();
                matcher.appendReplacement(sb, DateUtil.formatDate(matcher.group(1)));
                matcher.appendTail(sb);
                str = sb.toString();
            } else {
                break;
            }
        }
        return str;
    }
}
