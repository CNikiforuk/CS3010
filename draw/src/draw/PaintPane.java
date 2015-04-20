/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package draw;

import java.util.ArrayList;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineJoin;

/**
 *
 * @author Car
 */

public class PaintPane extends Pane {
    
    private ArrayList shapes;
    private MyShape selected;
    
    PaintPane(){
        this.setStyle("-fx-border-color: black; -fx-border-width: 1;");
    }
    
    void selectShape(MyShape shape){
        this.selected = shape; 
    }
    
}
