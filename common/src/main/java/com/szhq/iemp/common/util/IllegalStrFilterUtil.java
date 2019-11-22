package com.szhq.iemp.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wanghao
 * @date 2019/11/14
 */
public class IllegalStrFilterUtil {

    private static final String REGX = "[ _`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t";

    /**
     * 对非法字符进行检测
     *  true 表示参数不包含非法字符
     *  false 表示参数包含非法字符
     */
    public static Boolean isIllegalStr(String sInput) {
        if (sInput == null || sInput.trim().length() == 0) {
            return false;
        }
        sInput = sInput.trim();
        Pattern compile = Pattern.compile(REGX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = compile.matcher(sInput);
        return matcher.find();
    }

    public static void main(String[] args) {
        Boolean b = isIllegalStr("123*****21212");
        System.out.println(b);
    }

}
