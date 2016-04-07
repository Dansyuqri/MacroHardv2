package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Syuqri on 3/9/2016.
 */
public abstract class GameObject extends Rectangle {
    private Texture image;
    public Texture getImage() {
        return image;
    }
    public void setImage(Texture image) {
        this.image = image;
    }
    GameObject(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    GameObject(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(SpriteBatch sb){
        sb.draw(image, x, y);
    }
}
