package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.states.PlayState;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Switch extends Obstacle implements Collidable{
    private boolean on = false;

    public Switch(float x, float y, float width, float height){
        super(x, y, width, height);
        this.setImage(new Texture(Gdx.files.internal("switch_off.png")));;
    }

    @Override
    public boolean collides(Player player, PlayState playState) {
        return (player.overlaps(this));
    }

    public void setOn(){
        if (!on) {
            this.setImage(new Texture(Gdx.files.internal("switch_on.png")));
            on = true;
        }
    }

    public void setOff()
    {
        if (on) {
            this.setImage(new Texture(Gdx.files.internal("switch_off.png")));
            on = false;
        }
    }
}