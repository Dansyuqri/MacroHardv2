package com.mygdx.game.objects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.states.PlayState;

/**
 * Created by Samuel on 26/3/2016.
 */
public class Spikes extends Movable implements Collidable {
    public Spikes(float x, float y, float width, float height){
        super(x, y, width, height);
        //change to spikes
        this.setImage(new Texture(Gdx.files.internal("spikes.png")));
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