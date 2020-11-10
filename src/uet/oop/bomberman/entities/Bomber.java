package uet.oop.bomberman.entities;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import uet.oop.bomberman.AnimatedEntity;
import uet.oop.bomberman.graphics.Sprite;

public class Bomber extends AnimatedEntity {
    private int timeToExplode = 120;
    private int timeToDisappear = 20;
    private boolean exploded = false;
    private Sprite sprite;
    public Bomber(int x, int y, Image img) {
        super( x, y, img);
    }

    @Override
    public void update() {
        if (timeToExplode > 0) {
            timeToExplode--;
            sprite = Sprite.movingSprite(Sprite.bomb, Sprite.bomb_1, Sprite.bomb_2, animate, 20);
            img = sprite.getFxImage();
        } else {

            //animate();
        }
        animate();
    }


}
