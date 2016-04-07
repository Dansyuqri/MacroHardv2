package com.mygdx.game.states;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

/**
 * Created by Syuqri on 3/7/2016.
 */

public class GameStateManager {
    private AssetManager assetManager;
    private Stack<State> states;

    public GameStateManager(){
        states = new Stack<State>();
        assetManager = new AssetManager();
        assetManager.load("MainMenuSound.mp3", Music.class);
        assetManager.load("WallDestroySound.wav", Music.class);
        assetManager.load("TimeSlowSound.mp3", Music.class);
        assetManager.load("MenuSelectionClick.wav", Music.class);
        assetManager.load("GateSound.wav", Music.class);
        assetManager.finishLoading();
    }

    public void push(State state){
        states.push(state);
    }
    public void pop(){
        states.pop();
    }
    public void set(State state){
        states.pop();
        states.push(state);
    }

    public void update(byte[] message){
        states.peek().update(message);
    }

    public void render(SpriteBatch sb){
        states.peek().render(sb);
    }

    public State peek(){
        return states.peek();
    }

    public void startMusicLoop(String path) {
        if(assetManager.isLoaded(path)) {
            Music music = assetManager.get(path, Music.class);
            music.setVolume((float)0.5);
            music.play();
            music.setLooping(true);
        }
    }

    public void startMusic(String path) {
        if(assetManager.isLoaded(path)) {
            Music music = assetManager.get(path, Music.class);
            music.setVolume(1);
            music.play();
        }
    }


    public void disposeMusic(String path){
        this.assetManager.unload(path);
    }

}
