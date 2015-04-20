/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3010_lab4;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 *
 * @author Car
 */

//add markers on graph
//add listeners for boxes/sliders abcd values


public class Cs3010_lab4 extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        boolean verbose = false;
        
        BorderPane root = new BorderPane();
        GraphPane abc = new GraphPane();
        HBox h  = new HBox();
        HBox hbox = new HBox();
        
        double padVal = 5f;
        
        Slider sliderA = new Slider(0,5,1);
        Slider sliderB = new Slider(0,10,1);
        Slider sliderC = new Slider(-Math.PI,Math.PI,0);
        Slider sliderD = new Slider(-5,5,0);
        
        double tick = 5f;
        double block = 0.1f;
        
        sliderA.setShowTickMarks(true);
        sliderA.setShowTickLabels(true);
        sliderA.setMajorTickUnit(tick);
        sliderA.setBlockIncrement(block);
        
        sliderB.setShowTickMarks(true);
        sliderB.setShowTickLabels(true);
        sliderB.setMajorTickUnit(tick);
        sliderB.setBlockIncrement(block);
        
        sliderC.setShowTickMarks(true);
        sliderC.setShowTickLabels(true);
        sliderC.setMajorTickUnit(tick);
        sliderC.setBlockIncrement(block);
        
        sliderD.setShowTickMarks(true);
        sliderD.setShowTickLabels(true);
        sliderD.setMajorTickUnit(tick);
        sliderD.setBlockIncrement(block);
        
        double tfMaxW = 45;
        double tfMaxH = 25;
        
        Text y = new Text("  y = ");
        y.setTextAlignment(TextAlignment.JUSTIFY);
        Text equ = new Text("asin(bx+c)+d");
        
        //equ.setMaxSize(200, 25);
        //equ.setMinSize(200, 25);
        //equ.setBackground(Background.EMPTY);
        //equ.setTooltip(new Tooltip("Equation being graphed"));
        
        Button anim = new Button();
        anim.setText("Animate");
        anim.setOnAction((ActionEvent event)->{
            abc.animate();
        });

        
        hbox.getChildren().addAll(anim, y, equ);
        
        Text tA = new Text("a = ");
        TextField tfA = new TextField(String.valueOf(sliderA.getValue()));
        tfA.setTooltip(new Tooltip("value for a of y=asin(bx+c)+d"));
        tfA.setMaxSize(tfMaxW, tfMaxH);
        tfA.setMinSize(tfMaxW, tfMaxH);
        Text tB = new Text("b = ");
        TextField tfB = new TextField(String.valueOf(sliderB.getValue()));
        tfB.setTooltip(new Tooltip("value for b of y=asin(bx+c)+d"));
        tfB.setMaxSize(tfMaxW, tfMaxH);
        tfB.setMinSize(tfMaxW, tfMaxH);
        Text tC = new Text("c = ");
        TextField tfC = new TextField(String.valueOf(sliderC.getValue()));
        tfC.setTooltip(new Tooltip("value for c of y=asin(bx+c)+d"));
        tfC.setMaxSize(tfMaxW, tfMaxH);
        tfC.setMinSize(tfMaxW, tfMaxH);
        Text tD = new Text("d = ");
        TextField tfD = new TextField(String.valueOf(sliderD.getValue()));
        tfD.setTooltip(new Tooltip("value for d of y=asin(bx+c)+d"));
        tfD.setMaxSize(tfMaxW, tfMaxH);
        tfD.setMinSize(tfMaxW, tfMaxH);
        
        h.getChildren().add(tA);
        h.getChildren().add(tfA);
        h.getChildren().add(sliderA);
        h.getChildren().add(tB);
        h.getChildren().add(tfB);
        h.getChildren().add(sliderB);
        h.getChildren().add(tC);
        h.getChildren().add(tfC);
        h.getChildren().add(sliderC);
        h.getChildren().add(tD);
        h.getChildren().add(tfD);
        h.getChildren().add(sliderD);
        
        sliderA.valueProperty().addListener((javafx.beans.Observable ob) -> {
            abc.setA(sliderA.getValue());
            tfA.setText(String.valueOf(abc.getA()));
            if(verbose){System.out.println("Slider A Change  " + abc.getA());}
        });
        
        sliderB.valueProperty().addListener((javafx.beans.Observable ob) -> {
            abc.setB(sliderB.getValue());
            tfB.setText(String.valueOf(abc.getB()));
            if(verbose){System.out.println("Slider B Change  " + abc.getB());}
        });
        
        sliderC.valueProperty().addListener((javafx.beans.Observable ob) -> {
            abc.setC(sliderC.getValue());
            tfC.setText(String.valueOf(abc.getC()));
            if(verbose){System.out.println("Slider C Change  " + abc.getC());}
        });
        
        sliderD.valueProperty().addListener((javafx.beans.Observable ob) -> {
            abc.setD(sliderD.getValue());
            tfD.setText(String.valueOf(abc.getD()));
            if(verbose){System.out.println("Slider D Change  " + abc.getD());}
        });
        
        tfA.setOnAction((ActionEvent event) -> {
            if(verbose){System.out.println("Text field A");}
            try{
                sliderA.adjustValue(Double.parseDouble(tfA.getText()));
            }catch(Exception e){System.out.println(e);}
        });
        
        tfB.setOnAction((ActionEvent event) -> {
            if(verbose){System.out.println("Text field B");}
            try{
                sliderB.adjustValue(Double.parseDouble(tfB.getText()));
            }catch(Exception e){System.out.println(e);}
        });
        
        tfC.setOnAction((ActionEvent event) -> {
            if(verbose){System.out.println("Text field C");}
            try{
                sliderC.adjustValue(Double.parseDouble(tfC.getText()));
            }catch(Exception e){System.out.println(e);}
        });
        
        tfD.setOnAction((ActionEvent event) -> {
            if(verbose){System.out.println("Text field D");}
            try{
                sliderD.adjustValue(Double.parseDouble(tfD.getText()));
            }catch(Exception e){System.out.println(e);}
        });
        
        Text negPI = new Text("-π");
        Text posPI = new Text("π");
        Text neg5 = new Text("-5");
        Text pos5 = new Text("5");
        
        
        Text mouseLoc = new Text(String.valueOf(0)+", "+String.valueOf(0));
        mouseLoc.setFont(new Font("Verdana", 10));
        
        abc.getChildren().addAll(negPI, posPI, neg5, pos5, mouseLoc);
        root.setCenter(abc);
        root.setBottom(h);
        root.setTop(hbox);
        root.setPadding(new Insets(padVal,padVal,padVal,padVal));
        BorderPane.setAlignment(abc, Pos.TOP_LEFT);
        
        
        
        Scene scene = new Scene(root, 720, 480);
        
        negPI.relocate(0,scene.getHeight()/2f-60);
        posPI.relocate(scene.getWidth()-30,scene.getHeight()/2f-60);
        neg5.relocate(scene.getWidth()/2f, 0);
        pos5.relocate(scene.getWidth()/2f, scene.getHeight()-85);
        mouseLoc.relocate(scene.getWidth()-80, scene.getHeight()-85);
        
        abc.setOnMouseMoved((MouseEvent e) -> {
            double mlX, mlY;
            mlX = (e.getSceneX()-scene.getWidth()/2f)*(Math.PI*2f / abc.w);
            mlY = (e.getSceneY()-scene.getHeight()/2f+(padVal*2f))*(10f/abc.h);
            //String s = String.format("%1.2f, %1.2f", mlX, mlY);
            String locText = String.format("%1.2f, %1.2f", mlX, mlY);;
            
            mouseLoc.setText(locText);
        });
        
        root.widthProperty().addListener(new InvalidationListener(){
            @Override
            public void invalidated(javafx.beans.Observable ob) {
                if(verbose){System.out.println("Size Change");}
                root.resize(scene.getWidth(), scene.getHeight());
                negPI.relocate(0,scene.getHeight()/2f-60);
                posPI.relocate(scene.getWidth()-30,scene.getHeight()/2f-60);
                neg5.relocate(scene.getWidth()/2f, 0);
                pos5.relocate(scene.getWidth()/2f, scene.getHeight()-85);
                mouseLoc.relocate(scene.getWidth()-80, scene.getHeight()-85);
            }
        });
        
        root.heightProperty().addListener(new InvalidationListener(){
            @Override
            public void invalidated(javafx.beans.Observable ob) {
                if(verbose){System.out.println("Size Change");}
                root.resize(scene.getWidth(), scene.getHeight());
                negPI.relocate(0,scene.getHeight()/2f-60);
                posPI.relocate(scene.getWidth()-30,scene.getHeight()/2f-60);
                neg5.relocate(scene.getWidth()/2f, 0);
                pos5.relocate(scene.getWidth()/2f, scene.getHeight()-85);
                mouseLoc.relocate(scene.getWidth()-80, scene.getHeight()-85);
            }
        });
        
        primaryStage.setTitle("GraphPane");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(600);
        primaryStage.show();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
