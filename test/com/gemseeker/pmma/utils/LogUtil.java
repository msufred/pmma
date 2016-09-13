package com.gemseeker.pmma.utils;

/**
 *
 * @author Gem Seeker
 */
public final class LogUtil {

    public static final void verbose(String tag, String message){
        String msg = String.format("V: %s: %s", tag, message);
        System.out.println(msg);
    }
    
    public static final void error(String tag, String message){
        String msg = String.format("E: %s: %s", tag, message);
        System.out.println(msg);
    }
    
    public static final void error(String tag, String message, Exception e){
        String msg = String.format("E: %s: %s", tag, message);
        System.out.println(msg);
        System.out.println("\t\t" + e.getMessage());
    }
}
