package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Interface.Collidable;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Obstacle extends Movable implements Collidable {
    public Obstacle(){
        super();
        this.setImage(new Texture(Gdx.files.internal("wall4.1.png")));
    }

    @Override
    public boolean collide(Player player) {
        if (player.overlaps(this)) {
            return true;
        }
        else {
            return false;
        }
    }
}