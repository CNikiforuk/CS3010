/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package draw;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterAttributes;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * @author Carlos N.
 */

/*
Drawable - 
Defines methods used to get/set "properties" in a MyShape etc.

Bug Fixes - 
I added getters and a few setters
*/
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

/*
MyShape - 

This is the shape, CIRCLE,RECTANGLE,TRIANGLE,IMAGE,TEXT, ...

The MyShape is actually a StackPane and the "shape" is placed inside the
StackPane with small insets. (5 by default)

The class has defaults that can be changed.  The default shape is CIRCLE
for example. An instance is constructed according to the defaults.

Once a MyShape is constructed, several "properties" can be changed: fillColor,
strokeColor, strokeWide, strokeDashArray, Font, ...

A MyShape can be selected and moved and resized.

Bug Fixes - March 20, 2015
Made several changes to the MyShape class
1) Text isa Shape so if checking for instances check Text before Shape
   - Several methods were modified because of this
2) Text can be selected, resized and moved now
3) The shapeContains method handles Text now
4) Several changes were made regarding the use of Font.

*/

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


public class Draw extends Application {
    
    private TogglePane togglePane = new TogglePane(30f);//pane for toggles
    private PaintPane paintPane = new PaintPane();      //pane for drawing
    public Stack events;
    
    private double startx;          
    private double starty;
    public double startW;
    public double startH;
    private Text mousePosX = new Text();
    private Text mousePosY = new Text();
    
    private MyShape tmpShape = new MyShape();
    private static MyShape selectedShape = null;
    
    
    private boolean relocating;
    private boolean resizing;
    
    private final Slider[] sliderSRGB = new Slider[4];
    private final Slider[] sliderFRGB = new Slider[4]; 
    private DoubleProperty[] SRGB = {new SimpleDoubleProperty(0),new SimpleDoubleProperty(0),new SimpleDoubleProperty(0),new SimpleDoubleProperty(1)};
    private DoubleProperty[] FRGB = {new SimpleDoubleProperty(0),new SimpleDoubleProperty(1),new SimpleDoubleProperty(1),new SimpleDoubleProperty(1)};
    private Color stroke =  Color.color(SRGB[0].get(),SRGB[1].get(),SRGB[2].get(),SRGB[3].get());
    private Color fill = Color.color(FRGB[0].get(),FRGB[1].get(),FRGB[2].get(),FRGB[3].get());
    private Rectangle fillBox = new Rectangle(40,60);
    private Rectangle strokeBox = new Rectangle(40,60);
    
    ComboBox fontCB = new ComboBox<String>();
    
    private ChangeListener fillListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
        if(selectedShape != null){
            selectedShape.setFillColor((Color) fillBox.getFill());
            if(selectedShape.getShapeType() == MyShape.IMAGE){((ImageView)selectedShape.getNode()).setOpacity(((Color)fillBox.getFill()).getOpacity());}
        }
        
    };
    private ChangeListener strokeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
        if(selectedShape != null)selectedShape.setStrokeColor((Color) strokeBox.getFill());   
    };
    
    @Override
    public void start(Stage primaryStage) {
        
        fillBox.setFill(fill);
        fillBox.setStroke(Color.BLACK);
        MyShape.setDefaultFillPaint(fill);
        strokeBox.setFill(stroke);
        strokeBox.setStroke(Color.BLACK);
        MyShape.setDefaultStrokePaint(stroke);
        
        double tick = 0.5f;
        double block = 0.25f;
        double maxW = 50;
        double maxH = 15;
        
        for(int i=0; i<sliderSRGB.length;i++){sliderSRGB[i] = new Slider(0,1,SRGB[i].get());}
        for(int i=0; i<sliderFRGB.length;i++){sliderFRGB[i] = new Slider(0,1,FRGB[i].get());}
        
        for(int i=0; i<sliderSRGB.length;i++){
            sliderSRGB[i].setMajorTickUnit(tick);
            sliderSRGB[i].setBlockIncrement(block);
            sliderSRGB[i].setMaxSize(maxW, maxH);
            sliderSRGB[i].setPrefSize(maxW, maxH);
            SRGB[i].bind(sliderSRGB[i].valueProperty());
            sliderSRGB[i].valueProperty().addListener((javafx.beans.Observable ob) -> {
                stroke = Color.color(SRGB[0].get(),SRGB[1].get(),SRGB[2].get(),SRGB[3].get());
                strokeBox.setFill(stroke);
                MyShape.setDefaultStrokePaint(stroke);
            });
  
        }

        for(int i=0; i<sliderFRGB.length;i++){
            sliderFRGB[i].setMajorTickUnit(tick);
            sliderFRGB[i].setBlockIncrement(block);
            sliderFRGB[i].setMaxSize(maxW, maxH);
            sliderFRGB[i].setPrefSize(maxW, maxH);
            FRGB[i].bind(sliderFRGB[i].valueProperty());
            sliderFRGB[i].valueProperty().addListener((javafx.beans.Observable ob) -> {
                fill = Color.color(FRGB[0].get(),FRGB[1].get(),FRGB[2].get(),FRGB[3].get());
                fillBox.setFill(fill);
                MyShape.setDefaultFillPaint(fill);  
            });
        }
        
        
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        MenuItem mNew = new MenuItem("New");
        MenuItem mOpen = new MenuItem("Open");
        MenuItem mSave = new MenuItem("Save");
        
        menuFile.getItems().addAll(mNew, mOpen, mSave);
        Menu menuEdit = new Menu("Edit");
        Menu menuView = new Menu("View");
        Menu menuHelp = new Menu("Help");
        MenuItem mHelp = new MenuItem("Help");
        MenuItem mAbout = new MenuItem("About");
        menuHelp.getItems().addAll(mHelp, mAbout);
        
        Stage helpStage = new Stage();
        helpStage.initOwner(primaryStage);
        helpStage.setTitle("Help");
        Text helpText = new Text("Use the toggles on the left to create shapes.\n"
                            + "The grabber tool will let you select and modify the shapes you have created\n"
                            + "When selected, you can move and resize shapes. Drag the border to resize.\n"
                            + "Use the tools at the top to change color of shapes you are editing or drawing.\n"
                            + "'Front' and 'Back' buttons let you change the depth of shapes.\n"
                            + "To load images, first create an ImageBox, select it, and choose Load Image\n"
                            + "Save your current work by selecting File -> Save");
        helpText.setFont(new Font("Verdana", 13));
        helpText.setLineSpacing(5);
        VBox helpBox = new VBox(helpText);
        Scene helpScene = new Scene(helpBox,550,200);
        helpStage.setScene(helpScene);
        
        Stage aboutStage = new Stage();
        aboutStage.initOwner(primaryStage);
        aboutStage.setTitle("About");
        VBox aboutText = new VBox(new Text("Made by Carlos Nikiforuk\nVersion -1.0"));
        Scene aboutScene = new Scene(aboutText,300,200);
        aboutStage.setScene(aboutScene);
        
        FileChooser fileChooser = new FileChooser();
        mOpen.setOnAction((ActionEvent event) -> {
           try{
               fileChooserFilter(fileChooser);
               File file = fileChooser.showOpenDialog(primaryStage);
               if (file != null && selectedShape != null && selectedShape.getShapeType() == MyShape.IMAGE) {
                   ImageView iv = ((ImageView)selectedShape.getNode());
                   iv.setImage(new Image("file:"+file.getPath()));
               }
 
            }catch(Exception e){System.out.println(e);}
            
        });
        
        mHelp.setOnAction(e -> {
            helpStage.show();
        });
        mAbout.setOnAction(e -> {
            aboutStage.show();
        });
        
        menuBar.getMenus().addAll(menuFile, menuEdit, menuView, menuHelp);
        
        BorderPane root = new BorderPane();
        
        VBox topVB = new VBox();
        HBox hb = new HBox();
        hb.setPadding(new Insets(5,5,5,5));
        VBox sliderVB1 = new VBox();
        VBox sliderVB2 = new VBox();
        
        Button moveToBack = new Button("Back");
        moveToBack.setPrefSize(60, 30);
        moveToBack.setOnAction(e ->{
            if(selectedShape != null) {selectedShape.toBack();}
        });
        Button moveToFront = new Button("Front");
        moveToFront.setPrefSize(60, 30);
        moveToFront.setOnAction(e ->{
            if(selectedShape !=null) {selectedShape.toFront();}
        });
        
        VBox depthVB = new VBox(moveToBack, moveToFront);
        depthVB.setPadding(new Insets(0,10,0,10));
        
        
        fontCB.setMaxWidth(160);
        fontCB.getItems().addAll(Font.getFamilies());
        fontCB.setOnAction(e ->{
            MyShape.setDefaultFontName(((String)fontCB.getSelectionModel().selectedItemProperty().get()));
            System.out.println("Font set to: "+((String)fontCB.getSelectionModel().selectedItemProperty().get()));
            if(selectedShape != null && selectedShape.getShapeType() == MyShape.TEXT){
                ((Text)selectedShape.getNode()).setFont(new Font(((String)fontCB.getSelectionModel().selectedItemProperty().get()), ((Text)selectedShape.getNode()).getFont().getSize()));
            }
        });
        fontCB.getSelectionModel().select("Times New Roman");
        VBox fontVB = new VBox(fontCB);
        
        
        hb.setPadding(new Insets(5,5,5,5));
        hb.getChildren().addAll(strokeBox, sliderVB1, fillBox, sliderVB2, depthVB, fontVB);
        topVB.getChildren().addAll(menuBar, hb);
        
        String rgba = "RGBA";
        for(int i=0; i<sliderSRGB.length;i++){
            HBox tmp = new HBox(new Text(String.valueOf(rgba.charAt(i)+"")),sliderSRGB[i]); 
            tmp.setPadding(new Insets(0,5,0,5)); 
            sliderVB1.getChildren().add(tmp);
        }
        for(int i=0; i<sliderFRGB.length;i++){
            HBox tmp = new HBox(new Text(String.valueOf(rgba.charAt(i)+"")),sliderFRGB[i]); 
            tmp.setPadding(new Insets(0,5,0,5)); 
            sliderVB2.getChildren().add(tmp);
        }
        
        HBox statusBar = new HBox(new Text(" X: "), mousePosX, new Text(" Y: "), mousePosY);
        statusBar.setStyle("-fx-background-color: gainsboro");
        
        root.setTop(topVB);
        root.setLeft(togglePane);
        root.setCenter(paintPane);
        root.setBottom(statusBar);
        
        TextArea tempText = new TextArea();
        tempText.setText("Hello!");
        tempText.setPrefSize(1,1);
        tempText.setWrapText(true);
        tempText.setPromptText("your text here");
        //tempText.setStyle("-fx-background-color: transparent;");
        tempText.getStylesheets().add("file:textArea.css");
       
        MyShape.defaultShapeProperty().bind(togglePane.selectedToggleProperty());
        
        paintPane.setOnMousePressed(e -> setOnMousePressed(e));
        paintPane.setOnMouseDragged(e -> setOnMouseDragged(e));
        paintPane.setOnMouseReleased(e -> setOnMouseReleased(e));
        
        Scene scene = new Scene(root, 800, 600);
        
        mSave.setOnAction((ActionEvent event) -> {
            FileChooser fc = new FileChooser();
            fileChooserFilter(fc);
            fc.setTitle("Save Image");
            File file = fc.showSaveDialog(primaryStage);
            WritableImage tmp = new WritableImage((int)scene.getWidth(),(int)scene.getHeight());
            if (file != null){
                try{
                    System.out.println(fc.getSelectedExtensionFilter().getDescription().toLowerCase());
                    ImageIO.write(SwingFXUtils.fromFXImage(paintPane.snapshot(null, tmp), null), fc.getSelectedExtensionFilter().getDescription().toLowerCase(), file);
                }catch(Exception e){
                    System.out.println(e);
                }
            }        
        });
        
        paintPane.setOnMouseMoved(e -> mouseHandler(e));
        scene.setOnKeyPressed(e -> keyPressHandler(e));
        scene.setOnKeyTyped(e -> keyTypedHandler(e));
        primaryStage.setTitle("Draw!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
    public void print(final Node node) {
        ObservableSet<Printer> allPrinters = Printer.getAllPrinters();
        for(Printer p:allPrinters){ //List  all the printers
            System.out.println(p.getName());
        }
    
        Printer printer = Printer.getDefaultPrinter();
        PrinterAttributes printerAttributes = printer.getPrinterAttributes();
        // Do something with the attributes of the default printer
        
        PageLayout pageLayout = printer.createPageLayout(Paper.NA_LETTER, 
                PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
 
        System.out.println("Printable Height:"+pageLayout.getPrintableHeight());
        System.out.println("Printable Width :"+pageLayout.getPrintableWidth());
        System.out.println("Top Margin      :"+pageLayout.getTopMargin());
        System.out.println("Bottom Margin   :"+pageLayout.getBottomMargin());
        System.out.println("Left Margin     :"+pageLayout.getLeftMargin());
        System.out.println("Right Margin    :"+pageLayout.getRightMargin());
        
        //You may need to apply a scale transform to the node
        //  and/or use PageOrientation.LANDSCAPE
        
        //Since printing may take some time you may print on a different thread.
        //Otherwise we may slow down the UI and make it seem un-responsive
        
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean success = job.printPage(node);
            if (success) {
                job.endJob();
            }
        }
    }    
    
    private void mouseHandler(MouseEvent e){
        mousePosX.setText(Double.toString(e.getX()));
        mousePosY.setText(Double.toString(e.getY()));
    }
    
    private void keyPressHandler(KeyEvent e){
        if(e.getCode() == KeyCode.BACK_SPACE && selectedShape.getText().length()>0){
            String oldString = selectedShape.getText();
            String newString = oldString.substring(0, oldString.length()-1);
            System.out.println("O: "+oldString.length()+" N: "+newString.length());
            selectedShape.setText(newString);
        }
        
        switch(e.getCode()){
            case DELETE:{
                System.out.println("Delete Pressed!");
                if(selectedShape != null) paintPane.getChildren().remove(selectedShape);
                break;
            }
            case P:{
                if(e.isControlDown()){print(paintPane); break;}
            }
            case S:{
                if(e.isControlDown()){System.out.println("IMPLEMENT SAVE"); break;}
            }
            case Z:{
                if(e.isControlDown()){
                    System.out.println("IMPLEMENT UNDO");
                    
                }
                break;
            }
            case Y:{
                if(e.isControlDown()){System.out.println("IMPLEMENT REDO"); break;}
            }
        }
    }
    
    private void keyTypedHandler(KeyEvent e){
        System.out.println("Key Press: |"+e.getCharacter()+"| ");
        
        if(selectedShape != null && selectedShape.getShapeType() == MyShape.TEXT){
            if(Character.isSpaceChar(e.getCharacter().charAt(0)) || Character.isLetterOrDigit(e.getCharacter().charAt(0))){
                selectedShape.setText(selectedShape.getText()+e.getCharacter());
            }
        }
    }
    
    private void setOnMousePressed(MouseEvent me){
        if(me.getButton() == MouseButton.PRIMARY){
            if(togglePane.getSelectedToggle() != TogglePane.GRABBER){
                MyShape tmp = new MyShape();
                tmpShape = tmp;

                System.out.println("Primary Mouse Press");
                startx = me.getX()-MyShape.INSETSIZE;
                starty = me.getY()-MyShape.INSETSIZE;

                tmp.relocate(startx, starty);
                tmp.setOnMousePressed(e->shapePressed(e,tmp));
                tmp.setOnMouseReleased(e->shapeReleased(e,tmp));
                tmp.setOnMouseDragged(e->shapeDragged(e,tmp));
                paintPane.getChildren().add(tmp);
            
        }
        }
    }
        
        
    private void setOnMouseDragged(MouseEvent me){
        if(me.isPrimaryButtonDown()){
            if(togglePane.getSelectedToggle() == TogglePane.GRABBER){  
            
            }
            else{
               double tmpx, tmpy;
               tmpx = me.getX()-MyShape.INSETSIZE;
               tmpy = me.getY()-MyShape.INSETSIZE;
               if(tmpx > paintPane.getWidth()){tmpx = paintPane.getWidth();}
               if(tmpy > paintPane.getHeight()){tmpy = paintPane.getHeight();}
               if(tmpx < 0){tmpx = 0;}
               if(tmpy < 0){tmpy = 0;}

               tmpShape.changeSize(startx, starty, tmpx, tmpy);
            }
        }
    }
        
    private void setOnMouseReleased(MouseEvent me){
        tmpShape.setOpacity(1f);
        if(me.getButton() == MouseButton.PRIMARY) {
            //s.setOpacity(1f); //s.setSelected(true); 
        }
    }
    
    private void shapePressed(MouseEvent e, MyShape s) {
        
        if(togglePane.getSelectedToggle() == TogglePane.GRABBER && e.getButton() == MouseButton.PRIMARY){
            strokeBox.fillProperty().addListener(strokeListener);
            fillBox.fillProperty().addListener(fillListener);
            
            if(selectedShape != null){
                
                selectedShape.setSelected(false);//remove previously selected shape
                selectedShape = null;
                
                try{
                    sliderSRGB[0].setValue(((Color)s.getStrokeColor()).getRed());
                    sliderSRGB[1].setValue(((Color)s.getStrokeColor()).getGreen());
                    sliderSRGB[2].setValue(((Color)s.getStrokeColor()).getBlue());
                    sliderSRGB[3].setValue(((Color)s.getStrokeColor()).getOpacity());

                    sliderFRGB[0].setValue(((Color)s.getFillColor()).getRed());
                    sliderFRGB[1].setValue(((Color)s.getFillColor()).getGreen());
                    sliderFRGB[2].setValue(((Color)s.getFillColor()).getBlue());
                    sliderFRGB[3].setValue(((Color)s.getFillColor()).getOpacity());
                  
                }catch(NullPointerException exc){System.out.println("NP EXC");}
                
            }
            
            System.out.println("ShapePressed");
            s.setSelected(true);
            if(s.isSelected()){
                selectedShape = s;
                startx = e.getScreenX();
                starty = e.getScreenY();
                startW = s.getWidth();
                startH = s.getHeight();
                
                if(s.getShapeType() == MyShape.TEXT){
                        fontCB.getSelectionModel().select(((Text)s.getNode()).getFont().getName());
                }
            }
            e.consume();
        }  
    }
    
    
    private void shapeDragged(MouseEvent e, MyShape s) {
        
        if(togglePane.getSelectedToggle() == TogglePane.GRABBER && e.getButton() == MouseButton.PRIMARY){
            //System.out.println();
            if(s.isSelected()) {
                
                double newMouseX = e.getScreenX();
                double newMouseY = e.getScreenY();
                double dx = newMouseX-startx;
                double dy = newMouseY-starty;
                startx = newMouseX;
                starty = newMouseY;
                Bounds boundsInParent = s.getBoundsInParent();
                if(boundsInParent.getMinX()+dx<0) if(dx < 0)dx = 0;
                if(boundsInParent.getMaxX()+dx>((PaintPane)s.getParent()).getWidth()) if(dx > 0)dx = 0;
                if(boundsInParent.getMinY()+dy<0) if(dy < 0) dy = 0;
                if(boundsInParent.getMaxY()+dy>((PaintPane)s.getParent()).getHeight()) if(dy > 0)dy = 0;
                
                if((s.shapeContains(e.getX(),e.getY())||relocating||s.getShapeType() == MyShape.LINE || s.getShapeType() == MyShape.SCRIBBLE)&&!resizing){
                   relocating=true;
                   s.relocate(s.getLayoutX()+dx,s.getLayoutY()+dy);
                }
                else{
                   resizing = true;
                   //s.changeSizeButOnlyDuringADrag(s.getWidth(), s.getHeight());
                   double newX = e.getSceneX()-s.getParent().getLayoutX()-MyShape.INSETSIZE;
                   double newY = e.getSceneY()-s.getParent().getLayoutY()-MyShape.INSETSIZE;
                   s.changeSize(s.getLayoutX(),s.getLayoutY(),newX,newY);
                   //s.setPrefHeight(s.getHeight()+dy);
                   //s.setPrefWidth(s.getWidth()+dx); 
                }
            }
            e.consume();
        }
    }
    
    private void shapeReleased(MouseEvent e, MyShape s) {
        System.out.println("ShapeReleased");
        relocating =false;
        resizing   =false;
    }
    
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
    protected myButton[] toggles = new myButton[10];
    protected Image[] images = new Image[10];
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
        
        this.group.selectedToggleProperty().addListener(e ->{
            selected.set(getSelectedToggle());
            
            if(selectedShape != null){
                strokeBox.fillProperty().removeListener(strokeListener);
                fillBox.fillProperty().removeListener(fillListener);
                selectedShape.setSelected(false);
                selectedShape = null;
            }
        });

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
    
    public IntegerProperty selectedToggleProperty(){
        return this.selected;
    }
    
    }
    
    private static void fileChooserFilter( final FileChooser f){
        f.setTitle("Open Picture");
        f.setInitialDirectory(new File(System.getProperty("user.home")));
        f.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG","*.jpg"),
                new FileChooser.ExtensionFilter("PNG","*.png"),
                new FileChooser.ExtensionFilter("GIF","*.gif")
        );
        
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
