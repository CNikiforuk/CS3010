/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package draw;

import java.util.ArrayList;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author hrist
 */
public class MyEvents{
    
    public ArrayList<Event> events = new ArrayList();
    private SimpleIntegerProperty index = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty end = new SimpleIntegerProperty(0);
    private SimpleBooleanProperty empty = new SimpleBooleanProperty(true);
    private SimpleBooleanProperty atEnd = new SimpleBooleanProperty(true);
    
    public MyEvents() {
        index.addListener(e ->{
            empty.set((index.get()==0));
            atEnd.set((end.get()-index.get())<0);
        });
        
    }
    
    public void add(Event e){
        events.add(index.get(), e);
        end.set(index.get());
        System.out.println(e.getCode()+" Event added at Index:"+index.get()+" End Location:"+end.get());
        index.set(index.get()+1);
    }
    
    public void decrement(){
        index.set(Math.max(0,index.get()-1));
    }
    
    public void increment(){
        index.set(Math.min(end.get()+1, index.get()+1));
    }
    
    public Event getEvent(){
        return events.get(Math.max(0, index.get()-1));
    }
    
    public int getEnd(){
        return end.get();
    }
    
    public int getIndex(){
        return index.get();
    }
    
    public SimpleIntegerProperty endProperty(){
        return end;
    }
    
    public SimpleIntegerProperty indexProperty(){
        return index;
    }
    
    public void dumpEvents(){
        for (int i=0;i<events.size();i++){
            System.out.println(events.get(i).getCode()+" | "+events.get(i).getObject());
        }
    }
    
    public SimpleBooleanProperty emptyProperty(){
        return empty;
    }
    
    public SimpleBooleanProperty atEndProperty(){
        return atEnd;
    }
    
    public void clear(){
        index.set(0);
        end.set(0);
        atEnd.set(true);
        empty.set(true);
    }
}
