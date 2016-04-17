package com.mygdx.game.states;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.customEnum.StateType;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

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
        loadMusic("MainMenuSound.mp3");
        loadMusic("MenuSelectionClick.wav");
        assetManager.finishLoading();
    }
    public void loadAssets (StateType stateType) {
        switch (stateType) {
            case NON_PLAY:
                removeMusic("WallDestroySound.wav");
                removeMusic("TimeSlowSound.mp3");
                removeMusic("GateSound.wav");
                removeMusic("Dance Of Death.mp3");
                removeMusic("Howling Wind.mp3");
                removeMusic("IceBreak.mp3");
                loadMusic("MainMenuSound.mp3");
                loadMusic("MenuSelectionClick.wav");
                assetManager.finishLoading();
                break;
            case PLAY:
                removeMusic("MainMenuSound.mp3");
                removeMusic("MenuSelectionClick.wav");
                loadMusic("WallDestroySound.wav");
                loadMusic("TimeSlowSound.mp3");
                loadMusic("GateSound.wav");
                loadMusic("Dance Of Death.mp3");
                loadMusic("Howling Wind.mp3");
                loadMusic("IceBreak.mp3");
                assetManager.finishLoading();
                break;
        }
    }
    public void push(State state){
        states.push(state);
    }
    public void pop(){
        states.pop();
    }
    public void set(State state, StateType stateType){
        loadAssets(stateType);
        switch (stateType) {
            case NON_PLAY:
                startMusicLoop("MainMenuSound.mp3",(float) 0.15);
                break;
            case PLAY:
                startMusicLoop("Dance Of Death.mp3",(float) 0.1);
                break;
        }
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

    public void startMusicLoop(String path, float volume) {
        if(assetManager.isLoaded(path)) {
            Music music = assetManager.get(path, Music.class);
            music.setVolume(volume);
            music.play();
            music.setLooping(true);
        }
    }

    public void startMusic(String path,float volume) {
        if(assetManager.isLoaded(path)) {
            Music music = assetManager.get(path, Music.class);
            music.setVolume(volume);
            music.play();
        }
    }

    public void removeMusic(String path) {
        if (assetManager.isLoaded(path)) {
            stopMusic(path);
            assetManager.unload(path);
        }
    }
    public void loadMusic(String path) {
        if (!assetManager.isLoaded(path)) {
            assetManager.load(path, Music.class);
        }
    }

    public void stopMusic(String path){
        Music music = assetManager.get(path, Music.class);
        music.stop();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}
