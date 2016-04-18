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
public class Switch extends Movable implements Collidable{
    private static int nextID;
    private int id;
    private boolean on = false;

    public Switch(float x, float y, float width, float height, Stage stage){
        super(x, y, width, height);
        id = nextID - 80;
        nextID = (nextID+1)%200;
        this.setImage(new Texture(Gdx.files.internal("pressure_plate1.png")));
    }

    public int getId() {
        return id;
    }

    public static void reset(){
        nextID = 0;
    }

    @Override
    public boolean collides(Player player, PlayState playState) {
        return (player.overlaps(this));
    }

    public void setOn(){
        if (!on) {
            MacroHardv2.actionResolver.sendReliable(new byte[]{MessageCode.OPEN_DOORS, (byte) id});
            this.setImage(new Texture(Gdx.files.internal("pressure_plate1_pressed.png")));
            on = true;
        }
    }

    public void setOff()
    {
        if (on) {
            MacroHardv2.actionResolver.sendReliable(new byte[]{MessageCode.CLOSE_DOORS, (byte) id});
            this.setImage(new Texture(Gdx.files.internal("pressure_plate1.png")));
            on = false;
        }
    }
}