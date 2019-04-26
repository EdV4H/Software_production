import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class LoginController {

    private String serverIp = "localhost";

    private boolean isNextScene = false;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button button_selectserver;

    @FXML
    private TextField textfield_ip;

    @FXML
    private Button button_connectserver;

    @FXML
    private TextField textfield_name;

    @FXML
    private ChoiceBox<?> choicebox_sample;

    @FXML
    private ChoiceBox<String> choicebox_gamemode;

    @FXML
    private Label label_serverip;

    @FXML
    private void SetServerIp () {
        serverIp = textfield_ip.getText();
        label_serverip.setText(serverIp);
    }

    @FXML
    private void ConnetcServer () {
        isNextScene = true;
        textfield_ip.setDisable(true);
        textfield_name.setDisable(true);
        button_selectserver.setDisable(true);
        button_connectserver.setDisable(true);
        choicebox_sample.setDisable(true);
        choicebox_gamemode.setDisable(true);

        String gamemode = choicebox_gamemode.getValue();
        switch (gamemode) {
            case "Debug":
                break;
            case "FreeForAll":
                label_serverip.setText("サーバー接続中...");
                SceneManager.connectToServer(serverIp, textfield_name.getText());
                break;
        }
        SceneManager.changeScene(SceneManager.sceneType.Game);
    }

    public boolean getIsNextScene () {return isNextScene;}

    @FXML
    void initialize() {
        choicebox_gamemode.getItems().add("Debug");
        choicebox_gamemode.getItems().add("FreeForAll");
        choicebox_gamemode.getSelectionModel().select(1);
    }
}
