package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.customEnum.Stage;
import com.mygdx.game.states.PlayState;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Samuel on 3/4/2016.
 */
public class Hole extends Movable implements Collidable {
    private static int nextID = 0;
    private int id;
    private boolean broken = false;
    private boolean breakHole = false;
    public static final int HOLE_BREAK_TIME = 3;
    float holeDestroyTime;

    private static final int        FRAME_COLS = 4;
    private static final int        FRAME_ROWS = 1;

    Texture                         holeSheet;
    TextureRegion                   holeIn, holeBrkn, playImage;
    TextureRegion[]                 holeBreakingFrames;
    Animation                       holeBreakingAnimation;

    public Hole(float x, float y, float width, float height, Stage stage){
        super(x, y, width, height);
        id = nextID;
        nextID = (nextID+1)%127;

        holeDestroyTime = 0f;

        holeSheet = new Texture(Gdx.files.internal("hole.png"));
        TextureRegion[][] tmp = TextureRegion.split(holeSheet, holeSheet.getWidth()/FRAME_COLS, holeSheet.getHeight()/FRAME_ROWS);
        holeBreakingFrames = new TextureRegion[3];
        for(int i = 0; i < FRAME_COLS-1; i++){
            holeBreakingFrames[i] = tmp[0][i];
        }
        holeIn = tmp[0][0];
        holeBrkn = tmp[0][3];
        holeBreakingAnimation = new Animation(1.0f, holeBreakingFrames);

        if (!this.broken){
            this.setImage(holeIn);
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
        return holeSheet;
    }

    @Override
    public void draw(SpriteBatch sb){
        sb.draw(playImage, x, y);
    }

    public void setBroken(){
        if (!broken) {
            broken = true;
            this.setImage(holeBrkn);
        }
    }

    @Override
    public boolean collides(Player player, PlayState playState) {
        return player.overlaps(this);
    }

    public void setCurrentFrame(float stateTime, boolean check){
        this.setImage(holeBreakingAnimation.getKeyFrame(stateTime, check));
    }

    public boolean isBroken() {
        return broken;
    }

    public void setBreakHole(boolean breakHole) {
        this.breakHole = breakHole;
    }

    public boolean isBreakHole() {
        return breakHole;
    }

    public void setHoleDestroyTime(float holeDestroyTime){ this.holeDestroyTime = holeDestroyTime;}

    public float getHoleDestroyTime(){ return holeDestroyTime; }
}