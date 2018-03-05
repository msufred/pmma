package com.gemseeker.pmma.utils;

/**
 *
 * @author Gem Seeker
 */
public final class Log {

    public static final void v(String tag, String message){
        String msg = String.format("V: %s: %s", tag, message);
        System.out.println(msg);
    }
    
    public static final void e(String tag, String message){
        String msg = String.format("E: %s: %s", tag, message);
        System.out.println(msg);
    }
    
    public static final void e(String tag, String message, Exception e){
        String msg = String.format("E: %s: %s", tag, message);
        System.out.println(msg);
        System.out.println("\t\t" + e.getMessage());
    }
}
