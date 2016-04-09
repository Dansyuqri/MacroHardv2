package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.customEnum.Stage;
import com.mygdx.game.states.PlayState;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Obstacle extends Movable implements Collidable {
    private boolean destroyed = false;

    public Obstacle(float x, float y, float width, float height, Stage stage){
        super(x, y, width, height);
        if (stage == Stage.DUNGEON) {
            this.setImage(new Texture(Gdx.files.internal("wall4.1.png")));
        }
        else if (stage == Stage.ICE){
            this.setImage(new Texture(Gdx.files.internal("wall4.3.png")));
        }
    }

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