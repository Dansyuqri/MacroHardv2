package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.objects.Background;
import com.mygdx.game.objects.Barrier;
import com.mygdx.game.objects.BarrierOpen;
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
    private long lastDropTime, endPowerTime;
    private int gameSpeed, speedIncrement, playerSpeed, dangerZone, powerCounter, doorCounter;

    boolean[] path;
    boolean[] current = {false, false, false, false, false, false, false, false, false};
    boolean[] powerUp = {false, false, false, false, false, false, false, false, false};
    boolean[] doorSwitch = {false, false, false, false, false, false, false, false, false};
    boolean[] barrier = {false, false, false, false, false, false, false, false, false};

    private ArrayList<Obstacle> obstacles;
    private ArrayList<SideWall> sideWalls;
    private ArrayList<Switch> switches;
    private ArrayList<Barrier> barriers;
    private ArrayList<BarrierOpen> barrierOpens;
    private ArrayList<Power> powers;
    private ArrayList<Background> bg;

    final int spriteWidth = 50;
    final int spriteHeight = 50;
    private final String[] TYPES_OF_POWER = {"slowGameDown","fewerObstacles","speedPlayerUp","dangerZoneHigher"};

    protected PlayState(GameStateManager gsm) {
        super(gsm);
        //camera initialization
        cam = new OrthographicCamera();
        cam.setToOrtho(false, 480, 800);

        //arraylist initialization
        obstacles = new ArrayList<Obstacle>();
        sideWalls = new ArrayList<SideWall>();
        barriers = new ArrayList<Barrier>();
        switches = new ArrayList<Switch>();
        powers = new ArrayList<Power>();
        barrierOpens = new ArrayList<BarrierOpen>();
        bg = new ArrayList<Background>();


        //object initialization
        player = new Player();
        joystick = new JoyStick();

        //misc values initialization
        gameSpeed = 100;
        speedIncrement = 100;
        playerSpeed = 300;
        dangerZone = 400;
        powerCounter = 0;
        doorCounter = 0;

        boolean[] temp = {true, true, true, true, true, true, true, true, true};
        //spawning initialization
        wallCoord(temp);
        createObstacle(current);
        createSides();
        createBg();
        spawnBg();
    }
    @Override
    protected void handleInput() {
        float relativex = 0;
        float relativey = 0;
        Vector3 touchPos = new Vector3();
        if (Gdx.input.isTouched()) {
            touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            cam.unproject(touchPos);
            relativex = touchPos.x - (joystick.getX() + joystick.getJoystickWidth()/2);
            relativey = touchPos.y - (joystick.getY() + joystick.getJoystickHeight()/2);
            if (!touchHeld) {
                joystick.setX(touchPos.x - joystick.getJoystickWidth()/2);
                joystick.setY(touchPos.y - joystick.getJoystickHeight()/2);
                joystick.setCX(touchPos.x - joystick.getJoystickCenterWidth()/2);
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
        player.x += x * playerSpeed * Gdx.graphics.getDeltaTime();
        player.y += y * playerSpeed * Gdx.graphics.getDeltaTime();
        if (collisionCheck()){
            player.x = prevx;
            player.y = prevy;
            if (x > 0) x = 1;
            if (x < 0) x = -1;
            if (y > 0) y = 1;
            if (y < 0) y = -1;
            player.x += x * playerSpeed * Gdx.graphics.getDeltaTime();
            if (collisionCheck()){
                player.x = prevx;
            }
            player.y += y * playerSpeed * Gdx.graphics.getDeltaTime();
            if (collisionCheck()){
                player.y = prevy;
            }
            System.out.println(player.x);
            System.out.println(player.y);
        }
    }
    private boolean collisionCheck(){
        if (player.x > 465 - player.width ){
            player.x = 465 - player.width;
        }
        if (player.x < 15){
            player.x = 15;
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
                eachSwitch.setImage(new Texture(Gdx.files.internal("switch_on.png")));
                for (Barrier barrier: barriers){
                    BarrierOpen bg = new BarrierOpen();
                    bg.x = barrier.x;
                    bg.y = barrier.y;
                    bg.width = 50;
                    bg.height = 50;
                    barrierOpens.add(bg);
                }
                removeBarriers();
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

    private void removeBarriers(){
//		TODO: if notified by server (Ryan)
        barriers.clear();
    }
    @Override
    public void update(float dt) {
       // handleInput();
        //player.update(dt);
        //joystick.update(dt);
    }

    /**
     Spawn side walls to fill up the gap between the playing field and the actual maze
     */
    private void spawnSides(float in){
        for (int i = 0; i < 2; i++) {
            SideWall sideWall = new SideWall();
            sideWall.x = (465*i);
            sideWall.y = in;
            sideWall.width = 15;
            sideWall.height = spriteHeight;
            sideWalls.add(sideWall);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        // tell the camera to update its matrices.
        cam.update();
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        if(touchHeld){

            sb.draw(joystick.getJoystickImage(), joystick.getX(), joystick.getY());
            sb.draw(joystick.getJoystickCentreImage(), joystick.getCX(), joystick.getCY());
        }

        for(Background backg: bg) {
            sb.draw(backg.getImage(), backg.x, backg.y);
        }
        for(Obstacle obstacle: obstacles) {
            sb.draw(obstacle.getImage(), obstacle.x, obstacle.y);
        }
        for(SideWall sideWall: sideWalls){
            sb.draw(sideWall.getImage(), sideWall.x, sideWall.y);
        }
        for (Power power:powers){
            sb.draw(power.getImage(), power.x, power.y);
        }
        for(Switch eachSwitch: switches) {
            sb.draw(eachSwitch.getImage(), eachSwitch.x, eachSwitch.y);
        }
        for(Barrier barrier: barriers){
            sb.draw(barrier.getImage(), barrier.x, barrier.y);
        }
        for(BarrierOpen barrier: barrierOpens){
            sb.draw(barrier.getImage(), barrier.x, barrier.y);
        }
        sb.draw(player.getTexture(),player.x,player.y);
        sb.end();
        handleInput();
        effectPower();
        notifyDangerZone();

        while (sideWalls.get(sideWalls.size()-1).y < 999){
            float temp = sideWalls.get(sideWalls.size()-1).y + 50;
            wallCoord(path);
            spawnObstacle(current, temp);
            spawnPower(powerUp);
            spawnSwitch(doorSwitch);
            spawnDoor(barrier);
            spawnSides(temp);
        }
        if(bg.get(bg.size()-1).y < 1) {
            spawnBg();
        }

        Iterator<Obstacle> iter = obstacles.iterator();
        Iterator<SideWall> iter2 = sideWalls.iterator();
        Iterator<Power> iter3 = powers.iterator();
        Iterator<Switch> iter4 = switches.iterator();
        Iterator<Barrier> iter5 = barriers.iterator();
        Iterator<BarrierOpen> iter6 = barrierOpens.iterator();
        Iterator<Background> iter7 = bg.iterator();

        player.y -= gameSpeed*Gdx.graphics.getDeltaTime();

        while(iter.hasNext()) {
            Rectangle obstacle = iter.next();
            obstacle.y -= gameSpeed*Gdx.graphics.getDeltaTime();
            if(obstacle.y + spriteHeight < 0) iter.remove();
        }
        while(iter2.hasNext()) {
            Rectangle side = iter2.next();
            side.y -= gameSpeed*Gdx.graphics.getDeltaTime();
            if(side.y + spriteHeight < 0) iter2.remove();
        }
        while(iter3.hasNext()){
            Rectangle power = iter3.next();
            power.y -= gameSpeed*Gdx.graphics.getDeltaTime();
            if(power.y + spriteHeight < 0) iter3.remove();
        }
        while(iter4.hasNext()){
            Rectangle swt = iter4.next();
            swt.y -= gameSpeed*Gdx.graphics.getDeltaTime();
            if(swt.y + spriteHeight < 0) iter4.remove();
        }
        while(iter5.hasNext()){
            Rectangle door = iter5.next();
            door.y -= gameSpeed*Gdx.graphics.getDeltaTime();
            if(door.y + spriteHeight < 0) iter5.remove();
        }
        while(iter6.hasNext()){
            Rectangle door = iter6.next();
            door.y -= gameSpeed*Gdx.graphics.getDeltaTime();
            if(door.y + spriteHeight < 0) iter6.remove();
        }
        while(iter7.hasNext()){
            Rectangle bg = iter7.next();
            bg.y -= gameSpeed*Gdx.graphics.getDeltaTime();
            if(bg.y + 800 < 0) iter7.remove();
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
        for (BarrierOpen barrier:barrierOpens) {
            barrier.getImage().dispose();
        }
        player.getTexture().dispose();
        joystick.getJoystickCentreImage().dispose();
        joystick.getJoystickImage().dispose();
//		dropSound.dispose();
//		rainMusic.dispose();
        //sb.dispose();
    }

    private void notifyDangerZone(){
        if (player.y < dangerZone) {
            //notify server
        }
    }

    boolean setPowerLock = true;
    private void effectPower(){
        if (System.currentTimeMillis() > endPowerTime) {
            setPowerLock = true;
        }
        if (System.currentTimeMillis() <= endPowerTime){
            if (setPowerLock) {
                endPowerTime = System.currentTimeMillis() + 5000;
                setPowerLock = false;
            }
            if (player.getPower().equals("slowGameDown")) {
                gameSpeed -= speedIncrement;
            } else if (player.getPower().equals("fewerObstacles")) {

            } else if (player.getPower().equals("speedPlayerUp")) {
                playerSpeed += speedIncrement;
            } else if (player.getPower().equals("dangerZoneHigher")) {
                dangerZone += 50;
            }
        }
    }
    private void wallCoord(boolean[] pathin){
        boolean test = false;
        int out_index = 0;

        // random generator
        while (!test) {
            int temp = MathUtils.random(1, 9);
            for (int i = 0; i < temp; i++) {
                int coord = MathUtils.random(0,8);
                current[coord] = true;
            }
            for (int i = 0; i < current.length; i++){
                if (current[i] && pathin[i]){
                    test = true;
                    break;
                }
            }
        }

        // updating the path array (only 1 path)
        for (int k = 0; k < current.length; k++) {
            if (current[k] && pathin[k]) {
                pathin[k] = true;
                out_index = k;
            } else {
                pathin[k] = false;
            }
        }

        // updating the path array (if there are walkable areas next to the true path then they are
        // also true)
        for (int j = 1; j < current.length; j++) {
            if (out_index + j < current.length) {
                if (current[out_index + j] && pathin[out_index + j - 1]) {
                    pathin[out_index + j] = true;
                }
                else {
                    break;
                }
            }
            if (out_index - j >= 0) {
                if (current[out_index - j] && pathin[out_index - j + 1]) {
                    pathin[out_index - j] = true;
                }
                else {
                    break;
                }
            }
        }

        path = pathin;

        // spawning power ups after a certain time
        if (powerCounter > 20){
            boolean collision = true;
            while (collision){
                int temp = MathUtils.random(0,8);
                if (current[temp]){
                    powerUp[temp] = true;
                    collision = false;
                    powerCounter = 0;
                    break;
                }
            }
        }

        // spawning door switch
        if (doorCounter == 40){
            boolean reach = false;
            while (!reach){
                int temp = MathUtils.random(0,8);
                if (path[temp]){
                    doorSwitch[temp] = true;
                    reach = true;
                    break;
                }
            }
        }

        // spawning door/barrier
        if (doorCounter > 45){
            for (int i = 0; i < current.length; i++) {
                if (current[i]) {
                    barrier[i] = true;
                }
            }
            doorCounter = 0;
        }
    }

    private void createObstacle(boolean[] map) {
        for (int i = 0; i < map.length; i++) {
            if (!map[i]) {
                Obstacle obstacle = new Obstacle();
                obstacle.x = (spriteWidth * i) + 15;
                obstacle.y = 800;
                obstacle.width = spriteWidth;
                obstacle.height = spriteHeight;
                obstacles.add(obstacle);
            }
            current[i] = false;
        }
        powerCounter += 1;
        doorCounter += 1;
    }
    private void createBg(){
        Background backg = new Background();
        backg.x = 0;
        backg.y = 0;
        backg.width = 480;
        backg.height = 800;
        bg.add(backg);
    }
    private void spawnBg(){
        Background backg = new Background();
        backg.x = 0;
        backg.y = 800;
        backg.width = 480;
        backg.height = 800;
        bg.add(backg);
    }

    /**
     Method to spawn the walls using the coordinates from the wallCoord() method
     */
    private void spawnObstacle(boolean[] map, float in) {
        for (int i = 0; i < map.length; i++) {
            if (!map[i]) {
                Obstacle obstacle = new Obstacle();
                obstacle.x = (spriteWidth * i) + 15;
                obstacle.y = in;
                obstacle.width = spriteWidth;
                obstacle.height = spriteHeight;
                obstacles.add(obstacle);
            }
            current[i] = false;
        }
        powerCounter += 1;
        doorCounter += 1;
    }

    private void spawnPower(boolean[] map) {
        for (int i = 0; i < map.length; i++) {
            if (map[i]) {
                Power power = new Power(TYPES_OF_POWER[(int)(Math.random()*TYPES_OF_POWER.length)]);
                power.x = (spriteWidth * i) + 15;
                power.y = sideWalls.get(sideWalls.size()-1).y+50;
                power.width = spriteWidth;
                power.height = spriteHeight;
                powers.add(power);
            }
            powerUp[i] = false;
        }
    }

    private void spawnSwitch(boolean[] map){
        for (int i = 0; i < map.length; i++) {
            if (map[i]) {
                Switch doorSwitch = new Switch();
                doorSwitch.x = (spriteWidth * i) + 15;
                doorSwitch.y = sideWalls.get(sideWalls.size()-1).y+50;
                doorSwitch.width = spriteWidth;
                doorSwitch.height = spriteHeight;
                switches.add(doorSwitch);
            }
            doorSwitch[i] = false;
        }
    }

    private void spawnDoor(boolean[] map){
        for (int i = 0; i < map.length; i++) {
            if (map[i]) {
                Barrier door = new Barrier();
                door.x = (spriteWidth * i) + 15;
                door.y = sideWalls.get(sideWalls.size()-1).y+50;
                door.width = spriteWidth;
                door.height = spriteHeight;
                barriers.add(door);
            }
            barrier[i] = false;
        }
    }
    private void createSides(){
        for (int i = 0; i < 2; i++) {
            SideWall sideWall = new SideWall();
            sideWall.x = (465*i);
            sideWall.y = 800;
            sideWall.width = 15;
            sideWall.height = spriteHeight;
            sideWalls.add(sideWall);
        }
    }
}
