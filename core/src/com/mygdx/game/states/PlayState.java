package com.mygdx.game.states;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.objects.Background;
import com.mygdx.game.objects.DangerZone;
import com.mygdx.game.objects.Overlay;
import com.mygdx.game.objects.Barrier;
import com.mygdx.game.objects.BarrierOpen;
import com.mygdx.game.objects.Obstacle;
import com.mygdx.game.objects.Power;
import com.mygdx.game.objects.SideWall;
import com.mygdx.game.objects.Switch;
import com.mygdx.game.objects.JoyStick;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.UI;
import com.sun.org.apache.xpath.internal.operations.Bool;

import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Syuqri on 3/7/2016.
 */
public abstract class PlayState extends State{

    //objects
    private JoyStick joystick;
    protected Player player;
    private Vector3 touchPos = new Vector3();
    protected final int GAME_WIDTH = 9;

    //values
    public boolean running;
    private boolean touched;
    private boolean touchHeld;
    private long endPowerTime = System.currentTimeMillis();
    protected int gameSpeed;
    protected double speedChange;
    protected int playerSpeed, dangerZone, powerCounter, doorCounter;
    protected int dangerZoneSpeedLimit;
    public long start = System.currentTimeMillis();
    boolean powerState = false;
    boolean powerEffectTaken = false;

    //boolean arrays
    public boolean[] path = createArray(true);
    boolean[] current = createArray(true);
    boolean[] powerUp = createArray(false);
    boolean[] doorSwitch = createArray(false);
    boolean[] barrier = createArray(false);

    //Arraylists
    protected ArrayList<boolean[]> mapBuffer = new ArrayList<boolean[]>();
    protected ArrayList<boolean[]> doorBuffer = new ArrayList<boolean[]>();
    protected ArrayList<boolean[]> switchBuffer = new ArrayList<boolean[]>();
    protected ArrayList<boolean[]> powerUpBuffer = new ArrayList<boolean[]>();

    private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    public static ArrayList<SideWall> sideWalls = new ArrayList<SideWall>();
    private ArrayList<Switch> switches = new ArrayList<Switch>();
    private ArrayList<Barrier> barriers = new ArrayList<Barrier>();
    private ArrayList<BarrierOpen> barrierOpens = new ArrayList<BarrierOpen>();
    private ArrayList<Power> powers = new ArrayList<Power>();
    private ArrayList<Background> bg = new ArrayList<Background>();
    private ArrayList<Overlay> effects = new ArrayList<Overlay>();
    private ArrayList<DangerZone> dz = new ArrayList<DangerZone>();
    private ArrayList<UI> ui = new ArrayList<UI>();

    //final values
    final int spriteWidth = 50;
    final int spriteHeight = 50;
    private final String[] TYPES_OF_POWER = {"slowGameDown","speedPlayerUp","dangerZoneHigher"};


    protected PlayState(GameStateManager gsm) {
        super(gsm);
        running = true;

        //camera initialization
        cam = new OrthographicCamera();
        cam.setToOrtho(false, 480, 800);

        //object initialization
        player = new Player();
        joystick = new JoyStick();

        //misc values initialization
        gameSpeed = 80;
        speedChange = 0.6;
        playerSpeed = 300;
        dangerZone = 200;
        powerCounter = 0;
        doorCounter = 0;
        dangerZoneSpeedLimit = 200;

        createBg();
        createObstacle();
        createSides();
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
            if (!touchHeld) {
                joystick.setX(touchPos.x - joystick.getJoystickWidth()/2);
                joystick.setY(touchPos.y - joystick.getJoystickHeight()/2);
                joystick.setCX(touchPos.x - joystick.getJoystickCenterWidth()/2);
                joystick.setCY(touchPos.y - joystick.getJoystickCenterHeight()/2);
                touchHeld = true;
            }
            relativex = touchPos.x - (joystick.getX() + joystick.getJoystickWidth()/2);
            relativey = touchPos.y - (joystick.getY() + joystick.getJoystickHeight()/2);
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

            if (Math.pow(relativex, 2) + Math.pow(relativey, 2) > 400) {
                omniMove(cos, sin);
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        long score = System.currentTimeMillis()-start;
        handleInput();
        checkSwitchCollision();
        // tell the camera to update its matrices.
        while (sideWalls.get(sideWalls.size() - 1).y < 999) {
            synchronized (this) {
                while (mapBuffer.size() == 0){
                    try {
                        wait();
                    } catch (InterruptedException ignored){}
                }
                path = mapBuffer.remove(0);
                barrier = doorBuffer.remove(0);
                doorSwitch = switchBuffer.remove(0);
                powerUp = powerUpBuffer.remove(0);
                notifyAll();
            }
            float temp = sideWalls.get(sideWalls.size() - 1).y + 50;
            spawnObstacle(temp);
            spawnPower();
            spawnSwitch();
            spawnDoor();
            spawnSides(temp);
        }
        if (bg.get(bg.size() - 1).y < 1) {
            spawnBg();
        }
        // tell the camera to update its matrices.
        cam.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        sb.setProjectionMatrix(cam.combined);

        // begin a new batch and draw the player and all objects
        sb.begin();

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

        sb.draw(player.getTexture(), player.x, player.y);

        for (Overlay effect : effects) {
            sb.draw(effect.getImage(), effect.x, effect.y);
        }
        for (DangerZone danger : dz) {
            sb.draw(danger.getImage(), danger.x, danger.y);
        }
        for (UI inter : ui) {
            sb.draw(inter.getImage(), inter.x, inter.y);
        }

        if(touched){

            sb.draw(joystick.getJoystickImage(), joystick.getX(), joystick.getY());
            sb.draw(joystick.getJoystickCentreImage(), joystick.getCX(), joystick.getCY());
        }

        sb.end();

//		constantly check if any power/DangerZone's effect still lingers
        effectPower();
        effectDangerZone(player);

        // move the obstacles, remove any that are beneath the bottom edge of the screen.

        player.y -= gameSpeed * Gdx.graphics.getDeltaTime();

        Iterator<Obstacle> iter = obstacles.iterator();
        Iterator<SideWall> iter2 = sideWalls.iterator();
        Iterator<Power> iter3 = powers.iterator();
        Iterator<Switch> iter4 = switches.iterator();
        Iterator<Barrier> iter5 = barriers.iterator();
        Iterator<BarrierOpen> iter6 = barrierOpens.iterator();
        Iterator<Background> iter7 = bg.iterator();
        Iterator<Overlay> iter8 = effects.iterator();
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
        while (iter8.hasNext()) {
            Rectangle effect = iter8.next();
            effect.y -= gameSpeed * Gdx.graphics.getDeltaTime();
            if (effect.y + 800 < 0) iter8.remove();
        }
    }
    @Override
    public void update(float dt) {
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
        player.getImage().dispose();
        joystick.getJoystickImage().dispose();
        joystick.getJoystickCentreImage().dispose();
    }






    /***************************************************
     *  CREATE METHODS HERE
     ****************************************************
     */

    private void createObstacle() {
        for (int i = 0; i < path.length; i++) {
            if (!path[i]) {
                Obstacle obstacle = new Obstacle();
                obstacle.x = (spriteWidth * i) + 15;
                obstacle.y = 800;
                obstacle.width = spriteWidth;
                obstacle.height = spriteHeight;
                obstacles.add(obstacle);
            }
        }
        powerCounter += 1;
        doorCounter += 1;
    }
    private void createBg(){
        Background backg = new Background(0);
        Overlay effect = new Overlay(0);
        DangerZone danger = new DangerZone(0);
        UI inter = new UI(0);
        bg.add(backg);
        effects.add(effect);
        dz.add(danger);
        ui.add(inter);
    }
    private void createSides(){
        int counter = 0;
        while (counter*spriteHeight <= 800) {
            for (int i = 0; i < 2; i++) {
                SideWall sideWall = new SideWall(spriteHeight, counter*spriteHeight, i);
                sideWalls.add(sideWall);
            }
            counter++;
        }
    }

/***********************************************
 *  SPAWN METHODS HERE
 ***********************************************
 */
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
    private void spawnBg(){
        Background backg = new Background(800);
        Overlay effect = new Overlay(800);
        bg.add(backg);
        effects.add(effect);
    }
    private void spawnPower() {
        for (int i = 0; i < powerUp.length; i++) {
            if (powerUp[i]) {
                Power power = new Power(TYPES_OF_POWER[(int)(Math.random()*TYPES_OF_POWER.length)],i);
                powers.add(power);
            }
        }
    }
    private void spawnSwitch(){
//        ArrayList<Rectangle> dfs0 = new ArrayList<Rectangle>();
//        ArrayList<String> dfs1 = new ArrayList<String>();
//        float x = 15;
//        float y = sideWalls.get(PlayState.sideWalls.size()-1).y+50;
//        boolean spawn = false;
//        for (int i = 0; i < path.length; i++){
//            if (path[i]){
//                x = 15 + i*50;
//            }
//        }
//        while (!spawn){
//            Rectangle current = new Rectangle();
//            current.height = spriteHeight;
//            current.width = spriteWidth;
//            current.x = x;
//            current.y = y;
//            dfs0.add(current);
//            dfs1.add("grey");
//            //check counter-clockwise, starting with left
//            boolean leftCheck = false;
//            boolean bottomCheck = false;
//            boolean rightCheck = false;
//            boolean topCheck = false;
//            for (int i = 0; i < 4; i++){
//                while (i == 0 && x > 50 && !leftCheck){
//                    float tempX = x - 50;
//                    for (Rectangle check: obstacles){
//                        if (check.contains(tempX,y)){
//                            leftCheck = true;
//                        }
//                    }
//                }
//                while (i == 1 && y > 799 && !bottomCheck){
//                    float tempY = y - 50;
//                    for (Rectangle check: obstacles){
//                        if (check.contains(x,tempY)){
//                            bottomCheck = true;
//                        }
//                    }
//                }
//                while (i == 2 && x < 415 && !rightCheck){
//                    float tempX = x + 50;
//                    for (Rectangle check: obstacles){
//                        if (check.contains(tempX,y)){
//                            rightCheck = true;
//                        }
//                    }
//                }
//                while (i == 3 && y < 999 && !topCheck){
//                    float tempY = y + 50;
//                    for (Rectangle check: obstacles){
//                        if (check.contains(x,tempY)){
//                            topCheck = true;
//                        }
//                    }
//                }
//            }
//
//        }
        for (int i = 0; i < doorSwitch.length; i++) {
            if (doorSwitch[i]) {
                Switch doorSwitch = new Switch(spriteWidth, spriteHeight, i);
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
    /**
     Spawn side walls to fill up the gap between the playing field and the actual maze
     */
    private void spawnSides(float in){
        for (int i = 0; i < 2; i++) {
            SideWall sideWall = new SideWall(spriteHeight,in,i);
            sideWalls.add(sideWall);
        }
    }
    /**
     Method to move the player
     */
    private void omniMove(float x, float y){
        float prevx = player.x;
        float prevy = player.y;
        player.x += x * playerSpeed * Gdx.graphics.getDeltaTime();
        player.y += y * playerSpeed * Gdx.graphics.getDeltaTime();
        if (collidesObstacle()){
            player.x = prevx;
            player.y = prevy;
            if (x > 0) x = 1;
            if (x < 0) x = -1;
            if (y > 0) y = 1;
            if (y < 0) y = -1;
            player.x += x * playerSpeed * Gdx.graphics.getDeltaTime();
            if (collidesObstacle()){
                player.x = prevx;
            }
            player.y += y * playerSpeed * Gdx.graphics.getDeltaTime();
            if (collidesObstacle()){
                player.y = prevy;
            }
        }
    }

/***********************************************
 * MISC METHODS HERE
 ************************************************
 */

    /**
     Method handling collision. If there is an overlap over an object that should be impassable,
     the player will be moved back to his previous position (remembered by a temporary variable)
     */
    private boolean collidesObstacle(){
// collision with screen boundaries
        if (player.x > 465 - player.width ){
            player.x = 465 - player.height;
        }

        if (player.x < 15){
            player.x = 15;
        }

        if (player.y > 750){
            player.y = 750;
        }

//		collide with normal wall obstacle
        for (Obstacle obstacle : obstacles) {
            if (player.overlaps(obstacle)) {
                return true;
            }
        }
//		collide with barriers
        for (Barrier barrier : barriers) {
            if (player.overlaps(barrier)) {
                return true;
            }
        }
        return false;
    }

    /**
     Method to calculate and randomize the wall, power, switch etc. placement. It keeps randomly
     generating a sequence until it fulfils the condition where the player can reach to the next
     layer of walls. I.e, no dead ends. Also ensures that the power is spawned on a 'box' not
     occupied by a wall and that switches are reachable.
     */

    private void checkSwitchCollision(){
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
//		collide with power up PASSIVE
        for (Power power:powers){
            if (player.overlaps(power)){
                player.setPower(power.getType());
                powers.remove(power);
                powerState = true;
                endPowerTime = System.currentTimeMillis()+5000;
                System.out.println(power.getType());
            }
        }
    }


    private void removeBarriers(){
//		TODO: if notified by server (Ryan)
        barriers.clear();
    }

    /**
     Methods handling power-ups/affecting game attributes
     */
    boolean dangerZoneEffectTaken = false;
    private void effectDangerZone(Player p) {
        if (p.getY()<=dangerZone && !dangerZoneEffectTaken && gameSpeed<=dangerZoneSpeedLimit) {
            gameSpeed += 100;
            dangerZoneEffectTaken = true;
        } else if (p.getY()>dangerZone) {
            dangerZoneEffectTaken = false;
        }
    }

    private void effectPower(){
        if (powerState) {
            if (!powerEffectTaken) {
                if (player.getPower().equals("slowGameDown")) {
                    gameSpeed *= speedChange;
                } else if (player.getPower().equals("speedPlayerUp")) {
                    playerSpeed *= speedChange;
                } else if (player.getPower().equals("dangerZoneHigher")) {
                    dangerZone += 50;
                }
                powerEffectTaken = true;
            }
            if (System.currentTimeMillis() >= endPowerTime) {
                if (player.getPower().equals("slowGameDown")) {
                    gameSpeed *= speedChange;
                } else if (player.getPower().equals("speedPlayerUp")) {
                    playerSpeed *= speedChange;
                } else if (player.getPower().equals("dangerZoneHigher")) {
                    dangerZone -= 50;
                    // change background file
                }
                powerState = false;
                powerEffectTaken = false;
            }
        }
    }

    protected boolean[] createArray(boolean b){
        boolean[] array = new boolean[GAME_WIDTH];
        for (int i = 0; i < GAME_WIDTH; i++) {
            array[i] = b;
        }
        return array;
    }
}
