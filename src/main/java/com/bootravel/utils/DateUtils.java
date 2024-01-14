package com.bootravel.utils;

import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@CommonsLog
public class DateUtils {

    public static final String NORMAL_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String PATTERN_1 = "yyyy/MM/dd HH:mm:ss";

    private DateUtils(){

    }

    public static boolean isValidDate(String format, String date){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try{
            sdf.parse(date);
            return true;
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }

        return false;
    }

    public static boolean isValidDate(String format, String date, Locale locale){
        try{
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format, locale).withResolverStyle(ResolverStyle.STRICT);

            dateTimeFormatter.parse(date);

            return true;
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }

        return false;
    }

    public static boolean isBeforeDate(String date, String beforeDate){
        try{
            LocalDateTime dateTimeNow = LocalDateTime.parse(date);
            LocalDateTime dateTimeBefore = LocalDateTime.parse(beforeDate);

            if (dateTimeNow.isBefore(dateTimeBefore)){
                return true;
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }

        return false;
    }

    public static boolean isAfterDate(String date, String beforeDate){
        try{
            LocalDateTime dateTimeNow = LocalDateTime.parse(date);
            LocalDateTime dateTimeBefore = LocalDateTime.parse(beforeDate);

            if (dateTimeNow.isAfter(dateTimeBefore)){
                return true;
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }

        return false;
    }
    public static String convertDateToString(Date date) {
        if(date == null) {
            return "";
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = dt1.format(date);
        return dateStr;
    }

    public static String convertDateToString(Date date, String pattern) {
        if(date == null) {
            return "";
        }
        SimpleDateFormat dt1 = new SimpleDateFormat(pattern);
        String dateStr = dt1.format(date);
        return dateStr;
    }

    public static Date convertStringToDate(String s) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD);
        return formatter.parse(s);
    }

    public static Date convertStringToDate(String s,String pattern) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.parse(s);
    }


    /**
     * 文字列の日付を日付型に変換
     * @param dateStr 文字列の日付
     * @return
     * @throws DateTimeParseException
     */
    public static LocalDate convertStringToLocalDate(String dateStr)
    {
        LocalDate date;
        date = null;

        try {
            if(!Objects.isNull(dateStr)){
                date = LocalDate.parse(dateStr);
            }
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
        }

        return date;
    }

    /**
     * 文字列の日付をCalendarに変換
     * @param dateStr 文字列の日付
     * @return
     */
    public static Calendar convertStringToCalendar(String dateStr){
        LocalDate localDate;
        Calendar calendar;

        calendar = null;

        localDate = convertStringToLocalDate(dateStr);
        if(localDate != null){
            calendar = Calendar.getInstance();
            calendar.clear();

            calendar.set(localDate.getYear(), localDate.getMonthValue()-1, localDate.getDayOfMonth());
        }

        return calendar;
    }

    public static String convertFromDate(Date date, String pattern ){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(date);

        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return "";
    }

    public static boolean isDateFormat(String dateString, String formatDate) {
        if (StringUtils.isEmpty(dateString)) {
            return false;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatDate);
        try {
            LocalDate date = LocalDate.parse(dateString, formatter);
            return !Objects.isNull(date) && date.format(formatter).equals(dateString);
        } catch (DateTimeException e) {
            return false;
        }
    }
}
