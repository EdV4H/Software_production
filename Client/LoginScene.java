import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class LoginScene {
    private Scene scene = null;
    private Parent root = null;

    public LoginScene () {
        try {
            root = FXMLLoader.load(getClass().getResource("loginScene.fxml"));
            scene = new Scene (root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Scene getScene () {return scene;}
}