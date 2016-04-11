package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.MacroHardv2;
import com.mygdx.game.customEnum.MessageCode;
import com.mygdx.game.customEnum.Stage;
import com.mygdx.game.states.PlayState;

/**
 * Created by Syuqri on 3/9/2016.
 */
public class Switch extends Obstacle implements Collidable{
    private boolean on = false;

    public Switch(float x, float y, float width, float height, Stage stage){
        super(x, y, width, height, stage);
        this.setImage(new Texture(Gdx.files.internal("pressure_plate1.png")));
    }

    @Override
    public boolean collides(Player player, PlayState playState) {
        return (player.overlaps(this));
    }

    public void setOn(){
        if (!on) {
            MacroHardv2.actionResolver.sendReliable(new byte[]{MessageCode.OPEN_DOORS});
            this.setImage(new Texture(Gdx.files.internal("pressure_plate1_pressed.png")));
            on = true;
        }
    }

    public void setOff()
    {
        if (on) {
            this.setImage(new Texture(Gdx.files.internal("pressure_plate1.png")));
            on = false;
        }
    }
}