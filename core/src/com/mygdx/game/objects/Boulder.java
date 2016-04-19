package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.customEnum.Stage;
import com.mygdx.game.states.PlayState;

/**
 * Created by Samuel on 14/4/2016.
 */
public class Boulder extends Movable implements Collidable {
    private static int nextID;
    private int id;
    private boolean destroyed = false;
    private boolean toDestroy = false;
    private Stage stage;
    float boulderDestroyTime;

    private static final int        FRAME_COLS = 4;
    private static final int        FRAME_ROWS = 1;

    Texture                         boulderSheet;
    TextureRegion                   boulder, playImage;
    TextureRegion[]                 destroyFrames;
    Animation                       destroyAnimation;


    public Boulder(float x, float y, float width, float height, Stage stage){
        super(x, y, width, height);
        id = nextID - 80;
        nextID = (nextID+1)%200;
        this.stage = stage;
        boulderDestroyTime = 0f;

        boulderSheet = new Texture(Gdx.files.internal("boulder.png"));
        TextureRegion[][] tmp = TextureRegion.split(boulderSheet, boulderSheet.getWidth()/FRAME_COLS, boulderSheet.getHeight()/FRAME_ROWS);
        destroyFrames = new TextureRegion[3];
        for(int i = 0; i < FRAME_COLS-1; i++){
            destroyFrames[i] = tmp[0][i+1];
        }
        boulder = tmp[0][0];
        destroyAnimation = new Animation(0.15f, destroyFrames);

        this.setImage(boulder);

    }

    public int getId() {
        return id;
    }

    public static void reset(){
        nextID = 0;
    }

    public void setImage(TextureRegion image){
        this.playImage = image;
    }

    @Override
    public Texture getImage() {
        return boulderSheet;
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
        this.setImage(destroyAnimation.getKeyFrame(stateTime, check));
    }

    public void setToDestroy(boolean toDestroy) {
        this.toDestroy = toDestroy;
    }

    public boolean isToDestroy() {
        return toDestroy;
    }

    public void setBoulderDestroyTime(float wallDestroyTime){ this.boulderDestroyTime = wallDestroyTime;}

    public float getBoulderDestroyTime(){ return boulderDestroyTime; }
}