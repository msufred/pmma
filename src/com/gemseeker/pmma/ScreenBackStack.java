package com.gemseeker.pmma;

/**
 *
 * @author RAFIS-FRED
 */
public class ScreenBackStack {

    private Record bottom, top;
    
    public ScreenBackStack(){
        bottom = top = null;
    }
    
    public void push(ControlledScreen screen){
        Record rec = new Record();
        rec.screen = screen;
        if(top == null){
            bottom = top = rec;
        }else{
            top.next = rec;
            rec.prev = top;
            top = rec;
        }
    }
    
    public ControlledScreen pull(){
        if(top != null){
            ControlledScreen screen = top.screen;
            if(top != bottom){
                Record temp = top.prev;
                temp.next = null;
                top = temp;
            }
            return screen;
        }
        return null;
    } 
    
    public static class Record {
        Record next, prev;
        ControlledScreen screen;
    }
}
