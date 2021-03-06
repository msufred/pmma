package com.gemseeker.pmma.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/**
 *
 * @author RAFIS-FRED
 */
public final class Utils {

    public static final String RAW_DATE_FORMAT_STR = "m/dd/yyyy";
    public static final String LOCAL_DATE_FORMAT_STR = "MMM dd, yyyy";
    public static final String LOG_DATE_FORMAT_STR = "MMM-dd-yyyy";
    
    /**
     * RAW_DATE_FORMAT is a DateFormat used to parse and format the raw date
     * generated by the DatePicker. The DatePicker has a TextField that contains
     * the String value of the picked date. The value can be retrieve by calling
     * DatePicker.getEditor().getText().
     */
    public static final DateFormat RAW_DATE_FORMAT = new SimpleDateFormat(RAW_DATE_FORMAT_STR);
    
    /**
     * LOCAL_DATE_FORMAT is a DateFormat used to display dates on tables. Example:
     * Jul 04, 2016.
     */
    public static final DateFormat LOCAL_DATE_FORMAT = new SimpleDateFormat(LOCAL_DATE_FORMAT_STR);
    
    /**
     * LOG_DATE_FORMAT is a DateFormat used for log date.
     */
    public static final DateFormat LOG_DATE_FORMAT = new SimpleDateFormat(LOG_DATE_FORMAT_STR);
    
    public static final String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    
    public static final String PHONE_REGEX_1 = "(\\d-)?(\\d{3}-)?\\d{3}-\\d{4}";
    
    public static final String PHONE_REGEX_2 = "\\d{10}";
    
    public static final String PHONE_REGEX_3 = "\\d{11}";
    
    public static final String PHONE_REGEX_4 = "\\d{3}-\\d{3}-\\d{4}";

}
