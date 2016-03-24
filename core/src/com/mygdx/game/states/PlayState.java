package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.customEnum.MapTile;
import com.mygdx.game.customEnum.PowerType;
import com.mygdx.game.objects.Background;
import com.mygdx.game.objects.DangerZone;
import com.mygdx.game.objects.GameObject;
import com.mygdx.game.objects.Movable;
import com.mygdx.game.objects.Overlay;
import com.mygdx.game.objects.Door;
import com.mygdx.game.objects.Obstacle;
import com.mygdx.game.objects.Power;
import com.mygdx.game.objects.SideWall;
import com.mygdx.game.objects.Switch;
import com.mygdx.game.objects.JoyStick;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.UI;

import java.util.ArrayList;
import java.util.Collections;
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
    protected float gameSpeed, speedChange, speedIncrease, dangerZoneSpeedLimit, tempGameSpeed;
    protected int playerSpeed, dangerZone, powerCounter, doorCounter, score, scoreIncrement;
    boolean passivePowerState, passivePowerEffectTaken, activePowerState, activePowerEffectTaken;
    public float tracker;
    public float trackerBG;

    //boolean arrays
    public MapTile[] path = {MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY};
    boolean[] current = createArray(true);
    float[] doorSwitch = null;

    //Arraylists
    protected ArrayList<MapTile[]> mapBuffer = new ArrayList<MapTile[]>();

    private ArrayList<ArrayList<GameObject>> gameObjects = new ArrayList<ArrayList<GameObject>>();
    private ArrayList<GameObject> obstacles = new ArrayList<GameObject>();
    private ArrayList<GameObject> sideWalls = new ArrayList<GameObject>();
    private ArrayList<GameObject> switches = new ArrayList<GameObject>();
    private ArrayList<GameObject> doors = new ArrayList<GameObject>();
    private ArrayList<GameObject> powers = new ArrayList<GameObject>();
    private ArrayList<GameObject> bg = new ArrayList<GameObject>();
    private ArrayList<GameObject> effects = new ArrayList<GameObject>();
    private ArrayList<GameObject> dz = new ArrayList<GameObject>();
    private ArrayList<GameObject> ui = new ArrayList<GameObject>();

    //final values
    final int tileLength = 50;

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
        speedIncrease = (float) 0.07;
        speedChange = (float) 0.6;
        playerSpeed = 300;
        dangerZone = 200;
        powerCounter = 0;
        doorCounter = 0;
        dangerZoneSpeedLimit = 250;
        score = 0;
        scoreIncrement = 1;
        passivePowerState = passivePowerEffectTaken = activePowerState = activePowerEffectTaken = false;
        tracker = 800;
        trackerBG = 800;

        gameObjects.add(bg);
        gameObjects.add(powers);
        gameObjects.add(doors);
        gameObjects.add(obstacles);
        gameObjects.add(sideWalls);
        gameObjects.add(switches);
        gameObjects.add(new ArrayList<GameObject>(Collections.singletonList(player)));
        gameObjects.add(effects);
        gameObjects.add(dz);
        gameObjects.add(ui);

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
                //if this is the initial touch initialize the joystick at the touched location
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
            //calculates the relevant numbers needed for omnidirectional movement
            float angle = (float) Math.atan2(relativey, relativex);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            if (Math.abs(relativex) < joystick.getJoystickWidth()/2
                    && (Math.abs(relativey) < joystick.getJoystickHeight()/2)) {
                //if the touched position is within the circle set the dot to the touched position
                joystick.setCX(touchPos.x - joystick.getJoystickCenterWidth()/2);
                joystick.setCY(touchPos.y - joystick.getJoystickCenterHeight()/2);
            } else {
                //otherwise set it to the edge of the circle
                joystick.setCX(cos * joystick.getJoystickWidth()/2 + joystick.getX() + joystick.getJoystickWidth()/2 - joystick.getJoystickCenterWidth()/2);
                joystick.setCY(sin * joystick.getJoystickWidth()/2 + joystick.getY() + joystick.getJoystickHeight()/2 - joystick.getJoystickCenterHeight()/2);
            }
            //if the touch is within a specific distance from the centre of the circle then move the player
            if (Math.pow(relativex, 2) + Math.pow(relativey, 2) > 400) {
                omniMove(cos, sin);
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        handleInput();
        checkSwitchCollision();
        // tell the camera to update its matrices.
        while (tracker < 1050) {
            synchronized (this) {
                while (mapBuffer.size() == 0){
                    try {
                        wait();
                    } catch (InterruptedException ignored){}
                }
                path = mapBuffer.remove(0);
                notifyAll();
                score += scoreIncrement;
            }
            spawnObjects();
            spawnSides(tracker + tileLength);
            tracker += tileLength;
        }
        if (trackerBG < 800) {
            spawnBg();
            trackerBG += 800;
        }
        // tell the camera to update its matrices.
        cam.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        sb.setProjectionMatrix(cam.combined);

        // begin a new batch and draw the player and all objects
        sb.begin();
        draw(sb);

        if(touched){

            sb.draw(joystick.getJoystickImage(), joystick.getX(), joystick.getY());
            sb.draw(joystick.getJoystickCentreImage(), joystick.getCX(), joystick.getCY());
        }

        sb.end();

//		constantly check if any power/DangerZone's effect still lingers
        effectPassivePower();
        effectActivePower();
        effectDangerZone(player);

        tracker -= gameSpeed * Gdx.graphics.getDeltaTime();
        trackerBG -= gameSpeed * Gdx.graphics.getDeltaTime();

//      move the obstacles, remove any that are beneath the bottom edge of the screen.

        for (ArrayList<GameObject> gameObj: gameObjects){
            for (GameObject gameObject : gameObj) {
                if (gameObject instanceof Movable) {
                    ((Movable) gameObject).scroll(gameSpeed);
                }
            }
        }

    }
    @Override
    public void update(float dt) {
    }

    @Override
    public void dispose() {
        // dispose of all the native resources
        for (ArrayList<GameObject> gameObj: gameObjects) {
            for (GameObject gameObject : gameObj) {
                gameObject.getImage().dispose();
            }
        }
        joystick.getJoystickImage().dispose();
        joystick.getJoystickCentreImage().dispose();
    }

    /***************************************************
     *  CREATE METHODS HERE
     ****************************************************
     */

    private void createObstacle() {
        for (int i = 0; i < path.length; i++) {
            if (path[i] == MapTile.OBSTACLES) {
                Obstacle obstacle = new Obstacle((tileLength * i) + 15, 800, tileLength, tileLength);
                obstacles.add(obstacle);
            }
        }
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
        while (counter* tileLength <= 800) {
            for (int i = 0; i < 2; i++) {
                SideWall sideWall = new SideWall(tileLength, counter* tileLength, i);
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

    private void spawnObjects(){
        for (int i = 0; i < path.length; i++) {
            switch (path[i]){
                case SWITCH:
                    switches.add(new Switch((tileLength * (i % GAME_WIDTH) + 15), tracker, tileLength, tileLength));
                    break;
                case OBSTACLES:
                    obstacles.add(new Obstacle((tileLength * (i % GAME_WIDTH) + 15), tracker, tileLength, tileLength));
                    break;
                case POWER:
                    powers.add(new Power(PowerType.values()[(int)(Math.random() * (PowerType.values().length-1) + 1)], tileLength * (i % GAME_WIDTH) + 15, tracker, tileLength, tileLength));
                    break;
                case DOOR:
                    doors.add(new Door((tileLength * (i % GAME_WIDTH)) + 15, tracker, tileLength, tileLength));
                    break;
            }
        }
    }

    private void spawnBg(){
        Background backg = new Background(800);
        Overlay effect = new Overlay(800);
        bg.add(backg);
        effects.add(effect);
    }

    /**
     Spawn side walls to fill up the gap between the playing field and the actual maze
     */
    private void spawnSides(float in){
        for (int i = 0; i < 2; i++) {
            SideWall sideWall = new SideWall(tileLength,in,i);
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
            if (x > 0) x = (float)2/3;
            if (x < 0) x = -(float)2/3;
            if (y > 0) y = (float)2/3;
            if (y < 0) y = -(float)2/3;
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
        // DESTROY_WALL and GO_THROUGH_WALL implementation

        if (player.canGoThrough() && System.currentTimeMillis()<player.getEndPassivePowerTime()) {}
        else if (player.canGoThrough() && System.currentTimeMillis()>=player.getEndPassivePowerTime()) {
            player.setPassivePower(PowerType.NOTHING);
        } else {
    //		collides with normal wall obstacle
            for (GameObject obstacle : obstacles) {
                if (((Obstacle) obstacle).collides(player, this)) {
                    return true;
                }
            }
    //		collides with doors
            for (GameObject door : doors) {
                if (((Door)door).collides(player, this)) {
                    return true;
                }
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
        //		collides with switch
        for (GameObject eachSwitch:switches){
            if (((Switch) eachSwitch).collides(player, this)){
                for (GameObject door: doors){
                    ((Door)door).setOpen();
                }
                // then notify server
            }
        }

    //		collides with power up
        Iterator<GameObject> powerIterator = powers.iterator();
        while (powerIterator.hasNext()){
            if (((Power)powerIterator.next()).collides(player, this)){
                powerIterator.remove();
            }
        }
    }

    /**
     Methods handling power-ups/affecting game attributes
     */

    private void effectDangerZone(Player p) {
        if (p.getY()<=dangerZone && gameSpeed<=dangerZoneSpeedLimit) {
            gameSpeed += speedIncrease;
        }
    }

    private void effectPassivePower(){
        if (passivePowerState) {
            if (!passivePowerEffectTaken) {
                if (player.getPassivePower().equals(PowerType.FREEZE_MAZE)) {
                    tempGameSpeed = gameSpeed;
                    gameSpeed = 0;
                } else if (player.getPassivePower().equals(PowerType.DANGER_ZONE_HIGHER)) {
                    dangerZone += 20;
                    // change photo
                } else if (player.getPassivePower().equals(PowerType.SPEED_GAME_UP)) {
                    gameSpeed /= speedChange;
                } else if (player.getPassivePower().equals(PowerType.SPEED_PLAYER_UP)) {
                    playerSpeed /= speedChange;
                }
                passivePowerEffectTaken = true;
            }
            if (System.currentTimeMillis() >= player.getEndPassivePowerTime()) {
                if (player.getPassivePower().equals(PowerType.FREEZE_MAZE)) {
                    gameSpeed = tempGameSpeed;
                } else if (player.getPassivePower().equals(PowerType.DANGER_ZONE_HIGHER)) {
                    dangerZone -= 20;
                    // change photo
                } else if (player.getPassivePower().equals(PowerType.SPEED_GAME_UP)) {
                    gameSpeed *= speedChange;
                } else if (player.getPassivePower().equals(PowerType.SPEED_PLAYER_UP)) {
                    playerSpeed *= speedChange;
                }
                passivePowerState = false;
                passivePowerEffectTaken = false;
            }
        }
    }

    private void activateActivePower(){
        if (!player.getActivePower().equals(PowerType.NOTHING)) {
            activePowerState = true;
            player.setEndActivePowerTime(System.currentTimeMillis()+5000);
        }
    }

    private void effectActivePower(){
        if (activePowerState) {
            if (!activePowerEffectTaken) {
                if (player.getActivePower().equals(PowerType.DANGER_ZONE_LOWER)) {
                    dangerZone -= 20;
                }
                activePowerEffectTaken = true;
            }
            if (System.currentTimeMillis() >= player.getEndPassivePowerTime()) {
                if (player.getActivePower().equals(PowerType.DANGER_ZONE_LOWER)) {
                    dangerZone += 20;
                }
                player.setActivePower(PowerType.NOTHING);
                activePowerState = false;
                activePowerEffectTaken = false;
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

    public void draw(SpriteBatch sb){
        for (ArrayList<GameObject> gameObjList: gameObjects) {
            for (GameObject gameObj: gameObjList) {
                gameObj.draw(sb);
            }
        }
    }
}
