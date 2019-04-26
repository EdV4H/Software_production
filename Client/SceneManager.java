import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;

public class SceneManager extends Application {
    enum sceneType {
        Title, Login, Game, GameOver;
    }
    public static sceneType currentScene;

    public static Stage stage;

    private static LoginScene loginScene = null;
    private static GameScene gameScene = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            stage = primaryStage;
            loginScene = new LoginScene();
            gameScene = new GameScene();

            stage.setTitle("SceneManager");
            stage.setScene(loginScene.getScene());
            stage.setResizable(false);

            AnimationTimer timer = new AnimationTimer(){
            
                @Override
                public void handle(long now) {
                    if (Game.currentPhase ==  Game.Phase.Initialize) Game.addPlayer();
                }
            };
            timer.start();

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeScene (sceneType scene) {
        switch (scene) {
            case Title:
                break;
            case Login:
                stage.setScene(loginScene.getScene());
                break;
            case Game:
                stage.setScene(gameScene.getScene());
                break;
            case GameOver:
                break;
        }
        stage.show();
        System.out.println("Scene has changed.");
    }

    public static void connectToServer(String ip, String name) {
        Socket socket = null;
		try {
            //socket = new Socket("localhost", 10000);
            socket = new Socket(ip, 10000);
		} catch (UnknownHostException e) {
			System.err.println("UnknownHostException: " + e);
		} catch (IOException e) {
			System.err.println("IOException: " + e);
		}

		GameClient mrt = new GameClient(socket, name);
		mrt.start();
    }

    public static void main (String args[]) {
        launch(args);
    }

}