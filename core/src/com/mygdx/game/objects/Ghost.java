package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.states.PlayState;

/**
 * Created by Samuel on 30/3/2016.
 */
public class Ghost extends Movable implements Collidable {
    private boolean right = false;

    public Ghost(float x, float y, float width, float height){
        super(x, y, width, height);
        this.setImage(new Texture(Gdx.files.internal("ghost.png")));
    }

    @Override
    public boolean collides(Player player, PlayState playState) {
        return player.overlaps(this);
    }

    @Override
    public void scroll (float gameSpeed){
        y -= gameSpeed * Gdx.graphics.getDeltaTime();
        if (!right){
            x -= 200 * Gdx.graphics.getDeltaTime();
            if (x <= 15){
                right = true;
                this.setImage(new Texture(Gdx.files.internal("ghost2.png")));
            }
        }
        else {
            x += 200 * Gdx.graphics.getDeltaTime();
            if (x >= 415){
                right = false;
                this.setImage(new Texture(Gdx.files.internal("ghost.png")));
            }
        }
    }
}
