package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by hj on 19/4/16.
 */
public class Pointer extends GameObject {
    public Pointer (int player){
        super(220, 510, 40, 20);
        switch (player){
            case 0:
                this.setImage(new Texture(Gdx.files.internal("pointer_1.png")));
                break;
            case 1:
                this.setImage(new Texture(Gdx.files.internal("pointer_2.png")));
                break;
        }
    }

    public void set(Player player){
        this.x = player.x + 10;
        this.y = player.y + 60;
    }
}
