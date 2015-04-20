/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3010_lab4;

import java.util.Dictionary;
import java.util.Observable;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

/**
 *
 * @author Car
 */
public class GraphPane extends Pane {
    //y=asin(bx+c)+d
    final private DoubleProperty a = new SimpleDoubleProperty(1);
    final private DoubleProperty b = new SimpleDoubleProperty(1);
    final private DoubleProperty c = new SimpleDoubleProperty(0);
    final private DoubleProperty d = new SimpleDoubleProperty(0);
    private Polyline lineX = new Polyline();
    private Polyline lineY = new Polyline();
    private Polyline line = new Polyline();
    private Double points[];
    private double width[] = {0, Math.PI*2};
    private double height[] = {-5f, +5f};
    protected double w;
    protected double h;
    final private int res = 800;
    
    GraphPane(){
       
        lineX.getPoints().addAll(new Double[]{0.0, this.h/2.0, this.w, this.h/2.0});
        lineY.getPoints().addAll(new Double[]{this.w/2.0, 0.0, this.w/2.0, this.h});
        calcPoints(res);
        
        a.addListener(new ChangeListener(){

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                drawCurve();
            }
            
        });
        
        line.getPoints().addAll(points);
        line.setStroke(Color.INDIANRED);
        line.setStrokeWidth(2f);
        this.getChildren().addAll(lineX, lineY, line);
    }
    
    GraphPane(double a, double b, double c, double d)
    {
        this.setAll(a,b,c,d);
    }
    
    protected void setA(double a){
        this.a.set(a);
    }
    protected  void setB(double b){
        this.b.set(b);
    }
    protected  void setC(double c){
        this.c.set(c);
    }
    protected  void setD(double d){
        this.d.set(d);
    }
    
    protected  double getA(){
        return this.a.getValue();
    }
    protected  double getB(){
        return this.b.getValue();
    }
    protected  double getC(){
        return this.c.getValue();
    }
    protected  double getD(){
        return this.d.getValue();
    }
    
    protected void setAll(double a, double b, double c, double d)
    {
        this.setA(a);
        this.setB(b);
        this.setC(c);
        this.setD(d);
    }
    
    @Override
    public void resize(double width, double height){
        //this.resize(width, height);
        this.w = width;
        this.h = height;
        int iX= this.getChildren().indexOf(lineX);
        int iY = this.getChildren().indexOf(lineY);
        int curve = this.getChildren().indexOf(line);
        
        lineX.getPoints().setAll(new Double[]{0.0, height/2.0, width, height/2.0});
        lineY.getPoints().setAll(new Double[]{width/2.0, 0.0, width/2.0, height});
        
        drawCurve();
        
        this.getChildren().set(iX, lineX);
        this.getChildren().set(iY, lineY);
        this.getChildren().set(curve, line);
        

    }
    
    public void animate(){
        //a: 1 to 5
        //b: 1 to 10
        this.setA(1f);
        this.setB(1f);
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        final KeyValue kva = new KeyValue(a, 5);
        final KeyFrame kfa = new KeyFrame(Duration.millis(5000), kva);
        final KeyValue kvb = new KeyValue(b, 10);
        final KeyFrame kfb = new KeyFrame(Duration.millis(5000), kvb);
        timeline.getKeyFrames().addAll(kfa,kfb);
        timeline.play();
        timeline.setOnFinished((ActionEvent e)->{
            this.reset();
        });
        
    }
    
    public void reset(){
        this.setAll(1, 1, 0, 0);
    }
    
    void calcPoints(int res){
        // y=asin(bx+c)+d
        points = new Double[res*2];
        double x = -width[0];
        double inc = (2*width[1])/res;
        for(int i=0;i<res*2-1;i+=2){
            double tmp = (getB() * x) + getC();
            points[i] = x * (this.w/(width[1]));
            points[i+1] = (getA() * Math.sin(tmp) - getD()) * (this.h/(2*height[1]));
            points[i+1] += this.h/2f;
            x+=inc;
        }
    }
    
    void drawCurve(){
        calcPoints(res);
        line.getPoints().clear();
        line.getPoints().addAll(points);
    }
    
   
}
