package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.customEnum.Stage;

/**
 * Created by Samuel on 21/3/2016.
 */
public class Overlay extends Movable{
    public Overlay(float y, Stage stage){
        super(0, y, 480, 200);
        if (stage == Stage.DUNGEON) {
            this.setImage(new Texture(Gdx.files.internal("effects1.png")));
        }
        else if (stage == Stage.ICE){
            this.setImage(new Texture(Gdx.files.internal("effects2.png")));
        }
        else if (stage == Stage.DESERT){
            this.setImage(new Texture(Gdx.files.internal("effects3.png")));
        }
        else if (stage == Stage.TRANS_DUN_ICE){
            this.setImage(new Texture(Gdx.files.internal("effects_trans1.png")));
        }
        else if (stage == Stage.TRANS_DUN_DES){
            this.setImage(new Texture(Gdx.files.internal("effects_trans2.png")));
        }
        else if (stage == Stage.TRANS_ICE_DUN){
            this.setImage(new Texture(Gdx.files.internal("effects_trans3.png")));
        }
        else if (stage == Stage.TRANS_ICE_DES){
            this.setImage(new Texture(Gdx.files.internal("effects_trans4.png")));
        }
        else if (stage == Stage.TRANS_DES_DUN){
            this.setImage(new Texture(Gdx.files.internal("effects_trans5.png")));
        }
        else if (stage == Stage.TRANS_DES_ICE){
            this.setImage(new Texture(Gdx.files.internal("effects_trans6.png")));
        }
    }
}
