package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.customEnum.Stage;
import com.mygdx.game.states.PlayState;

/**
 * Created by Syuqri on 3/16/2016.
 */
public class Background extends Movable{
    public Background(float y, Stage stage){
        super(0, y, 480, 200);
        if (stage == Stage.DUNGEON) {
            this.setImage(new Texture(Gdx.files.internal("bg.png")));
        }
        else if (stage == Stage.ICE){
            this.setImage(new Texture(Gdx.files.internal("bg_ice.png")));
        }
    }
}
