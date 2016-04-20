package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Samuel on 20/4/2016.
 */
public class PlayerEffect extends GameObject {
    public PlayerEffect (Player player){
        super(player.x - 5, player.y - 5, 50, 50);
        this.setImage(new Texture(Gdx.files.internal("empty.png")));
    }

    public void set(Player player){
        this.x = player.x - 5;
        this.y = player.y - 5;
        if (player.getCanDestroy() && player.getIsInvicible()) {
            this.setImage(new Texture(Gdx.files.internal("invulBash_effect.png")));
        } else if (!player.getCanDestroy() && player.getIsInvicible()){
            this.setImage(new Texture(Gdx.files.internal("invul_effect.png")));
        } else if (player.getCanDestroy() && !player.getIsInvicible()){
            this.setImage(new Texture(Gdx.files.internal("bash_effect.png")));
        } else {
            this.setImage(new Texture(Gdx.files.internal("empty.png")));
        }
    }
}