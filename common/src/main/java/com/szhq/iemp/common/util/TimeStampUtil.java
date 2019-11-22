package com.szhq.iemp.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class TimeStampUtil {

    public final static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * 获取下个月开始时间
     */
    public static Date getNextMonthStartTime(){
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date endDate = getBeforeDawnDate(cal.getTime());
        return endDate;
    }

    /**
     * 获取几年后凌晨时间
     */
    public static Date getYearTimeByPeriod(Date date, Integer period){
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 12 * period);
        System.out.println(cal.getTime());
        cal.add(Calendar.SECOND, -1);
        Date endDate = getBeforeDawnDate(cal.getTime());
        return endDate;
    }

    private static Date getBeforeDawnDate(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return new Date(java.sql.Date.valueOf(localDate).getTime());
    }

    /**
     *时间转换（线程安全）
     */
    public static Date parseDate(String dateStr, String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = DATE_PATTERN;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate date = LocalDate.parse(dateStr, formatter);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = date.atStartOfDay(zoneId);
        return Date.from(zdt.toInstant());
    }

    /**
     *时间转换（线程安全）
     */
    public static String formatDate(Date date, String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = DATE_PATTERN;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime now = date.toInstant().atZone(zoneId).toLocalDateTime();
        return now.format(formatter);

    }


    @SuppressWarnings("unused")
    public static long getDayStartTs() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getDayStartTs(long ts) {
        Date date = new Date(ts);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getDayEndTs(long ts) {
        Date date = new Date(ts);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTimeInMillis();
    }

    @SuppressWarnings("unused")
    public static long getDayAmEndTs(long ts) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(ts);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTimeInMillis();
    }

    @SuppressWarnings("unused")
    public static long getDayPmStartTs(long ts) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(ts);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static int getApm(long ts) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ts);
        return calendar.get(Calendar.AM_PM);
    }

    /**
     * 获取TS字段值
     */
    public static String getTs() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static String getTs(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static Date getTs(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(date);
        } catch (ParseException e) {
            log.error("e", e);
        }
        return new Date();
    }


    public static void main(String[] args) {
        /*System.out.println(getDayStartTs(1535955847549L));
        System.out.println(getDayEndTs(1535955847549L));
        System.out.println(getDayAmEndTs(1535955847549L));
        System.out.println(getDayPmStartTs(1535955847549L));
        System.out.println(getApm(1535955847549L));*/
        /*byte _alert = (byte) 3;
        boolean isBackout = ((_alert & (byte) 4) == 4);
        System.out.println(isBackout);*/

        /*String reservationNumber = new SimpleDateFormat("yyyyMMdd").format(new Date(1535955847549L)) + String.format("%05d", new Random().nextInt(10000) + 1);
        System.out.println(reservationNumber);*/

//        System.out.println(TimeStampUtil.getDayStartTs());
//        System.out.println(formatDate(new Date(), null));
//        Date date = getNextMonthStartTime();
//       date =  getYearTimeByPeriod(date, 1);
        Date date = parseDate("2019-10-18", "yyyy-MM-dd");
        System.out.println(date);
    }


}
