package com.union.check.checker.utils;

import com.union.check.exception.IllegalDateExpressionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date Calculating Util, calc date expression and format to specific pattern
 *
 * @author v_chenqianming
 * @time 2015/10/26
 */
public final class DateUtil {
    private static Logger log = LoggerFactory.getLogger(CronUtil.class);

    private static String[] UNIT_SUPPORT_PATTERNS = {
            "second(s)?", "minute(s)?", "hour(s)?",
            "day(s)?", "week(s)?", "month(s)?", "year(s)?",
    };
    private static int[] UNIT_SUPPORT_FIELDS = {
            Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR,
            Calendar.DATE, Calendar.WEDNESDAY, Calendar.MONTH, Calendar.YEAR,
    };
    private static Pattern VALID_PATTERN;
    private static Pattern TOKEN_PATTERN;

    /**
     * join string array with connector
     * @param strAry string array
     * @param connector connector between array elements
     * @return string after array joined by connector
     */
    public static String join(String[] strAry, String connector) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strAry.length; i++) {
            if (i == (strAry.length - 1)) {
                sb.append(strAry[i]);
            } else {
                sb.append(strAry[i]).append(connector);
            }
        }
        return new String(sb);
    }

    static {
        String unitsPattern = join(UNIT_SUPPORT_PATTERNS, "|");
        String tokenPattern = "([\\+|\\-])?[0-9]+\\s+(" + unitsPattern + ")\\s*?";
        TOKEN_PATTERN = Pattern.compile(tokenPattern, Pattern.CASE_INSENSITIVE);
        String pattern = "\\s*'\\s*(" + tokenPattern + ")+'\\s*";
        VALID_PATTERN = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    /**
     * Get date calculation unit
     * @param strUnit unit in string type
     * @return calendar enumeration
     */
    private static int getCalcUnit(String strUnit) {
        for (int i = 0; i < UNIT_SUPPORT_PATTERNS.length; i++) {
            if (UNIT_SUPPORT_PATTERNS[i].startsWith(strUnit)) {
                return UNIT_SUPPORT_FIELDS[i];
            }
        }
        return 0;
    }

    /**
     * Calculate and format date expression within a string sequence
     * @param dateExpression  date expression within a string sequence
     * @return date after calculation
     */
    public static String formatDate(String dateExpression) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter;
        String[] tokens = dateExpression.split("--(?i)date");
        if (tokens.length > 2 || tokens.length < 1) {
            log.error("Illegal Date Expression: " + dateExpression);
            throw new IllegalDateExpressionException(dateExpression);
        }
        formatter = new SimpleDateFormat(tokens[0].trim());
        if (tokens.length == 2) {
            Matcher matcher = VALID_PATTERN.matcher(tokens[1]);
            if (matcher.matches()) {
                Matcher tokenMatcher = TOKEN_PATTERN.matcher(tokens[1]);
                while (tokenMatcher.find()) {
                    String token = tokenMatcher.group();
                    String[] items = token.split("\\s");
                    int diff = Integer.parseInt(items[0].replace("+", ""));
                    int unit = getCalcUnit(items[1].trim());
                    calendar.add(unit, diff);
                }
            } else {
                log.error("Illegal Date Expression: " + dateExpression);
                throw new IllegalDateExpressionException(dateExpression);
            }
        }
        return formatter.format(calendar.getTime());
    }
}
