package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.objects.Background;
import com.mygdx.game.objects.Barrier;
import com.mygdx.game.objects.BarrierOpen;
import com.mygdx.game.objects.Obstacle;
import com.mygdx.game.objects.Power;
import com.mygdx.game.objects.SideWall;
import com.mygdx.game.objects.Switch;
import com.mygdx.game.objects.JoyStick;
import com.mygdx.game.objects.Player;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Syuqri on 3/7/2016.
 */
public class PlayState extends State{

    //objects
    private JoyStick joystick;
    private Player player;
    private Vector3 touchPos = new Vector3();
    private SpriteBatch sb;
    //values
    public volatile boolean running;
    private boolean touched;
    private boolean touchHeld;
    private long endPowerTime;
    private int gameSpeed, speedIncrement, playerSpeed, dangerZone, powerCounter, doorCounter;

    //boolean arrays
    public boolean[] path = {true, true, true, true, true, true, true, true, true};
    boolean[] current = {true, true, true, true, true, true, true, true, true};
    boolean[] powerUp = {false, false, false, false, false, false, false, false, false};
    boolean[] doorSwitch = {false, false, false, false, false, false, false, false, false};
    boolean[] barrier = {false, false, false, false, false, false, false, false, false};

    //Arraylists
    private ArrayList<boolean[]> mapBuffer = new ArrayList<boolean[]>();
    private ArrayList<boolean[]> doorBuffer = new ArrayList<boolean[]>();
    private ArrayList<boolean[]> switchBuffer = new ArrayList<boolean[]>();
    private ArrayList<boolean[]> powerUpBuffer = new ArrayList<boolean[]>();

    private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    private ArrayList<SideWall> sideWalls = new ArrayList<SideWall>();
    private ArrayList<Switch> switches = new ArrayList<Switch>();
    private ArrayList<Barrier> barriers = new ArrayList<Barrier>();
    private ArrayList<BarrierOpen> barrierOpens = new ArrayList<BarrierOpen>();
    private ArrayList<Power> powers = new ArrayList<Power>();
    private ArrayList<Background> bg = new ArrayList<Background>();

    //final values
    final int spriteWidth = 50;
    final int spriteHeight = 50;
    private final String[] TYPES_OF_POWER = {"slowGameDown","fewerObstacles","speedPlayerUp","dangerZoneHigher"};

    protected PlayState(GameStateManager gsm) {
        super(gsm);
        System.out.println("This is playstate");
        running = true;
        //camera initialization
        cam = new OrthographicCamera();
        cam.setToOrtho(false, 480, 800);

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
        //spawning initialization
        MapMaker mapMaker = new MapMaker(this);
        mapMaker.start();
        System.out.println("This is end");
    }
    @Override
    protected void handleInput() {
        if(Gdx.input.isTouched()){
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            touched = true;
        } else {
            touched = false;
        }

        float relativex = 0;
        float relativey = 0;
        if (touched) {
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

    @Override
    public void render(SpriteBatch sb) {
        System.out.println("this is render");
        handleInput();
        checkSwitchCollision();
        System.out.println("after methods");
        this.sb=sb;
        // tell the camera to update its matrices.
        while (sideWalls.get(sideWalls.size() - 1).y < 999) {
            synchronized (mapBuffer) {
                while (mapBuffer.size() == 0){
                    try {
                        mapBuffer.wait();
                    } catch (InterruptedException e){

                    }
                }
                path = mapBuffer.remove(0);
                barrier = doorBuffer.remove(0);
                doorSwitch = switchBuffer.remove(0);
                powerUp = powerUpBuffer.remove(0);
                mapBuffer.notifyAll();
            }

            System.out.println("after lock");
            float temp = sideWalls.get(sideWalls.size() - 1).y + 50;
            spawnObstacle(temp);
            spawnPower();
            spawnSwitch();
            spawnDoor();
            spawnSides(temp);
            System.out.println("after spawns");
        }
        System.out.println("after sidewall");
        if (bg.get(bg.size() - 1).y < 1) {
            spawnBg();
        }
        cam.update();
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        System.out.println("after begin");


        for (Background backg : bg) {
            sb.draw(backg.getImage(), backg.x, backg.y);
        }
        for (Obstacle obstacle : obstacles) {
            sb.draw(obstacle.getImage(), obstacle.x, obstacle.y);
        }
        for (SideWall sideWall : sideWalls) {
            sb.draw(sideWall.getImage(), sideWall.x, sideWall.y);
        }
        for (Power power : powers) {
            sb.draw(power.getImage(), power.x, power.y);
        }
        for (Switch eachSwitch : switches) {
            sb.draw(eachSwitch.getImage(), eachSwitch.x, eachSwitch.y);
        }
        for (Barrier barrier : barriers) {
            sb.draw(barrier.getImage(), barrier.x, barrier.y);
        }
        for (BarrierOpen barrier : barrierOpens) {
            sb.draw(barrier.getImage(), barrier.x, barrier.y);
        }
        System.out.println("after for loops");
        sb.draw(player.getTexture(), player.x, player.y);
        if(touched){

            sb.draw(joystick.getJoystickImage(), joystick.getX(), joystick.getY());
            sb.draw(joystick.getJoystickCentreImage(), joystick.getCX(), joystick.getCY());
        }
        sb.end();
        effectPower();
        notifyDangerZone();




        Iterator<Obstacle> iter = obstacles.iterator();
        Iterator<SideWall> iter2 = sideWalls.iterator();
        Iterator<Power> iter3 = powers.iterator();
        Iterator<Switch> iter4 = switches.iterator();
        Iterator<Barrier> iter5 = barriers.iterator();
        Iterator<BarrierOpen> iter6 = barrierOpens.iterator();
        Iterator<Background> iter7 = bg.iterator();

        player.y -= gameSpeed*Gdx.graphics.getDeltaTime();

        while (iter.hasNext()) {
            Rectangle obstacle = iter.next();
            obstacle.y -= gameSpeed * Gdx.graphics.getDeltaTime();
            if (obstacle.y + spriteHeight < 0) iter.remove();
        }
        while (iter2.hasNext()) {
            Rectangle side = iter2.next();
            side.y -= gameSpeed * Gdx.graphics.getDeltaTime();
            if (side.y + spriteHeight < 0) iter2.remove();
        }
        while (iter3.hasNext()) {
            Rectangle power = iter3.next();
            power.y -= gameSpeed * Gdx.graphics.getDeltaTime();
            if (power.y + spriteHeight < 0) iter3.remove();
        }
        while (iter4.hasNext()) {
            Rectangle swt = iter4.next();
            swt.y -= gameSpeed * Gdx.graphics.getDeltaTime();
            if (swt.y + spriteHeight < 0) iter4.remove();
        }
        while (iter5.hasNext()) {
            Rectangle door = iter5.next();
            door.y -= gameSpeed * Gdx.graphics.getDeltaTime();
            if (door.y + spriteHeight < 0) iter5.remove();
        }
        while (iter6.hasNext()) {
            Rectangle door = iter6.next();
            door.y -= gameSpeed * Gdx.graphics.getDeltaTime();
            if (door.y + spriteHeight < 0) iter6.remove();
        }
        while (iter7.hasNext()) {
            Rectangle bg = iter7.next();
            bg.y -= gameSpeed * Gdx.graphics.getDeltaTime();
            if (bg.y + 800 < 0) iter7.remove();
        }
        System.out.println("this is render end");
    }
    @Override
    public void update(float dt) {
        // handleInput();
        //player.update(dt);
        //joystick.update(dt);
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
        sb.dispose();
    }
    private void removeBarriers(){
//		TODO: if notified by server (Ryan)
        barriers.clear();
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



    private void checkSwitchCollision() {
        //		collide with switch
        for (Switch eachSwitch : switches) {
            if (player.overlaps(eachSwitch)) {
                // change this to another different switch image
                eachSwitch.setImage(new Texture(Gdx.files.internal("switch_on.png")));
                for (Barrier barrier : barriers) {
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
    }


    private void notifyDangerZone(){
        if (player.y < dangerZone) {
            //notify server
        }
    }

    /**
     Method handling power-ups
     */
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
    /**
     Method to calculate and randomize the wall, power, switch etc. placement. It keeps randomly
     generating a sequence until it fulfils the condition where the player can reach to the next
     layer of walls. I.e, no dead ends. Also ensures that the power is spawned on a 'box' not
     occupied by a wall and that switches are reachable.
     */
    void wallCoord(){
        boolean test = false;
        int out_index = 0;
        boolean[] new_row = {false, false, false, false, false, false, false, false, false};

        // random generator
        while (!test) {
            int temp = MathUtils.random(1, 9);
            for (int i = 0; i < temp; i++) {
                int coord = MathUtils.random(0,8);
                new_row[coord] = true;
            }
            for (int i = 0; i < new_row.length; i++) {
                if (new_row[i] && current[i]) {
                    test = true;
                    break;
                }
            }
        }

        // updating the path array (only 1 path)
        for (int k = 0; k < new_row.length; k++) {
            if (new_row[k] && current[k]) {
                current[k] = true;
                out_index = k;
            } else {
                current[k] = false;
            }
        }

        // updating the path array (if there are walkable areas next to the true path then they are
        // also true)
        for (int j = 1; j < new_row.length; j++) {
            if (out_index + j < new_row.length) {
                if (new_row[out_index + j] && current[out_index + j - 1]) {
                    current[out_index + j] = true;
                }
                else {
                    break;
                }
            }
            if (out_index - j >= 0) {
                if (new_row[out_index - j] && current[out_index - j + 1]) {
                    current[out_index - j] = true;
                }
                else {
                    break;
                }
            }
        }

        // spawning power ups after a certain time
        boolean[] temp_power = {false, false, false, false, false, false, false, false, false};
        if (powerCounter > 20){
            while (true){
                int temp = MathUtils.random(0,8);
                if (new_row[temp]){
                    temp_power[temp] = true;
                    powerCounter = 0;
                    break;
                }
            }
        }

        // spawning door switch
        boolean[] temp_switch = {false, false, false, false, false, false, false, false, false};
        if (doorCounter == 40){
            while (true){
                int temp = MathUtils.random(0,8);
                if (current[temp]){
                    temp_switch[temp] = true;
                    break;
                }
            }
        }

        // spawning door/barrier
        boolean[] temp_door = {false, false, false, false, false, false, false, false, false};
        if (doorCounter > 45){
            for (int i = 0; i < current.length; i++) {
                if (new_row[i]) {
                    temp_door[i] = true;
                }
            }
            doorCounter = 0;
        }
        synchronized (mapBuffer) {
            while (mapBuffer.size() > 3){
                System.out.println("wallcoord");
                try {
                    mapBuffer.wait();
                } catch (InterruptedException e){

                }
            }
            powerUpBuffer.add(temp_power);
            mapBuffer.add(new_row);
            switchBuffer.add(temp_switch);
            doorBuffer.add(temp_door);
            mapBuffer.notifyAll();
            System.out.println("wallcoord fin");
        }
    }

    public void createObstacle(boolean[] map) {
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
    public void createBg(){
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
    private void spawnObstacle(float in) {
        for (int i = 0; i < path.length; i++) {
            if (!path[i]) {
                Obstacle obstacle = new Obstacle();
                obstacle.x = (spriteWidth * i) + 15;
                obstacle.y = in;
                obstacle.width = spriteWidth;
                obstacle.height = spriteHeight;
                obstacles.add(obstacle);
            }
        }
        powerCounter += 1;
        doorCounter += 1;
    }

    private void spawnPower() {
        for (int i = 0; i < powerUp.length; i++) {
            if (powerUp[i]) {
                Power power = new Power(TYPES_OF_POWER[(int)(Math.random()*TYPES_OF_POWER.length)]);
                power.x = (spriteWidth * i) + 15;
                power.y = sideWalls.get(sideWalls.size()-1).y+50;
                power.width = spriteWidth;
                power.height = spriteHeight;
                powers.add(power);
            }
        }
    }

    private void spawnSwitch(){
        for (int i = 0; i < doorSwitch.length; i++) {
            if (doorSwitch[i]) {
                Switch doorSwitch = new Switch();
                doorSwitch.x = (spriteWidth * i) + 15;
                doorSwitch.y = sideWalls.get(sideWalls.size()-1).y+50;
                doorSwitch.width = spriteWidth;
                doorSwitch.height = spriteHeight;
                switches.add(doorSwitch);
            }
        }
    }

    private void spawnDoor(){
        for (int i = 0; i < barrier.length; i++) {
            if (barrier[i]) {
                Barrier door = new Barrier();
                door.x = (spriteWidth * i) + 15;
                door.y = sideWalls.get(sideWalls.size()-1).y+50;
                door.width = spriteWidth;
                door.height = spriteHeight;
                barriers.add(door);
            }
        }
    }

    public void createSides(){
        for (int i = 0; i < 2; i++) {
            SideWall sideWall = new SideWall();
            sideWall.x = (465*i);
            sideWall.y = 800;
            sideWall.width = 15;
            sideWall.height = spriteHeight;
            sideWalls.add(sideWall);
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
}
