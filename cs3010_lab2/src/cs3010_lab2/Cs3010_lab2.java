/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3010_lab2;


import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Car
 */
public class Cs3010_lab2 extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        TextField txtfield = new TextField("Help!");
        Rectangle r = new Rectangle();
        r.setWidth(200);
        r.setHeight(100);
        r.setFill(Color.RED);
        r.setStroke(Color.BLUE);
        r.setStrokeWidth(10);

        Image img = new Image("file:img1.jpg");
        ImageView imgview = new ImageView(img);
        
        Text txt = new Text("HELLOOOO WORLLLDDD");
        Font font = new Font("courier", 30);
        txt.setFont(font);
        
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        
        BorderPane root = new BorderPane();

        Scene scene = new Scene(root, 640, 480);
        
        
        root.setTop(btn);
        root.setBottom(txtfield);
        root.setCenter(imgview);
        root.setRight(r);
        root.setTop(txt);
        
        double x,y; 
        x = y = 10;
        double spacex, spacey;
        double maxWidth = scene.getWidth();
        double maxHeight = scene.getHeight();
        int rows = 2;
        spacex = maxWidth / (root.getChildren().size() / rows);
        spacey = maxHeight / ((root.getChildren().size() + 1) / rows);
        
        btn.relocate(x,y);
        x+=spacex; 
        if(x >= maxWidth){x = 10; y+=spacey;}
        
        txtfield.relocate(x,y);
        x+=spacex; 
        if(x >= maxWidth){x = 10; y+=spacey;}
        
        r.relocate(x,y);
        x+= spacex; 
        if(x >= maxWidth){x = 10; y+=spacey;}
        
        imgview.relocate(x,y);
        x+=spacex; 
        if(x >= maxWidth){x = 10; y+=spacey;}
        
        txt.relocate(x,y);
        x+=spacex; 
        
        
        primaryStage.setTitle("Hello!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
