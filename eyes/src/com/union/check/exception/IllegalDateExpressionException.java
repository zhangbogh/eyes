package com.union.check.exception;

/**
 *  Throws when date format is illegal
 *
 * @author v_chenqianming
 * @time 2015/10/28
 */
public class IllegalDateExpressionException extends IllegalArgumentException {
    public IllegalDateExpressionException(String format) {
        super("Illegal Date Format Found: " + format);
    }
}
