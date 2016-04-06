package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.states.PlayState;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Samuel on 3/4/2016.
 */
public class Hole extends Obstacle implements Collidable {
    private boolean broken = false;
    private boolean breakHole = false;

    public Hole(float x, float y, float width, float height){
        super(x, y, width, height);
        this.setImage(new Texture(Gdx.files.internal("hole1.png")));
    }

    public void setBroken(){
        if (!broken) {
            broken = true;
            this.setImage(new Texture(Gdx.files.internal("hole2.png")));
        }
    }

    @Override
    public boolean collides(Player player, PlayState playState) {
        return player.overlaps(this);
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
}
