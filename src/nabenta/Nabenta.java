package nabenta;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Nabenta extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.initStyle(StageStyle.UNDECORATED);
        
        Parent root = FXMLLoader.load(getClass().getResource("ParentRoot.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        Image logo = new Image(getClass().getResourceAsStream("assets/logo/logo_circle_noTitle.png"));
        stage.getIcons().add(logo);
        stage.setResizable(false);
        stage.show();
        
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
