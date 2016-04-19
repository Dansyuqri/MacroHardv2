package com.mygdx.game.objects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.customEnum.Stage;

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
        else if (stage == Stage.DESERT){
            this.setImage(new Texture(Gdx.files.internal("bg_desert.png")));
        }
        else if (stage == Stage.TRANS_DUN_ICE){
            this.setImage(new Texture(Gdx.files.internal("bg_trans1.png")));
        }
        else if (stage == Stage.TRANS_DUN_DES){
            this.setImage(new Texture(Gdx.files.internal("bg_trans2.png")));
        }
        else if (stage == Stage.TRANS_ICE_DUN){
            this.setImage(new Texture(Gdx.files.internal("bg_trans3.png")));
        }
        else if (stage == Stage.TRANS_ICE_DES){
            this.setImage(new Texture(Gdx.files.internal("bg_trans4.png")));
        }
        else if (stage == Stage.TRANS_DES_DUN){
            this.setImage(new Texture(Gdx.files.internal("bg_trans5.png")));
        }
        else if (stage == Stage.TRANS_DES_ICE){
            this.setImage(new Texture(Gdx.files.internal("bg_trans6.png")));
        }
    }
}
