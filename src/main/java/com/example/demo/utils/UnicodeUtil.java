package com.example.demo.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class UnicodeUtil {

    // 16进制数组
    private static final char[] HEX_CHAR_ARRAY = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'a', 'b', 'c', 'd', 'e', 'f'};

    // unicode开始标记
    private static final String UNICODE_START = "\\u";

    // 十六进制常量
    private static final int HEX_NUMBER = 16;

    //解码
    public static String decode(String unicodeString) {
        if (StringUtils.isBlank(unicodeString) || !unicodeString.contains(UNICODE_START)) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int index, pos = 0;
        while (true) {
            index = unicodeString.indexOf(UNICODE_START, pos);
            if (index == -1) {
                break;
            }
            for (int i = 3; i < 7; i++) {
                if (i == 6 || index + i >= unicodeString.length()) {
                    stringBuilder.append((char) Integer.parseInt(unicodeString.substring(index + 2, index + i), HEX_NUMBER));
                    pos = index + i;
                    break;
                }
                char nextChar = unicodeString.charAt(index + i);
                if (!ArrayUtils.contains(HEX_CHAR_ARRAY, nextChar)) {
                    stringBuilder.append((char) Integer.parseInt(unicodeString.substring(index + 2, index + i), HEX_NUMBER));
                    pos = index + i;
                    break;
                }
            }
        }
        return stringBuilder.toString();
    }

    public static String encode(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        StringBuilder unicodeBuilder = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char nextChar = string.charAt(i);
            unicodeBuilder.append("\\u");
            unicodeBuilder.append(Integer.toHexString(nextChar));
        }
        return unicodeBuilder.toString();
    }
}

