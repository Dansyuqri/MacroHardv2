package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.states.PlayState;

/**
 * Created by Samuel on 3/4/2016.
 */
public class Hole extends Obstacle implements Collidable {
    private boolean broken = false;
    public long brokenTime = 0;

    public Hole(float x, float y, float width, float height){
        super(x, y, width, height);
        this.setImage(new Texture(Gdx.files.internal("hole1.png")));
    }

    public void setBroken(){
        broken = true;
        this.setImage(new Texture(Gdx.files.internal("hole2.png")));
    }

    @Override
    public boolean collides(Player player, PlayState playState) {
        if (player.overlaps(this) && broken) {
            return true;
        }
        else if (player.overlaps(this) && !broken){
            return false;
        }
        else {
            return false;
        }
    }
}
