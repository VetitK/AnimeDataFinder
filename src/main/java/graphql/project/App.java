package graphql.project;

import graphql.project.controller.AnimeController;
import graphql.project.utils.Resources;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;



public class App extends Application {

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        // loader.setLocation(Resources.getUI("/animeWindow.fxml"));
        
        loader.setLocation(Resources.getUI("animeWindow.fxml"));
        loader.setController(new AnimeController());
        // TabPane pane = loader.load();
        TabPane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setTitle("Anime App");
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}