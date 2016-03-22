package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Samuel on 22/3/2016.
 */
public class UI extends GameObject{
    public UI(int y){
        super();
        this.setImage(new Texture(Gdx.files.internal("UI.png")));
        this.x = 0;
        this.y = y;
        this.width = 480;
        this.height = 800;
    }
}
