package com.ant.little.common.util;

/**
 * @author: little-ant
 * 描述:
 * @date: 2022/12/27
 * @Version 1.0
 **/
public class DigitalUtil {

    public static int[] parseDigit(String data, String split) {
        if (data == null) {
            return null;
        }
        String[] part = data.split(split);
        if (part.length != 2) {
            return null;
        }
        int a = Integer.parseInt(part[0]);
        int b = Integer.parseInt(part[1]);
        return new int[]{a, b};
    }

    public static Character findFirstNotDigit(String data) {
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) < '0' || data.charAt(i) > '9') {
                return data.charAt(i);
            }
        }
        return null;
    }
}
