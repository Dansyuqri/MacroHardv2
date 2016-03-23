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

    public Switch(int spriteWidth,int spriteHeight,float x, float y){
        super();
        this.setImage(new Texture(Gdx.files.internal("switch_off.png")));
        this.x = 15 + x*spriteWidth;
        this.y = PlayState.tracker - (y*spriteHeight);
        this.width = spriteWidth;
        this.height = spriteHeight;
    }

    @Override
    public boolean collide(Player player, PlayState playState) {
        if (player.overlaps(this) && !on) {
            this.setImage(new Texture(Gdx.files.internal("switch_on.png")));
            on = true;
            for (GameObject gameObj: playState.getGameObjects()){
                if (gameObj instanceof Door){
                    ((Door) gameObj).setOpen();
                }
            }
        }
        return false;
    }
}