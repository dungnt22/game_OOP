package uet.oop.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.stage.WindowEvent;
import uet.oop.bomberman.GameControl.Player;
import uet.oop.bomberman.Level.Map;
import uet.oop.bomberman.entities.*;
import uet.oop.bomberman.graphics.Sprite;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class BombermanGame extends Application {
    
    public static final int WIDTH = 31;
    public static final int HEIGHT = 14;

    private Map map;
    private GraphicsContext gc;
    private GraphicsContext gc1;
    private Canvas canvas;
    private Canvas canvas1;
    private List<Entity> entities = new ArrayList<>();
    private List<Entity> entities1 = new ArrayList<>();
    private List<Entity> stillObjects = new ArrayList<>();
    private List<Entity> stillObjects1 = new ArrayList<>();

    private String Level;
    private Player player = new Player();
    private Font font;
    private Text Time = new Text();
    private Text Score = new Text();


    public static void main(String[] args) {
        Application.launch(BombermanGame.class);
    }

    @Override
    public void start(Stage stage) {

        Level = "Level1.txt";

        map = new Map(Level);
        map.insertFromFile(Level);
        createMap();

        String musicFile = "bomb_explosion.wav";
        Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
        
        // Tao Canvas
        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc = canvas.getGraphicsContext2D();

        canvas1 = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc1 = canvas1.getGraphicsContext2D();

        stage.setTitle("Bomberman UET");

        // Tao root container
        Group root = new Group();
        root.getChildren().addAll(canvas, canvas1);

        font = Font.font("Consolas", FontWeight.BOLD, 18);

        Time.setX(150);
        Time.setY(20);
        Time.setFill(Color.WHITE);
        Time.setFont(font);

        Score.setText("Score: 0");
        Score.setX(700);
        Score.setY(20);
        Score.setFill(Color.WHITE);
        Score.setFont(font);



        Thread time = new Thread() {
            public void run() {
                for (int i = 200; i >= 0; i--) {
                    Time.setText("Time: " + i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        time.start();

        root.getChildren().addAll(Time, Score);

        // Tao scene
        Scene scene = new Scene(root);


        gc.fillRect(0, 0, Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE);

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case UP:
                        player.UpPressed = true;
                        break;
                    case DOWN:
                        player.DownPressed = true;
                        break;
                    case LEFT:
                        player.LeftPressed = true;
                        break;
                    case RIGHT:
                        player.RightPressed = true;
                        break;
                    case SPACE:
                        Bomber bomber = new Bomber((int) player.x / Sprite.SCALED_SIZE,(int) player.y / Sprite.SCALED_SIZE, Sprite.bomb.getFxImage());
                        stillObjects1.add(bomber);
                        stillObjects1.remove(player);
                        stillObjects1.add(player);
                        break;
                }
                render1();
                update1();
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case UP:
                        player.UpPressed = false;
                        break;
                    case DOWN:
                        player.DownPressed = false;
                        break;
                    case LEFT:
                        player.LeftPressed = false;
                        break;
                    case RIGHT:
                        player.RightPressed = false;
                        break;
                }
                render1();
                update1();
            }
        });

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Platform.exit();
                System.exit(0);
            }
        });

        // Them scene vao stage
        stage.setScene(scene);
        stage.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                render1();
                update1();
            }
        };
        timer.start();
        render();

    }

    public void createMap() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 1; j < HEIGHT; j++) {
                Entity object;
                if (j == 1 || j == HEIGHT - 1 || i == 0 || i == WIDTH - 1) {
                    object = new Wall(i, j, Sprite.wall.getFxImage());
                }
                else {
                    object = new Grass(i, j, Sprite.grass.getFxImage());
                }
                stillObjects.add(object);
            }
        }
        for (int i = 0; i < map.HEIGHT; i++) {
            for (int j = 0; j < map.map.get(i).length(); j++) {
                Entity object;
                if (map.map.get(i).charAt(j) == '#') {
                    object = new Wall(j, i + 1, Sprite.wall.getFxImage());
                    stillObjects.add(object);
                } else if (map.map.get(i).charAt(j) == 'f') {
                    object = new flameItem(j, i + 1, Sprite.powerup_flames.getFxImage());
                    stillObjects.add(object);
                } else if (map.map.get(i).charAt(j) == 'b') {
                    object = new bombItem(j, i + 1, Sprite.powerup_bombs.getFxImage());
                    stillObjects.add(object);
                } else if (map.map.get(i).charAt(j) == 's') {
                    object = new speedItem(j, i + 1, Sprite.powerup_speed.getFxImage());
                    stillObjects.add(object);
                } else if (map.map.get(i).charAt(j) == 'x') {
                    object = new Portal(j, i + 1, Sprite.portal.getFxImage());
                    stillObjects.add(object);
                } else if (map.map.get(i).charAt(j) == '*') {
                    object = new Brick(j, i + 1, Sprite.brick.getFxImage());
                    stillObjects1.add(object);
                } else if (map.map.get(i).charAt(j) == '1') {
                    object = new Balloon(j, i + 1, Sprite.balloom_right1.getFxImage());
                    stillObjects1.add(object);
                } else if (map.map.get(i).charAt(j) == '2') {
                    object = new Oneal(j, i + 1, Sprite.oneal_right1.getFxImage());
                    stillObjects1.add(object);
                } else if (map.map.get(i).charAt(j) == 'p') {
                    player = new Player(j, i + 1, Sprite.player_down.getFxImage());
                    stillObjects1.add(player);
                } else {
                    object = new Grass(j, i + 1, Sprite.grass.getFxImage());
                    stillObjects.add(object);
                }
                if (map.map.get(i).charAt(j) == 'x' || map.map.get(i).charAt(j) == 's' || map.map.get(i).charAt(j) == 'f' || map.map.get(i).charAt(j) == 'b') {
                    object = new Brick(j, i + 1, Sprite.brick.getFxImage());
                    stillObjects1.add(object);
                }
            }
        }
    }

    public void update() {
        entities.forEach(Entity::update);
    }

    public void update1() {
        player.update();
        entities1.forEach(Entity::update);
    }

    public void render() {
        //gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        stillObjects.forEach(g -> g.render(gc));
        entities.forEach(g -> g.render(gc));
    }

    public void render1() {
        gc1.clearRect(0, 0, canvas1.getWidth(), canvas1.getHeight());
        stillObjects1.forEach(g -> g.render(gc1));
        entities1.forEach(g -> g.render(gc1));
    }
}
