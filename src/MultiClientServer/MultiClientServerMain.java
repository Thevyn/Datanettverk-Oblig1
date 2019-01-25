package MultiClientServer; /**
 * Multiclient socket server with JavaFX: Main class
 * DATA/ITPE2410 Networking and Cloud Computing, Spring 2019
 * Raju Shrestha, OsloMet
 **/

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MultiClientServerMain extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("multiclientserver.fxml"));
        primaryStage.setTitle("DATA2410/ITPE2410: Multi-client Socket - Server");
        primaryStage.setScene(new Scene(root, 600, 480));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop()
    {
        System.exit(0);
    }
}
