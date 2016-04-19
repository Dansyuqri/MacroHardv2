package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.states.PlayState;

/**
 * Created by Samuel on 11/4/2016.
 */
public class Sand extends Movable implements Collidable {
    public Sand(float x, float y, float width, float height){
        super(x, y, width, height);
        this.setImage(new Texture(Gdx.files.internal("quicksand.png")));
    }

    @Override
    public boolean collides(Player player, PlayState playState) {
        return player.overlaps(this);
    }
}