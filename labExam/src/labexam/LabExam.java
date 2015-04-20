/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labexam;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author francoc
 */
public class LabExam extends Application {
        Ellipse main = new Ellipse();
        Ellipse left = new Ellipse();
        Ellipse right = new Ellipse();
        Ellipse anim = new Ellipse();
        PathTransition pt = new PathTransition();
        Duration time;
    
    
    @Override
    public void start(Stage primaryStage) {
        
        main.setFill(Color.YELLOW);
        main.setStroke(Color.BLUE);
        main.setStrokeWidth(3);
        
        left.setFill(Color.BLUE);
        left.setOnMouseEntered(e -> leftEnter(e));
        left.setOnMouseExited(e -> leftExited(e));
        
        right.setFill(Color.BLACK);

        anim.setVisible(false);
        
        BorderPane root = new BorderPane();
        root.getChildren().addAll(main, left, right, anim);
        Scene scene = new Scene(root, 400, 400);

        scene.widthProperty().addListener(e -> {
            main.relocate(0, 0);
            left.relocate((scene.getWidth()/3)-left.getRadiusX(), (scene.getHeight()/3)-left.getRadiusY());
            right.relocate((2*scene.getWidth()/3)-right.getRadiusX(), (scene.getHeight()/3)-right.getRadiusY());
            anim.relocate((scene.getWidth()/3)-left.getRadiusX(), (scene.getHeight()/3)-left.getRadiusY());
            time = pt.getCurrentTime();
            pt.stop();
            pt.setPath(left);
            
        });
        scene.heightProperty().addListener(e -> {
            main.relocate(0, 0);
            left.relocate((scene.getWidth()/3)-left.getRadiusX(), (scene.getHeight()/3)-left.getRadiusY());
            right.relocate((2*scene.getWidth()/3)-right.getRadiusX(), (scene.getHeight()/3)-right.getRadiusY());
            anim.relocate((scene.getWidth()/3)-anim.getRadiusX(), (scene.getHeight()/3)-anim.getRadiusY());
            pt.stop();
            pt.setPath(left);
        });
        
        main.radiusXProperty().bind(scene.widthProperty().divide(2));
        main.radiusYProperty().bind(scene.heightProperty().divide(2));
        left.radiusXProperty().bind(scene.widthProperty().divide(10));
        left.radiusYProperty().bind(scene.heightProperty().divide(10));
        right.radiusXProperty().bind(scene.widthProperty().divide(10));
        right.radiusYProperty().bind(scene.heightProperty().divide(10));
        anim.radiusXProperty().bind(scene.widthProperty().divide(30));
        anim.radiusYProperty().bind(scene.heightProperty().divide(30));
        
        pt.setDuration(Duration.millis(8000));
        pt.setPath(left);
        pt.setNode(anim);
        pt.setCycleCount(Timeline.INDEFINITE);
        pt.setAutoReverse(false);
        pt.setInterpolator(Interpolator.LINEAR);
        pt.play();
        pt.pause();
        
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    private void leftEnter(MouseEvent e){
        left.setFill(Color.GREEN);
        pt.play();
        anim.setVisible(true);
    }
    private void leftExited(MouseEvent e){
        left.setFill(Color.BLUE);
        pt.pause();
        anim.setVisible(false);
    }
    
}
