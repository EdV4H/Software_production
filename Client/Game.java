import javafx.scene.layout.Pane;

import org.omg.PortableInterceptor.HOLDING;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Game extends Task {
    enum Phase {
        Connection, Initialize, Custom, Action;
    }
    public static Phase currentPhase;

    private static Thread thread;

    private static Pane screen;
    public static double screenWidth, screenHeight;
    private static ProgressBar hpBar;
    private static Label nameLabel;

    private static Character[] player;
    private static int playerNum;
    private static int myServerNumber;

    private static boolean isGameOver;

    public Game (Pane root) {
        screen = root;
        screenWidth = 450; screenHeight = 450;

        hpBar = new ProgressBar();
        hpBar.setTranslateX(10); hpBar.setTranslateY(420);
        hpBar.setProgress(1);
        screen.getChildren().add(hpBar);

        nameLabel = new Label();
        nameLabel.setTranslateX(10); nameLabel.setTranslateY(400);
        screen.getChildren().add(nameLabel);

        currentPhase = Phase.Connection;
        isGameOver = false;
        thread = new Thread(this);
    }

    @Override
    protected Object call() throws Exception {
        while (!isGameOver) {
            switch (currentPhase) {
                case Initialize:
                    System.out.println("Initializing...");
                    try { Thread.sleep(1000);} catch (Exception e) { e.printStackTrace();}  
                    break;
                case Custom:
                    System.out.println("Customizing...");
                    try { Thread.sleep(1000);} catch (Exception e) { e.printStackTrace();}
                    break;
                case Action:
                    for (int i=0; i<playerNum; i++) {player[i].action();}
                    try { Thread.sleep(100000);} catch (Exception e) { e.printStackTrace();}
                    for (int i=0; i<playerNum; i++) {player[i].stop();}
                    break;
                default:
                    isGameOver = true;
                    break;
            }
        }
        return null;
    }

    public static void addPlayer () {
        for (int i = 0; i < playerNum; i++) {
            System.out.println("adding player...");
            screen.getChildren().add(player[i]);
            for (int j = 0; j < Character.getBulletNum(); j++) screen.getChildren().add(player[i].bullets[j]);
        }
        currentPhase = Phase.Custom;
    }

    public static void receiveCmd (String inputLine) {
        if (inputLine != null) {
            String[] inputTokens = inputLine.split(" ");
            String cmd = inputTokens[0];
            switch (cmd) {
                case "START":
                    int member = Integer.parseInt(inputTokens[1]);
                    playerNum = member;
                    player = new Character[playerNum];
                    for (int i = 0; i < playerNum; i++) {
                        String name = inputTokens[2+4*i];
                        int x = Integer.parseInt(inputTokens[2+4*i+2]);
                        int y = Integer.parseInt(inputTokens[2+4*i+3]);
                        
                        if (name.equals(GameClient.getMyName())) {
                            player[i] = new Character("player", i, x, y, 100, 50);
                            myServerNumber = i;
                            nameLabel.setText(GameClient.getMyName());
                        } else player[i] = new Character("enemy", i, x, y, 100, 50);
                    }

                    thread.setDaemon(true);
                    thread.start();
                    currentPhase = Phase.Initialize;
                    break;
                case "CMD":
                    int n = Integer.parseInt(inputTokens[inputTokens.length-1]);
                    player[n].inputCmd(inputLine);
                    break;
                case "ACTION":
                    currentPhase = Phase.Action;
                    break;
            }
        }
    }

    private static class Character extends ImageView{
        private AnimationTimer animationTimer;
        private Timeline cmdTimer;

        private final int id, max_hp;
        private int hp;
        private double x, y, px, py, r, speed;
        private final String name;

        private String cmd[];
        private int cmdNum;

        public Bullet bullets[];
        private static int bulletNum;

        Character (String name, int id, double x, double y, int hp, double speed) {
            super();
            this.name = name;
            this.r = 10;
            Image image = new Image("src/"+name+".png", r*2, 0, true, false);
            this.setImage(image);
            this.id = id;
            max_hp = hp;
            this.hp = hp;
            this.x = x; this.y = y;
            this.px = x; this.py = y;
            this.setRotate(0);
            this.speed = speed;
            this.cmdNum = 0;
            this.bulletNum = 10;

            cmdTimer = new Timeline(new KeyFrame(Duration.millis(100000/speed), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    exec();
                }
            }));
            cmdTimer.setCycleCount(Timeline.INDEFINITE);

            animationTimer = new AnimationTimer(){          
                @Override
                public void handle(long now) {
                    update();
                    draw();
                }
            };
            draw();
            System.out.println("character created.");

            bullets = new Bullet[bulletNum];
            for (int i = 0; i < bulletNum; i++) bullets[i] = new Bullet("enemy", name);
        }

        public static int getBulletNum () {return bulletNum;}

        public double getPositionX () {return x;}
        public double getPositionY () {return y;}

        void update () {
            //System.out.println("update() is called.");
            if (x < px) x += 0.1; else if (x > px)  x -= 0.1;
            if (y < py) y += 0.1; else if (y > py)  y -= 0.1;
            //this.setRotate(this.getRotate()+2);
        }

        void draw () { this.setX(x-r); this.setY(y-r); }

        void exec () {
            String command = cmd[cmdNum];
            System.out.println("void exec() is running... and cmd is " + command);
            cmdNum++;
            switch (command) {
                case "UP":
                    move(30, this.getRotate());
                    break;
                case "DOWN":
                    move(30, this.getRotate()+180d);
                    break;
                case "RIGHT":
                    move(30, this.getRotate()+90d);
                    break;
                case "LEFT":
                    move(30, this.getRotate()-90d);
                    break;
                case "UL":
                    move(30, this.getRotate()-45d);
                    break;
                case "UR":
                    move(30, this.getRotate()+45d);
                    break;
                case "DL":
                    move(30, this.getRotate()-135d);
                    break;
                case "DR":
                    move(30, this.getRotate()+135d);
                    break;
                case "SEARCH":
                    search(100);
                    break;
                case "FIRE":
                    fire(this.getRotate());
                    break;
                default:
                    cmdNum = 0;
                    break;
            }
            if (cmdNum >= cmd.length) cmdNum = 0;
        }

        void inputCmd (String inputLine) {
            String inputTokens[] = inputLine.split(" ");
            cmd = new String[inputTokens.length-2];
            for (int i = 1; i < inputTokens.length-1; i++) cmd[i-1] = inputTokens[i];
        }

        void move (double range, double digree) {
            System.out.println("move " + range + " " + digree);
            double vertical = -range * Math.cos((digree)*Math.PI/180d);
            double horizontal = range * Math.sin((digree)*Math.PI/180d);
            px += horizontal; py += vertical;
            if (px < r) px = r; else if (px > screenWidth-r) px = screenWidth - r;
            if (py < r) py = r; else if (py > screenHeight-r) py = screenHeight - r;
        }

        void search (double range) {
            for (int i = 0; i < playerNum; i++) {
                if (id == i) continue;
                this.setRotate(getDigree(x, y, player[i].getPositionX(), player[i].getPositionY()));
                System.out.println("getRatate() = " + this.getRotate());
            }
        }

        void fire (double rot) {
            for (int i = 0; i < bulletNum; i++) {
                if (bullets[i].isActive) continue;
                bullets[i].firing(x, y, rot, 1, 100);
                break;
            }
        }

        void stop () {
            cmdTimer.stop();
            animationTimer.stop();
        }
        void action () {
            animationTimer.start();
            cmdTimer.play();
        }

        protected double getDigree(double x, double y, double x2, double y2) {
            double radian = Math.atan2(y2 - y,x2 - x);
            double digree = radian * 180d / Math.PI;
            digree += 90;
            return digree;
        }
    }    

    private static class Bullet extends ImageView{
        private AnimationTimer timer;

        private final String who;
        private double x, y, r, spd;
        private int dmg;
        private boolean isActive;

        public Bullet (String img, String who) {
            super();
            r = 3;
            Image image = new Image("src/"+img+".png", r*2, 0, true, false);
            this.setImage(image);
            this.who = who;
            isActive = false;
            this.setVisible(false);
            x = 0; y = 0; this.setX(0); this.setY(0); this.setRotate(0); 

            timer = new AnimationTimer(){
            
                @Override
                public void handle(long now) {
                    update();
                    draw();
                }
            };
            System.out.println("Bullet is created.");
        }

        public void firing (double x, double y, double rot, double spd, int dmg) {
            this.x = x; this.y = y; this.setRotate(rot); 
            this.spd = spd;
            this.dmg = dmg;
            this.isActive = true;
            this.setVisible(true);
            timer.start();
            System.out.println("Firing at " + x + " " + y + " " + rot + ".");
        }

        void update () {
            if (!isActive) return;
            double vertical = -spd * Math.cos(this.getRotate() * Math.PI / 180d);
            double horizontal = spd * Math.sin(this.getRotate() * Math.PI / 180d);
            x += horizontal; y += vertical;
            
            if (x < 0 || x > screenWidth || y < 0 || y > screenHeight) {
                this.isActive = false;
                this.setVisible(false);
                timer.stop();
            }      
        }

        void draw () { this.setX(x-r); this.setY(y-r); }
    }
}