package com.szhq.iemp.reservation.util;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class PinyinUtil {

    /**
     * 获取字符串拼音的第一个字母
     */
    public static String toFirstChar(String chinese) {
        String pinyinStr = "";
        if (StringUtils.isNotEmpty(chinese) && chinese.length() >= 1) {
            pinyinStr = chinese.substring(0, 1);
        }
        return pinyinStr;
    }

    /**
     * 汉字转为拼音
     */
    public static String toPinyin(String chinese) {
        String pinyinStr = "";
        char[] newChar = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < newChar.length; i++) {
            if (newChar[i] > 128) {
                try {
                    pinyinStr += PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0];
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    log.error("e", e);
                }
            } else {
                pinyinStr += newChar[i];
            }
        }
        return pinyinStr;
    }

    public static void main(String[] args) {
        String pinyin = PinyinUtil.toPinyin("啊");
        System.out.println(pinyin);
        String first = PinyinUtil.toFirstChar(pinyin);
        System.out.println(first);
    }
}
