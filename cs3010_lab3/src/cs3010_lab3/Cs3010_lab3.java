/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3010_lab3;

import java.awt.Desktop;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
/**
 *
 * @author Car
 */
public class Cs3010_lab3 extends Application {
    
    String lastdir = "left";
    
    private Desktop desktop = Desktop.getDesktop();
    File f = new File("."); //refers to whole directory
    File [] pictures = f.listFiles(new imgFilter()); //all pictures in directory
        
    ArrayList<File> picList = new ArrayList<>(Arrays.asList(pictures));
    ListIterator<File> picIterator = picList.listIterator();
    
    class imgFilter implements FileFilter{
        @Override
        public boolean accept(File pathname) {
            String name = pathname.getName();
            if(name==null)return false;
            if(name.toLowerCase().endsWith(".jpg") || 
               name.toLowerCase().endsWith(".png") ||
               name.toLowerCase().endsWith(".gif"))
            {
               return true;
            }
            else return false;
        }
           
    }

    
    @Override
    public void start(Stage primaryStage) {
        
        Image image = new Image("file:"+picIterator.next().getName());
        ImageView v;
        v = new ImageView(image);
        v.setPreserveRatio(true);
        v.setFitHeight(500);
        v.setFitWidth(600);
        
        System.out.println("file:"+f.getName());
        
        FileChooser fileChooser = new FileChooser();
        
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        MenuItem mOpen = new MenuItem("Open");
        mOpen.setOnAction((ActionEvent event) -> {
           try{
               fileChooserFilter(fileChooser);
               File file = fileChooser.showOpenDialog(primaryStage);
               if (file != null) {
                   display(file, v);
               }
 
            }catch(Exception e){System.out.println(e);}
            
        });
        
        MenuItem mSave = new MenuItem("Save");
        mSave.setOnAction((ActionEvent event) -> {
            FileChooser fc = new FileChooser();
            fileChooserFilter(fc);
            fc.setTitle("Save Image");
            System.out.println(v.getId());
            File file = fc.showSaveDialog(primaryStage);
            if (file != null){
                try{
                    System.out.println(fc.getSelectedExtensionFilter().getDescription().toLowerCase());
                    ImageIO.write(SwingFXUtils.fromFXImage(v.getImage(), null), fc.getSelectedExtensionFilter().getDescription().toLowerCase(), file);
                }catch(Exception e){
                    System.out.println(e);
                }
            }
            
        });
        
        
        menuFile.getItems().addAll(mOpen, mSave);
        Menu menuEdit = new Menu("Edit");
        Menu menuView = new Menu("View");
        
        menuBar.getMenus().addAll(menuFile, menuEdit, menuView);
        
    
        Button btnPrev = new Button();
        btnPrev.setText("Prev");
        btnPrev.setOnAction((ActionEvent event) -> {
           try{
                if(picIterator.hasPrevious()){
                    String tmp = picIterator.previous().getPath();
                    if(lastdir == "right"){tmp = picIterator.previous().getPath();}
                    lastdir = "left";
                    System.out.println("Loading "+tmp);
                    v.setImage(new Image("file:"+tmp));
                    event.consume();
                }
                
            }catch(Exception e){System.out.println(e);}
            
        });
        
        
        Button btnNext = new Button();
        btnNext.setText("Next");
        btnNext.setOnAction((ActionEvent event) -> {
            try{
                if(picIterator.hasNext()){
                    String tmp = picIterator.next().getPath();
                    if(lastdir == "left"){tmp = picIterator.next().getPath();}
                    lastdir = "right";
                    System.out.println("Loading "+tmp);
                    v.setImage(new Image("file:"+tmp));
                    event.consume();
                }
            }catch(Exception e){System.out.println(e);}
        });
        
        GridPane gp = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col2.setPercentWidth(50);

        StackPane sp = new StackPane();
        sp.getChildren().add(v);
        
        GridPane.setHalignment(btnPrev, HPos.RIGHT);
        GridPane.setHalignment(btnNext, HPos.LEFT);
        gp.getColumnConstraints().addAll(col1,col2);
       
        gp.add(btnPrev, 0, 0);
        gp.add(btnNext, 1, 0);
        
        BorderPane.setAlignment(btnPrev, Pos.TOP_CENTER);
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(sp);
        root.setBottom(gp);
        
        root.setOnMouseClicked(e -> {
            double tmp1 = e.getScreenX();
            root.setOnMouseReleased(e2 -> {
                double tmp2 = e2.getScreenX();
                if(tmp2 -tmp1 > 100){
                    if(picIterator.hasPrevious()){
                        String t = picIterator.previous().getPath();
                        if(lastdir == "right"){t = picIterator.previous().getPath();}
                        lastdir = "left";
                        System.out.println("Loading "+t);
                        v.setImage(new Image("file:"+t));
                    }
                }
                else if(tmp1 - tmp2 > 100){
                    if(picIterator.hasNext()){
                        String t = picIterator.next().getPath();
                        if(lastdir == "left"){t = picIterator.next().getPath();}
                        lastdir = "right";
                        System.out.println("Loading "+t);
                        v.setImage(new Image("file:"+t));
                    }
                }
                
                System.out.println("tmp: "+tmp1+" "+tmp2);
                
            });
        });

        //root.getChildren().addAll(menuBar, btnPrev, btnNext, v);
        
        Scene scene = new Scene(root, 600, 600);
        
        
        primaryStage.setTitle("Image Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
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
    
    private void display(File file, ImageView v){
       f = file;
       File p = new File(f.getParentFile().getPath());
       pictures = p.listFiles(new imgFilter());
       picList = new ArrayList<>(Arrays.asList(pictures));
       picIterator = picList.listIterator(picList.indexOf(f));
       
       System.out.println(f.getParentFile().getPath());
       
       v.setImage(new Image("file:"+picIterator.next().getPath()));

    }
    
}
