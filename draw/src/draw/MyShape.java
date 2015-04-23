/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package draw;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

/**
 *
 * @author Car
 */


//based off of David's code
interface Drawable {
    void setFillColor(Color c);
    void setStrokeColor(Color c); 
    void setStrokeWidth(double width);
    void setStrokeDashArray(double [] value);    
    void setFont(Font value);
    void setText(String value);
    Paint getFillColor();
    Paint getStrokeColor(); 
    double getStrokeWidth();
    double [] getStrokeDashArray();    
    Font getFont();
    String getText();
}


class MyShape extends StackPane implements Drawable{
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
    public static final double INSETSIZE = 4;
    public static final int MINSIZE = 10;
    
    private final int shapeType;
    private final SimpleBooleanProperty selected;
    private final Node shape; //The content of the shape
    private static Paint defaultFillPaint=Color.RED;
    private static Paint defaultStrokePaint=Color.BLACK;
    private static Paint defaultSelectedPaint= new Color(0,0,0,0.5); //transparent gray
    private static double defaultStrokeWidth=3;
    private static final IntegerProperty defaultShapeType = new SimpleIntegerProperty(CIRCLE);
    private static double defaultWidth = 2;
    private static double defaultHeight = 2;
    private static String defaultFontName = "Times Roman";
    private static double defaultFontSize = 15;
    private final float arc = 15f;        //default arc width/height

    
    private Node makeShape(){
        Node s = null;
        switch(defaultShapeType.get()){
            case GRABBER: break;
            case CIRCLE: s = new Circle(Math.min(defaultWidth/2, defaultHeight/2));break;
            case RECTANGLE: s = new Rectangle(defaultWidth,defaultHeight);break;
            case TRIANGLE: s = new Polygon(0,0,defaultWidth,0,0,defaultHeight); break;
            case IMAGE: s = new ImageView(new Image("file:testpic.jpg")); break;
            case TEXT: s = new Text("Text"); break;
            case ROUNDEDRECTANGLE: s = new Rectangle(defaultWidth/2, defaultHeight/2); 
                                   ((Rectangle)s).setArcHeight(arc); 
                                   ((Rectangle)s).setArcWidth(arc) ; break;
            case PIXELSPRAY: s = new ImageView(new Image("file:./icons/pixelspray2.png")); s.setRotate(Math.random()*360); break;
            case LINE: s = new Polyline(0,0,0,0); break;
            case SCRIBBLE: s = new Polyline(); ((Polyline)s).setFill(null); break;
        }
        
        return s;
    }

    public MyShape(){
        super();
        selected = new SimpleBooleanProperty(false);
        selected.set(false);
        shape = makeShape();
        shapeType = defaultShapeType.get();
        
        if(shape instanceof Text){//test this first because Text isa Shape
            ((Text)shape).setStroke(defaultStrokePaint);
            ((Text)shape).setFont(new Font(defaultFontName,defaultFontSize));
            ((Text)shape).setBoundsType(TextBoundsType.VISUAL);
        } else if(shape instanceof Polyline){
            ((Polyline)shape).setStroke(defaultStrokePaint);
            ((Polyline)shape).setStrokeWidth(defaultStrokeWidth);
        }else if(shape instanceof Shape){
            ((Shape)shape).setFill(defaultFillPaint);
            ((Shape)shape).setStroke(defaultStrokePaint);
            ((Shape)shape).setStrokeWidth(defaultStrokeWidth);
        }else if(shape instanceof ImageView){
            ((ImageView)shape).setFitHeight(100);
            ((ImageView)shape).setFitWidth(100);
        }
        this.setPadding(new Insets(INSETSIZE,INSETSIZE,INSETSIZE,INSETSIZE));
        this.setMinSize(10,10);
        this.setOpacity(0.5f);
        this.getChildren().add(shape);
         
    }
    
    public void changeSize(double startX, double startY, double mouseX, double mouseY){
        switch(this.getShapeType()){
            case CIRCLE: 
                //this.resizeRelocate(Math.min(startX,mouseX), Math.min(startY,mouseY), Math.abs(mouseX-startX), Math.abs(mouseY-startY));
                Circle c = ((Circle)shape);
                c.setCenterX(Math.abs(startX-mouseX)/2);
                c.setCenterY(Math.abs(startY-mouseY)/2);
                c.setRadius(Math.max(MyShape.MINSIZE, Math.max((mouseX-startX),(mouseY-startY)))/2);
                break;
            case ROUNDEDRECTANGLE:
            case RECTANGLE:
                Rectangle r = (Rectangle)shape;
                if(!this.isSelected()){
                    this.relocate(Math.min(startX,mouseX), Math.min(startY,mouseY));
                    r.setWidth (Math.abs(mouseX-startX));
                    r.setHeight(Math.abs(mouseY-startY));
                }else{
                  r.setWidth (Math.max(MyShape.MINSIZE, mouseX-startX));
                  r.setHeight(Math.max(MyShape.MINSIZE, mouseY-startY));  
                }
                
                break;
            case TRIANGLE:
                //this.resize(Math.abs(mouseX-startX), Math.abs(mouseY-startY));
                Polygon poly = (Polygon)shape;
                if(!this.isSelected()){
                    this.relocate(Math.min(startX,mouseX), Math.min(startY,mouseY));
                }
                poly.getPoints().set(0,startX);poly.getPoints().set(1,startY);
                poly.getPoints().set(2,startX);poly.getPoints().set(3,mouseY);
                poly.getPoints().set(4,mouseX);poly.getPoints().set(5,startY);
                break;
            case IMAGE:
                if(!this.isSelected())this.relocate(Math.min(startX,mouseX), Math.min(startY,mouseY));
                ImageView iv = (ImageView)shape;
                iv.setFitHeight(Math.abs(mouseY-startY));
                iv.setFitWidth(Math.abs(mouseX-startX));
                break;
                
            case TEXT: 
                //this.resizeRelocate(Math.min(startX,mouseX), Math.min(startY,mouseY), Math.abs(mouseX-startX), Math.abs(mouseY-startY));
                Text t = (Text)shape;
                Bounds boundsInLocal = t.getBoundsInLocal();
                double h = boundsInLocal.getHeight();
                double w = boundsInLocal.getWidth();
                double newHeight = mouseY-startY - getInsets().getTop() - getInsets().getBottom();
                double newWidth = mouseX-startX - getInsets().getLeft() - getInsets().getRight();
                double wr = newWidth/w;
                double hr = newHeight/h;
                double scale = Math.min(wr, hr);
                double newSize = Math.max(t.getFont().getSize()*scale,2);
                String name = t.getFont().getName();
                t.setFont(new Font(name,newSize));   
                break;
            case PIXELSPRAY:
                break;
            case LINE:
                this.resizeRelocate(Math.min(startX,mouseX), Math.min(startY,mouseY), Math.abs(mouseX-startX), Math.abs(mouseY-startY));
                Polyline p = (Polyline)shape;
                p.getPoints().set(0, startX);
                p.getPoints().set(1, startY);
                p.getPoints().set(2, mouseX);
                p.getPoints().set(3, mouseY);
                break;
            case SCRIBBLE:
                System.out.println(mouseX+" | "+this.getBoundsInParent().getMinX());
                Polyline s = (Polyline)shape;
                s.getPoints().addAll(mouseX, mouseY);
                break;
        
        }
    }
 
    public boolean isSelected(){
        return selected.get();
    }
    public void setSelected(boolean value){
        selected.set(value);
        if(value)this.setBackground(new Background(new BackgroundFill(defaultSelectedPaint,CornerRadii.EMPTY,Insets.EMPTY)));
        else this.setBackground(Background.EMPTY);

    }
    public BooleanProperty selectedProperty(){
        return selected;
    } 
    
    /*
    shapeContains - 
        A shape (Circle, Rectangle, ...) is contained in a StackPane.
        This method checks to see if the mouse is inside the actual shape,
        and not just inside the StackPane.
    */
    public boolean shapeContains(double x, double y){
        if (shape instanceof Circle) return !shape.contains(x-this.getWidth()/2, y-this.getHeight()/2);
        else if(shape instanceof Rectangle)
            return shape.contains(x-this.getInsets().getLeft(), y-this.getInsets().getTop());
        else if(shape instanceof Text){
            Insets insets = this.getInsets();
            return x>insets.getLeft()&& x < this.getWidth()-insets.getRight()&&y>insets.getTop()&&y<this.getHeight()-insets.getBottom();   
        }
        else return this.contains(x+this.getInsets().getLeft(), y+this.getInsets().getTop());
        
    }
    
    public Node getNode(){
        return this.shape;
    }
    public static void setDefaultFillPaint(Paint value){
        defaultFillPaint = value;
    }
    public static void setDefaultStrokePaint(Paint value){
        defaultStrokePaint = value;
    }
    public static void setDefaultSelectedPaint(Paint value){
        defaultSelectedPaint = value;
    }
    public static void setDefaultStrokeWidth(double value){
        defaultStrokeWidth = value;
    }
    public static void setDefaultShapeType(int value){
        defaultShapeType.set(value);
    }
    public static void setDefaultWidth(double value){
        defaultWidth = value;
    }
    public static void setDefaultHeight(double value){
        defaultHeight = value;
    }
    public static double getDefaultWidth(){
        return defaultWidth;
    }
    public static double getDefaultHeight(){
        return defaultHeight;
    } 
    public static void setDefaultFontName(String value){
        defaultFontName = value;
    }
    public static void setDefaultFontSize(double value){
        defaultFontSize = value;
    }
    public static String getDefaultFontName(){
        return defaultFontName;
    }
    public static double getDefaultSize(){
        return defaultFontSize;
    } 
    public static int getDefaultShapeType(){
        return defaultShapeType.get();
    }
    public static IntegerProperty defaultShapeProperty(){
        return defaultShapeType;
    }
    @Override
    public void setFillColor(Color value){
        if(shape instanceof Text){// Remember that Text isa Shape
            ((Text)shape).setFill(value);
        }else if (shape instanceof Shape){
          ((Shape)shape).setFill(value);
        }
    }
    @Override
    public void setStrokeColor(Color value){
        if(shape instanceof Text){
            ((Text)shape).setStroke(value);
        } else if (shape instanceof Shape){
          ((Shape)shape).setStroke(value);
        }
    }
    @Override
    public void setStrokeWidth(double value){
        if(shape instanceof Text){
            ((Text)shape).setStrokeWidth(value);
        }else if (shape instanceof Shape){
          ((Shape)shape).setStrokeWidth(value);
        }
    }
    @Override
    public void setStrokeDashArray(double [] value){
        if (shape instanceof Text){ 
            ((Shape)shape).getStrokeDashArray().clear();
            for(int i = 0; i < value.length; i++)
               ((Shape)shape).getStrokeDashArray().add(value[i]);
        }else if(shape instanceof Shape){
            ((Shape)shape).getStrokeDashArray().clear();
            for(int i = 0; i < value.length; i++)
               ((Shape)shape).getStrokeDashArray().add(value[i]);
        }
    }
    @Override
    public void setFont(Font value) {
        if(shape instanceof Text){
           ((Text)shape).setFont(value);
        }
    }
    @Override
    public Paint getFillColor() {
        if(shape instanceof Shape)
            return ((Shape)shape).getFill();
        else return Color.BLACK;
    }
    @Override
    public Paint getStrokeColor() {
        if(shape instanceof Shape)
            return ((Shape)shape).getStroke();
        else return Color.BLACK;
    }
    @Override
    public double getStrokeWidth() {
        if(shape instanceof Shape){
            return ((Shape)shape).getStrokeWidth();
        } else return 0;
    }
    @Override
    public double[] getStrokeDashArray() {
        if(shape instanceof Shape){
            ObservableList<Double> l= ((Shape)shape).getStrokeDashArray();
            double [] a = new double[l.size()];
            for(int i = 0; i < a.length; i++)
                a[i] = l.get(i);
            return a;
        } else return new double[0];        
    }
    @Override
    public Font getFont() {
        if(shape instanceof Text){
           return ((Text)shape).getFont();
        }
        else return Font.getDefault();//System default
    }

    @Override
    public void setText(String value) {
        if(shape instanceof Text){
           ((Text)shape).setText(value);
        }
    }

    @Override
    public String getText() {
        if(shape instanceof Text){
           return ((Text)shape).getText();
        }
        else return "";
    }
    
    public int getShapeType(){
        return shapeType;
    }
}