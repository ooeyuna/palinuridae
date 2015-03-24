/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package moe.yuna.palinuridae.utils;

import com.google.common.base.CharMatcher;
import java.lang.reflect.Field;

/**
 *
 * @author rika
 */
public class PojoAnnotationUtil {
    public static String getFieldGetMethodString(Field field) {
        return "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
    }
    public static String getFieldSetMethodString(Field field) {
        return "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
    }
    public static String convertColumn(String field) {
        StringBuilder sb = new StringBuilder(field);
        int indexIn = CharMatcher.JAVA_UPPER_CASE.indexIn(sb);
        if (indexIn < 0) {
            return sb.toString();
        } else {
            return convertColumn(sb, indexIn).toString();
        }
    }

    public static StringBuilder convertColumn(StringBuilder sb, int indexIn) {
        String charAt = String.valueOf(sb.charAt(indexIn));
        sb.replace(indexIn, indexIn + 1, "_" + charAt.toLowerCase());
        indexIn = CharMatcher.JAVA_UPPER_CASE.indexIn(sb);
        if (indexIn < 0) {
            return sb;
        } else {
            return convertColumn(sb, indexIn);
        }
    }


}
