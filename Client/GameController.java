import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;

public class GameController implements Initializable {

    @FXML
    private Pane pane_screen;

    private Game game;

    @FXML
    private TextField textfield_cmd;

    @FXML
    private Button button_send;

    @FXML
    private Button button_ul;

    @FXML
    private Button button_up;

    @FXML
    private Button button_ur;

    @FXML
    private Button button_left;

    @FXML
    private Button button_search;

    @FXML
    private Button button_right;

    @FXML
    private Button button_dl;

    @FXML
    private Button button_dowm;

    @FXML
    private Button button_dr;

    @FXML
    private Button button_fire;

    @FXML
    private Button button_disconnect;

    @FXML
    void cmdDLeft(MouseEvent event) {
        textfield_cmd.setText(textfield_cmd.getText()+" DL");
    }

    @FXML
    void cmdDRight(MouseEvent event) {
        textfield_cmd.setText(textfield_cmd.getText()+" DR");
    }

    @FXML
    void cmdDown(MouseEvent event) {
        textfield_cmd.setText(textfield_cmd.getText()+" DOWN");
    }

    @FXML
    void cmdLeft(MouseEvent event) {
        textfield_cmd.setText(textfield_cmd.getText()+" LEFT");
    }

    @FXML
    void cmdRight(MouseEvent event) {
        textfield_cmd.setText(textfield_cmd.getText()+" RIGHT");
    }

    @FXML
    void cmdSearch(MouseEvent event) {
        textfield_cmd.setText(textfield_cmd.getText()+" SEARCH");
    }

    @FXML
    void cmdULeft(MouseEvent event) {
        textfield_cmd.setText(textfield_cmd.getText()+" UL");
    }

    @FXML
    void cmdURight(MouseEvent event) {
        textfield_cmd.setText(textfield_cmd.getText()+" UR");
    }

    @FXML
    void cmdUp(MouseEvent event) {
        textfield_cmd.setText(textfield_cmd.getText()+" UP");
    }

    @FXML
    void cmdFire(MouseEvent event) {
        textfield_cmd.setText(textfield_cmd.getText()+" FIRE");
    }

    @FXML
    private void sendCommand (MouseEvent event) {
        String msg = "CMD" + textfield_cmd.getText();
        GameClient.Sendmessage(msg);
        System.out.println("cmd sent successfully.");
    }

    @FXML
    private void disconnect (MouseEvent event) {
        GameClient.Sendmessage("DISCONNECT");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("GameController initialized.");
        game = new Game(pane_screen);
    }

}
