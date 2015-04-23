/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package draw;

/**
 *
 * @author hrist
 */
public class Event{
    
    public static final int REMOVE = -1;
    public static final int MODIFY = 0;
    public static final int ADD = 1;
    
    private final int code;
    private Object object;

    public Event(int c, Object o){
        this.code = c;
        this.object = o;
    }

    protected int getCode(){
        return this.code;
    }
    protected Object getObject(){
        return this.object;
    }
        
}
