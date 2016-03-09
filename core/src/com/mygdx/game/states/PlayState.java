package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.objects.Barrier;
import com.mygdx.game.objects.Obstacle;
import com.mygdx.game.objects.Power;
import com.mygdx.game.objects.SideWall;
import com.mygdx.game.objects.Switch;
import com.mygdx.game.sprites.JoyStick;
import com.mygdx.game.objects.Player;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Syuqri on 3/7/2016.
 */
public class PlayState extends State{

    private boolean touchHeld = false;
    private JoyStick joystick;
    private com.mygdx.game.objects.Player player;
    private ArrayList<Obstacle> obstacles;
    private ArrayList<SideWall> sideWalls;
    private ArrayList<Switch> switches;
    private ArrayList<Barrier> barriers;
    private ArrayList<Power> powers;
    private int gameSpeed;


    protected PlayState(GameStateManager gsm) {
        super(gsm);
        player = new Player();
        //cam.setToOrtho(false, Gdx.graphics.getHeight()/2,Gdx.graphics.getWidth()/2);
        sideWalls = new ArrayList<SideWall>();
        obstacles = new ArrayList<Obstacle>();
        barriers = new ArrayList<Barrier>();
        powers = new ArrayList<Power>();
        switches = new ArrayList<Switch>();
        gameSpeed = 100;
        spawnSides();
    }

    @Override
    protected void handleInput() {
    }

    private void processInput(){
        float relativex = 0;
        float relativey = 0;
        Vector3 touchPos = new Vector3();
        if (Gdx.input.isTouched()) {
            joystick = new JoyStick(Gdx.input.getX(),Gdx.input.getY());
            touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            cam.unproject(touchPos);
            relativex = touchPos.x - (joystick.getX() + joystick.getJoystickWidth()/2);
            relativey = touchPos.y - (joystick.getY() + joystick.getJoystickHeight()/2);
            if (!touchHeld) {
                joystick.setX(touchPos.x - joystick.getJoystickWidth()/2);
                joystick.setY(touchPos.y - joystick.getJoystickHeight() / 2);
                joystick.setCX(touchPos.x - joystick.getJoystickCenterWidth() / 2);
                joystick.setCY(touchPos.y - joystick.getJoystickCenterHeight()/2);
                touchHeld = true;
            }
        } else {
            touchHeld = false;
        }

        if (touchHeld) {
            // check if touch is within joystick hitbox with buffer
            float angle = (float) Math.atan2(relativey, relativex);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            if (Math.abs(relativex) < joystick.getJoystickWidth()/2
                    && (Math.abs(relativey) < joystick.getJoystickHeight()/2)) {
                joystick.setCX(touchPos.x - joystick.getJoystickCenterWidth()/2);
                joystick.setCY(touchPos.y - joystick.getJoystickCenterHeight()/2);
            } else {
                joystick.setCX(cos * joystick.getJoystickWidth()/2 + joystick.getX() + joystick.getJoystickWidth()/2 - joystick.getJoystickCenterWidth()/2);
                joystick.setCY(sin * joystick.getJoystickWidth()/2 + joystick.getY() + joystick.getJoystickHeight()/2 - joystick.getJoystickCenterHeight()/2);
            }

            omniMove(cos, sin);
        }
    }
    private void omniMove(float x, float y){
        float prevx = player.x;
        float prevy = player.y;
        player.x += x * player.getPlayerSpeed() * Gdx.graphics.getDeltaTime();
        if (collisionCheck()){
            player.x = prevx;
        }
        player.y += y * player.getPlayerSpeed() * Gdx.graphics.getDeltaTime();
        if (collisionCheck()){
            player.y = prevy;
        }
    }
    private boolean collisionCheck(){
        if (player.x > 464 - player.width ){
            player.x = 464 - player.width;
        }
        if (player.x < 16){
            player.x = 16;
        }
//		collide with normal wall obstacle
        for (Obstacle obstacle: obstacles) {
            if (player.overlaps(obstacle)){
                return true;
            }
        }
//		collide with barriers
        for (Barrier barrier: barriers) {
            if (player.overlaps(barrier)){
                return true;
            }
        }
//		collide with switch
        for (Switch eachSwitch:switches){
            if (player.overlaps(eachSwitch)){
                // change this to another different switch image
                eachSwitch.setImage(new Texture(Gdx.files.internal("joystick_centre.png")));
                // then notify server
            }
        }
//		collide with power up
        for (Power power:powers){
            if (player.overlaps(power)){
                player.setPower(power.getType());
                powers.remove(power);
                // then notify server
            }
        }
        return false;
    }
    @Override
    public void update(float dt) {
       // handleInput();
        //player.update(dt);
        //joystick.update(dt);
    }
    private void spawnSides(){
        for (int i = 0; i < 2; i++) {
            SideWall sideWall = new SideWall();
            sideWall.x = (464*i);
            sideWall.y = 800;
            sideWall.width = 16;
            sideWall.height = 64;
            sideWalls.add(sideWall);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        if(touchHeld){
            sb.draw(joystick.getJoystickImage(), joystick.getX(), joystick.getY());
            sb.draw(joystick.getJoystickCentreImage(), joystick.getCX(), joystick.getCY());
        }

        for(SideWall sideWall: sideWalls){
            sb.draw(sideWall.getImage(), sideWall.x, sideWall.y);
        }
        for(Switch eachSwitch: switches) {
            sb.draw(eachSwitch.getImage(), eachSwitch.x, eachSwitch.y);

        }
        sb.draw(player.getTexture(),player.x,player.y);
        sb.end();
        processInput();

        Iterator<Obstacle> iter = obstacles.iterator();
        Iterator<SideWall> iter2 = sideWalls.iterator();
        while(iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= gameSpeed*Gdx.graphics.getDeltaTime();
            if(raindrop.y + 50 < 0) iter.remove();
//			if(raindrop.overlaps(player)) {
//				dropSound.play();
//				iter.remove();
//			}
        }
        while(iter2.hasNext()) {
            Rectangle side = iter2.next();
            side.y -= gameSpeed*Gdx.graphics.getDeltaTime();
            if(side.y + 50 < 0) iter2.remove();
        }
    }

    @Override
    public void dispose() {
// dispose of all the native resources
        for (Obstacle obstacle:obstacles) {
            obstacle.getImage().dispose();
        }
        for (Power power:powers) {
            power.getImage().dispose();
        }
        for (Barrier barrier:barriers) {
            barrier.getImage().dispose();
        }
        for (Switch eachSwitch:switches) {
            eachSwitch.getImage().dispose();
        }
        for (SideWall sideWall:sideWalls) {
            sideWall.getImage().dispose();
        }
        player.getImage().dispose();
        joystick.getJoystickCentreImage().dispose();
        joystick.getJoystickImage().dispose();
//		dropSound.dispose();
//		rainMusic.dispose();
        //sb.dispose();
    }
}
