package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.customEnum.Stage;
import com.mygdx.game.states.PlayState;
import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Obstacle extends Movable implements Collidable {
    private boolean destroyed = false;

    private static final int        FRAME_COLS = 4;
    private static final int        FRAME_ROWS = 3;

    Texture                         wallSheet;
    TextureRegion                   dunWall, iceWall, desWall, playImage;
    TextureRegion[]                 destroyFramesDun, destroyFramesIce, destroyFramesDes;
    Animation                       destroyAnimationDun, destroyAnimationIce, destroyAnimationDes;


    public Obstacle(float x, float y, float width, float height, Stage stage){
        super(x, y, width, height);
        wallSheet = new Texture(Gdx.files.internal("wall.png"));
        TextureRegion[][] tmp = TextureRegion.split(wallSheet, wallSheet.getWidth()/FRAME_COLS, wallSheet.getHeight()/FRAME_ROWS);
        destroyFramesDun = new TextureRegion[3];
        destroyFramesIce = new TextureRegion[3];
        destroyFramesDes = new TextureRegion[3];
        for(int i = 0; i < FRAME_COLS-1; i++){
            destroyFramesDun[i] = tmp[0][i];
            destroyFramesIce[i] = tmp[1][i];
            destroyFramesDes[i] = tmp[2][i];
        }
        dunWall = tmp[0][0];
        iceWall = tmp[1][0];
        desWall = tmp[2][0];
        destroyAnimationDun = new Animation(0.25f, destroyFramesDun);
        destroyAnimationIce = new Animation(0.25f, destroyFramesIce);
        destroyAnimationDes = new Animation(0.25f, destroyFramesDes);

        if (stage == Stage.DUNGEON) {
            this.setImage(new Texture(Gdx.files.internal("wall4.1.png")));
        }
        else if (stage == Stage.ICE){
            this.setImage(new Texture(Gdx.files.internal("wall4.3.png")));
        }
        else if (stage == Stage.DESERT){
            this.setImage(new Texture(Gdx.files.internal("wall4.4.png")));
        }
    }

//    public void setImage(TextureRegion image){
//        this.playImage = image;
//    }
//
//    @Override
//    public Texture getImage() {
//        return wallSheet;
//    }
//
//    @Override
//    public void draw(SpriteBatch sb){
//        sb.draw(playImage, x, y);
//    }

    @Override
    public boolean collides(Player player, PlayState playState) {
        if (player.overlaps(this)) {
            return true;
        }
        else {
            return false;
        }
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}