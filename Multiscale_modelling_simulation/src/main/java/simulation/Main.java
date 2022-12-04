package simulation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        MainView MainView = new MainView();
        Scene scene=new Scene(new StackPane(MainView),600,600);
        primaryStage.setScene(scene);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
