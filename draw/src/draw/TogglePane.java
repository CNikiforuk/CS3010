/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package draw;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Car
 */
class TogglePane extends GridPane{
    
    public static final int GRABBER = 0;
    public static final int PIXELSPRAY = 1;
    public static final int CIRCLE = 2;
    public static final int TRIANGLE = 3;
    public static final int RECTANGLE = 4;
    public static final int ROUNDEDRECTANGLE = 5;
    public static final int LINE = 6;
    public static final int SCRIBBLE = 7;
    public static final int IMAGE = 8;
    public static final int TEXT = 9;
    
    private IntegerProperty selected = new SimpleIntegerProperty(GRABBER);
    
    public float size; 
    private myButton[] toggles = new myButton[10];
    private Image[] images = new Image[10];
    private ToggleGroup group = new ToggleGroup();
    
    class myButton extends ToggleButton{
        myButton(String text){
            this.setText(text);
            this.setToggleGroup(group);
            this.setScaleShape(false);
            this.setShape(new Rectangle(size,size)); 
        }
    }
    
    TogglePane(float s){
        size = s;
        
        images[GRABBER] = new Image("file:./icons/grab.png");
        images[CIRCLE] = new Image("file:./icons/circle.png");
        images[RECTANGLE] = new Image("file:./icons/shape.png");
        images[TRIANGLE] = new Image("file:./icons/triangle.png");
        images[IMAGE] = new Image("file:./icons/image.png");
        images[TEXT] = new Image("file:./icons/text.png");
        images[ROUNDEDRECTANGLE] = new Image("file:./icons/roundrect.png");
        images[PIXELSPRAY] = new Image("file:./icons/pixelspray.png");
        images[LINE] = new Image("file:./icons/line.png");
        images[SCRIBBLE] = new Image("file:./icons/scribble.png");
        
        for(int i=0;i<toggles.length;i++){
            toggles[i] = new myButton("");
            ImageView tmp = new ImageView(images[i]);
            tmp.setFitHeight(size-5);
            tmp.setFitWidth(size-5);
            toggles[i].setGraphic(tmp);
        }
        
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        col1.setHalignment(HPos.CENTER);
        col2.setHalignment(HPos.CENTER);
        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        col1.setPrefWidth(size);
        col2.setPrefWidth(size);
        row1.setPrefHeight(size);
        row2.setPrefHeight(size);
        this.getColumnConstraints().addAll(col1,col2);
        this.getRowConstraints().addAll(row1, row2);
        this.setGridLinesVisible(true);
        this.setPadding(new Insets(0,5,0,5));
        this.setHgap(2f);
        this.setVgap(2f);
        this.setMinSize(0, 0);
        
        for(int i=0;i<toggles.length;i++){
            this.add(toggles[i],i%2,i/2);
        }
        this.selectToggle(GRABBER);
    }
    
    public void selectToggle(int t){
        this.group.selectToggle(toggles[t]);
    }
    
    public int getSelectedToggle(){
        int toggle = 0;
        try{
        for(int i=0;i<this.toggles.length;i++){
            if (this.group.getSelectedToggle().equals(this.toggles[i])){toggle = i; break;} 
        }
        return toggle;
        }catch (NullPointerException e){selectToggle(0);System.out.println("Toggle Deselect: Setting toggle to grabber.");return 0;}
    }
    
    public IntegerProperty selectedShapeProperty(){
        return this.selected;
    }
    
    public ToggleGroup getToggleGroup(){
        return this.group;
    }
    
    }
