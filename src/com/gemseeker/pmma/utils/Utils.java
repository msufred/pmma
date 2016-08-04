package com.gemseeker.pmma.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author RAFIS-FRED
 */
public final class Utils {

    public static final String RAW_DATE_FORMAT_STR = "m/dd/yyyy";
    public static final String LOCAL_DATE_FORMAT_STR = "yyyy MMM dd";
    public static final String LOG_DATE_FORMAT_STR = "MMM-dd-yyyy";
    
    public static final DateFormat RAW_DATE_FORMAT = new SimpleDateFormat(RAW_DATE_FORMAT_STR);
    public static final DateFormat LOCAL_DATE_FORMAT = new SimpleDateFormat(LOCAL_DATE_FORMAT_STR);
    public static final DateFormat LOG_DATE_FORMAT = new SimpleDateFormat(LOG_DATE_FORMAT_STR);
    
    
}
