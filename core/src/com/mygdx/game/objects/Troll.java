package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.customEnum.*;
import com.mygdx.game.states.PlayState;

import java.util.ArrayList;

/**
 * Created by Samuel on 19/4/2016.
 */
public class Troll extends Movable implements Collidable {
    private static final int        FRAME_COLS = 4;
    private static final int        FRAME_ROWS = 1;

    Texture                         trollSheet;
    TextureRegion[]                 trollWalkFrames;
    Animation                       trollWalkAnimation;
    TextureRegion                   playImage;

    public Troll(float x, float y, float width, float height) {
        super(x, y, width, height);
        trollSheet = new Texture(Gdx.files.internal("ice_troll.png"));
        TextureRegion[][] tmp = TextureRegion.split(trollSheet, trollSheet.getWidth()/FRAME_COLS, trollSheet.getHeight()/FRAME_ROWS);              // #10
        trollWalkFrames = new TextureRegion[4];


        for(int i = 0; i < FRAME_COLS; i++){
            trollWalkFrames[i] = tmp[0][i];
        }
        this.setImage(tmp[0][0]);

        trollWalkAnimation = new Animation(0.2f, trollWalkFrames);
    }

    @Override
    public boolean collides(Player player, PlayState playState) {
        return player.overlaps(this);
    }

    public boolean collides(ArrayList<GameObject> obstacles, PlayState playState) {
        for (GameObject obstacle: obstacles) {
            if (obstacle.overlaps(this)){
                ((Obstacle)obstacle).setToDestroy(true);
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(SpriteBatch sb){
        sb.draw(playImage, x, y);
    }

    @Override
    public Texture getImage() {
        return trollSheet;
    }

    public void setImage(TextureRegion image){
        this.playImage = image;
    }

    public void setCurrentFrame(float stateTime, boolean check) {
        this.setImage(trollWalkAnimation.getKeyFrame(stateTime, check));
    }

    @Override
    public void scroll (float gameSpeed) {
        y -= 200 * PlayState.deltaCap;
    }
}
