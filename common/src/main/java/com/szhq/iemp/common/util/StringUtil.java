package com.szhq.iemp.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class StringUtil extends StringUtils {


    private static final FieldPosition HELPER_POSITION = new FieldPosition(0);
    private final static Format dateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
    private final static NumberFormat numberFormat = new DecimalFormat("000000");
    private static int seq = 0;
    private static final int MAX = 999999;

    /**
     * 根据当前的时间生成编码Id
     *
     * @return String
     */
    public static synchronized String createUUID() {
        Calendar calendar = Calendar.getInstance();
        StringBuffer sb = new StringBuffer();
        sb.append("iemp");
        dateFormat.format(calendar.getTime(), sb, HELPER_POSITION);
        numberFormat.format(seq, sb, HELPER_POSITION);
        if (seq == MAX) {
            seq = 0;
        } else {
            seq++;
        }
        return sb.toString();
    }

    /**
     * 将Double转为String
     */
    public static String doubleToString(double d, String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(d);
    }


}
