package nl.hazenebula.oubliette;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainPane mainPane = new MainPane(primaryStage);

        primaryStage.setScene(new Scene(mainPane, 1280, 960));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
