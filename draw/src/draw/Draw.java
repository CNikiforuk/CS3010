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


public class Draw extends Application {
    
    private TogglePane togglePane = new TogglePane(30f);//pane for toggles
    private PaintPane paintPane = new PaintPane();      //pane for canvas
    private MyShape tmpShape = new MyShape();           //tmp for adding shape
    private static MyShape selectedShape = null;        //currently selected shape
    
    private MyEvents events = new MyEvents();           //stack for events        
    private IntegerProperty eventIndex = new SimpleIntegerProperty(0);
    private int eventEnd = 0;
    
    private double startx;                              //starting mouse positions when clicked  
    private double starty;
    private Text mousePosX = new Text();                //mouse positions on bottom bar
    private Text mousePosY = new Text();

    private boolean relocating;                         //bools for setting when shapes
    private boolean resizing;                           //are being moved or resized
    
    private final Slider[] sliderSRGB = new Slider[4];  //stroke colour slider
    private final Slider[] sliderFRGB = new Slider[4];  //fill colour slider
    
    
    //Colors and properties for stroke and fill binding.
    private DoubleProperty[] SRGB = {new SimpleDoubleProperty(0),new SimpleDoubleProperty(0),new SimpleDoubleProperty(0),new SimpleDoubleProperty(1)};
    private DoubleProperty[] FRGB = {new SimpleDoubleProperty(0),new SimpleDoubleProperty(1),new SimpleDoubleProperty(1),new SimpleDoubleProperty(1)};
    private Color stroke =  Color.color(SRGB[0].get(),SRGB[1].get(),SRGB[2].get(),SRGB[3].get());
    private Color fill = Color.color(FRGB[0].get(),FRGB[1].get(),FRGB[2].get(),FRGB[3].get());
    
    private Rectangle strokeBox = new Rectangle(40,60); //UI strokebox
    private Rectangle fillBox = new Rectangle(40,60);   //UI fillbox
    
    private ComboBox fontCB = new ComboBox<String>();           //font combobox
    
    private MenuItem mSave; //save menu item 
    
    //listeners for fill and stroke changes for UI
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
        MenuItem mPrint = new MenuItem("Print");
        mSave = new MenuItem("Save");
        menuFile.getItems().addAll(mNew, mOpen, mSave, mPrint);
        
        Menu menuEdit = new Menu("Edit");
        MenuItem mUndo = new MenuItem("Undo"); mUndo.setDisable(true);
        MenuItem mRedo = new MenuItem("Redo"); mRedo.setDisable(true);
        MenuItem mDelete = new MenuItem("Delete");
        
        mUndo.disableProperty().bind(events.emptyProperty());
        mRedo.disableProperty().bind(events.atEndProperty());
        
        menuEdit.getItems().addAll(mUndo, mRedo, mDelete);
        
        Menu menuView = new Menu("View");
        
        Menu menuHelp = new Menu("Help");
        MenuItem mHelp = new MenuItem("Help");
        MenuItem mAbout = new MenuItem("About");
        menuHelp.getItems().addAll(mHelp, mAbout);
        
        Stage helpStage = new Stage();
        helpStage.initOwner(primaryStage);
        helpStage.setTitle("Help");
        Text helpText = new Text("CREATING SHAPES\nUse the toggles on the left to create shapes.\n"
                            + "At the top, the left coloured rectangle is Stroke colour, right is Fill colour.\n" 
                            + "\nEDITING SHAPES\nThe grabber tool will let you select and modify the shapes you have created\n"
                            + "When selected, you can move,resize, and recolor shapes.\n"
                            + "Drag the bottom right corner of shapes to resize them.\n"
                            + "Use the tools at the top to change color of shapes you are editing or drawing.\n"
                            + "'Front' and 'Back' buttons let you change the z-order of shapes.\n"
                            + "\nIMAGES\nfile -> load will load the selected image into a new imagebox. \n"
                            + "If an imagebox is selected, the image will be loaded there instead.\n"
                            + "Save your current work as an image file by selecting File -> Save\n"
                            + "Image opacity can be changed by modifying the fill alpha.\n"
                            + "\nHOTKEYS\nBasic hotkeys work, ctrl-o to open image, ctrl-p to print, etc.\n"
                            
                            );
        helpText.setFont(new Font("Verdana", 13));
        helpText.setLineSpacing(5);
        VBox helpBox = new VBox(helpText);
        Scene helpScene = new Scene(helpBox,600,400);
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
               if (file != null){
                    if(selectedShape != null && selectedShape.getShapeType() == MyShape.IMAGE){
                        ImageView iv = ((ImageView)selectedShape.getNode());
                        iv.setImage(new Image("file:"+file.getPath()));
                    }else{
                        MyShape tmp = new MyShape(MyShape.IMAGE);
                        tmpShape = tmp;
                        events.add(new Event(Event.ADD, tmp));
                        eventIndex.set(eventIndex.get()+1);
                        eventEnd = eventIndex.get();

                        startx = 0;
                        starty = 0;

                        tmp.relocate(startx, starty);
                        tmp.setOnMousePressed(e->shapePressed(e,tmp));
                        tmp.setOnMouseReleased(e->shapeReleased(e,tmp));
                        tmp.setOnMouseDragged(e->shapeDragged(e,tmp));
                        ImageView iv = ((ImageView)tmp.getNode());
                        iv.setImage(new Image("file:"+file.getPath()));
                        paintPane.getChildren().add(tmp);
                    }
               }
 
            }catch(Exception e){System.out.println(e);}
            
        });
        
        mHelp.setOnAction(e -> {
            helpStage.show();
        });
        mAbout.setOnAction(e -> {
            aboutStage.show();
        });
        
        mUndo.setOnAction(e -> {
            undo();
        });
        
        mRedo.setOnAction(e -> {
            redo();
        });
        
        mDelete.setOnAction(e -> {
            if(selectedShape != null) {
                    paintPane.getChildren().remove(selectedShape);
                    events.add(new Event(Event.REMOVE, selectedShape));
                }
        });
        
        
        
        mPrint.setOnAction(e -> {
            print(paintPane);
        });
        
        mNew.setOnAction(e -> {
           paintPane.getChildren().clear();
           events.clear();
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
       
        togglePane.getToggleGroup().selectedToggleProperty().addListener(e ->{
            togglePane.selectedShapeProperty().set(togglePane.getSelectedToggle());
            
            if(selectedShape != null){
                strokeBox.fillProperty().removeListener(strokeListener);
                fillBox.fillProperty().removeListener(fillListener);
                selectedShape.setSelected(false);
                selectedShape = null;
            }
        });
        MyShape.defaultShapeProperty().bind(togglePane.selectedShapeProperty()); //bind default shape to selected
        
        paintPane.setOnMousePressed(e -> setOnMousePressed(e));
        paintPane.setOnMouseDragged(e -> setOnMouseDragged(e));
        paintPane.setOnMouseReleased(e -> setOnMouseReleased(e));
        
        Scene scene = new Scene(root, 800, 600);
        
        mSave.setOnAction((ActionEvent event) -> {
            save(primaryStage, scene);    
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
                if(selectedShape != null) {
                    paintPane.getChildren().remove(selectedShape);
                    events.add(new Event(Event.REMOVE, selectedShape));
                }
                break;
            }
            case P:{
                if(e.isControlDown()){print(paintPane); break;}
            }
            case S:{
                if(e.isControlDown()){mSave.fire();}
                break;
            }
            case Z:{
                if(e.isControlDown()){
                    undo();
                }break;
            }
            case Y:{
                if(e.isControlDown()){
                    redo();
                }break;
            }
        }
    }
    
    private void undo(){
        if(events.getEnd() > 0){
            switch(events.getEvent().getCode()){
                case Event.ADD:
                    if(!paintPane.getChildren().remove((MyShape)events.getEvent().getObject())) System.out.println("End of Events!");
                    break;
                case Event.REMOVE:
                    try{
                        paintPane.getChildren().add((MyShape)events.getEvent().getObject());
                    }catch(IllegalArgumentException ex){System.out.println("End of Events!");}
                    break;
            }
            events.decrement();
        }
    }
    
    private void redo(){
        if(events.getEnd() > 0){
            events.increment();
            switch(events.getEvent().getCode()){
                case Event.ADD:
                        try{
                            paintPane.getChildren().add((MyShape)events.getEvent().getObject());
                        }catch(IllegalArgumentException ex){System.out.println("End of Events!");}
                    break;
                case Event.REMOVE:
                    if(!paintPane.getChildren().remove((MyShape)events.getEvent().getObject()))System.out.println("End of Events!");
                    break;
            }
        }
    }
    
    private void save(Stage stage, Scene scene){
        FileChooser fc = new FileChooser();
            fileSaverFilter(fc);
            File file = fc.showSaveDialog(stage);
            WritableImage tmp = new WritableImage((int)scene.getWidth(),(int)scene.getHeight());
            if (file != null){
                try{
                    System.out.println(fc.getSelectedExtensionFilter().getDescription().toLowerCase());
                    ImageIO.write(SwingFXUtils.fromFXImage(paintPane.snapshot(null, tmp), null), fc.getSelectedExtensionFilter().getDescription().toLowerCase(), file);
                }catch(Exception e){
                    System.out.println(e);
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
                events.add(new Event(Event.ADD, tmp));
                eventIndex.set(eventIndex.get()+1);
                eventEnd = eventIndex.get();

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
  
    
    private static void fileChooserFilter(final FileChooser f){
        f.setTitle("Open Picture");
        f.setInitialDirectory(new File(System.getProperty("user.home")));
        f.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG","*.jpg"),
                new FileChooser.ExtensionFilter("PNG","*.png"),
                new FileChooser.ExtensionFilter("GIF","*.gif")
        );
        
    }
    
    private static void fileSaverFilter(final FileChooser f){
        f.setTitle("Save Picture");
        f.setInitialDirectory(new File(System.getProperty("user.home")));
        f.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG","*.jpg"),
                new FileChooser.ExtensionFilter("PNG","*.png"),
                new FileChooser.ExtensionFilter("GIF","*.gif")
        );
        
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
