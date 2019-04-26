import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class GameScene {
    public static Scene scene = null;
    public static Parent root = null;

    public GameScene () {
        try {
            root = FXMLLoader.load(getClass().getResource("gameScene.fxml")); 
            scene = new Scene (root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Scene getScene () {return scene;}
}