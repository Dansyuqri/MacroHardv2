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
    public Obstacle(float x, float y, float width, float height){
        super(x, y, width, height);
        if (PlayState.stage == Stage.DUNGEON) {
            this.setImage(new Texture(Gdx.files.internal("wall4.1.png")));
        }
        else if (PlayState.stage == Stage.ICE){
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
}