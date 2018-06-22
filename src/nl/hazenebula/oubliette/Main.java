package nl.hazenebula.oubliette;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        View view = new View();

        primaryStage.setTitle("Main");
        primaryStage.setScene(new Scene(view, 640, 480));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}