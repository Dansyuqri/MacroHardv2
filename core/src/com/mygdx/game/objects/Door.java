package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.states.PlayState;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Door extends Obstacle implements Collidable{
    private boolean open = false;

    public Door(float x, float y, float width, float height){
        super(x, y, width, height);
        this.setImage(new Texture(Gdx.files.internal("gate_closed.png")));
    }

    public void setOpen(){
        if (!open) {
            open = true;
            this.setImage(new Texture(Gdx.files.internal("gate_open.png")));
        }
    }

    public void setClose(){
        if (open) {
            open = false;
            this.setImage(new Texture(Gdx.files.internal("gate_closed.png")));
        }
    }

    @Override
    public boolean collides(Player player, PlayState playState) {
        if (player.overlaps(this) && !open) {
            return true;
        }
        else {
            return false;
        }
    }
}