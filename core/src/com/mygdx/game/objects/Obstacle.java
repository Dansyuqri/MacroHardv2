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
    private static int nextID;
    private int id;
    private boolean destroyed = false;
    private boolean toDestroy = false;
    private Stage stage;
    float wallDestroyTime;

    private static final int        FRAME_COLS = 4;
    private static final int        FRAME_ROWS = 3;

    Texture                         wallSheet;
    TextureRegion                   dunWall, iceWall, desWall, playImage;
    TextureRegion[]                 destroyFramesDun, destroyFramesIce, destroyFramesDes;
    Animation                       destroyAnimationDun, destroyAnimationIce, destroyAnimationDes;


    public Obstacle(float x, float y, float width, float height, Stage stage){
        super(x, y, width, height);
        id = nextID - 80;
        nextID = (nextID+1)%200;
        this.stage = stage;
        wallDestroyTime = 0f;

        wallSheet = new Texture(Gdx.files.internal("wall.png"));
        TextureRegion[][] tmp = TextureRegion.split(wallSheet, wallSheet.getWidth()/FRAME_COLS, wallSheet.getHeight()/FRAME_ROWS);
        destroyFramesDun = new TextureRegion[3];
        destroyFramesIce = new TextureRegion[3];
        destroyFramesDes = new TextureRegion[3];
        for(int i = 0; i < FRAME_COLS-1; i++){
            destroyFramesDun[i] = tmp[0][i+1];
            destroyFramesIce[i] = tmp[1][i+1];
            destroyFramesDes[i] = tmp[2][i+1];
        }
        dunWall = tmp[0][0];
        iceWall = tmp[1][0];
        desWall = tmp[2][0];
        destroyAnimationDun = new Animation(0.15f, destroyFramesDun);
        destroyAnimationIce = new Animation(0.15f, destroyFramesIce);
        destroyAnimationDes = new Animation(0.15f, destroyFramesDes);

        if (stage == Stage.DUNGEON || stage == Stage.TRANS_DES_DUN || stage == Stage.TRANS_ICE_DUN) {
//            this.setImage(new Texture(Gdx.files.internal("wall4.1.png")));
            this.setImage(dunWall);
        }
        else if (stage == Stage.ICE || stage == Stage.TRANS_DES_ICE || stage == Stage.TRANS_DUN_ICE){
//            this.setImage(new Texture(Gdx.files.internal("wall4.3.png")));
            this.setImage(iceWall);
        }
        else if (stage == Stage.DESERT || stage == Stage.TRANS_DUN_DES || stage == Stage.TRANS_ICE_DES){
//            this.setImage(new Texture(Gdx.files.internal("wall4.4.png")));
            this.setImage(desWall);
        }
    }

    public static void reset(){
        nextID = 0;
    }

    public int getId() {
        return id;
    }

    public void setImage(TextureRegion image){
        this.playImage = image;
    }

    @Override
    public Texture getImage() {
        return wallSheet;
    }

    @Override
    public void draw(SpriteBatch sb){
        sb.draw(playImage, x, y);
    }

    @Override
    public boolean collides(Player player, PlayState playState) {
        return (player.overlaps(this) && !toDestroy);
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setCurrentFrame(float stateTime, boolean check){
        if (stage == Stage.DUNGEON){
            this.setImage(destroyAnimationDun.getKeyFrame(stateTime, check));
        } else if (stage == Stage.ICE){
            this.setImage(destroyAnimationIce.getKeyFrame(stateTime, check));
        } else if (stage == Stage.DESERT){
            this.setImage(destroyAnimationDes.getKeyFrame(stateTime, check));
        }
    }

    public void setToDestroy(boolean toDestroy) {
        this.toDestroy = toDestroy;
    }

    public boolean isToDestroy() {
        return toDestroy;
    }

    public void setWallDestroyTime(float wallDestroyTime){ this.wallDestroyTime = wallDestroyTime;}

    public float getWallDestroyTime(){ return wallDestroyTime; }
}