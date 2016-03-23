package com.mygdx.game.states;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Interface.Collidable;
import com.mygdx.game.customEnum.MapTile;
import com.mygdx.game.customEnum.PowerType;
import com.mygdx.game.objects.Background;
import com.mygdx.game.objects.DangerZone;
import com.mygdx.game.objects.GameObject;
import com.mygdx.game.objects.Movable;
import com.mygdx.game.objects.Overlay;
import com.mygdx.game.objects.Door;
import com.mygdx.game.objects.DoorOpen;
import com.mygdx.game.objects.Obstacle;
import com.mygdx.game.objects.Power;
import com.mygdx.game.objects.SideWall;
import com.mygdx.game.objects.Switch;
import com.mygdx.game.objects.JoyStick;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.UI;

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
    private long endPassivePowerTime, endActivePowerTime;
    protected float gameSpeed, speedChange, speedIncrease, dangerZoneSpeedLimit;
    protected int playerSpeed, dangerZone, powerCounter, doorCounter, score, scoreIncrement;
    boolean passivePowerState, passivePowerEffectTaken, activePowerState, activePowerEffectTaken;

    //boolean arrays
    public MapTile[] path = {MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY, MapTile.EMPTY};
    boolean[] current = createArray(true);
    float[] doorSwitch = null;

    //Arraylists
    protected ArrayList<MapTile[]> mapBuffer = new ArrayList<MapTile[]>();
    protected ArrayList<float[]> switchBuffer = new ArrayList<float[]>();

    private ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
    public static float tracker;
    public float trackerBG;

    //final values
    final int spriteWidth = 50;
    final int spriteHeight = 50;

    protected PlayState(GameStateManager gsm) {
        super(gsm);
        running = true;

        //camera initialization
        cam = new OrthographicCamera();
        cam.setToOrtho(false, 480, 800);

        //object initialization
        player = new Player();
        gameObjects.add(player);
        joystick = new JoyStick();

        //misc values initialization
        tracker = 800;
        trackerBG = 800;
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
        endPassivePowerTime = endActivePowerTime = System.currentTimeMillis();

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
        while (tracker < 1000) {
            synchronized (this) {
                while (mapBuffer.size() == 0){
                    try {
                        wait();
                    } catch (InterruptedException ignored){}
                }
                path = mapBuffer.remove(0);
                doorSwitch = switchBuffer.remove(0);
                notifyAll();
            }
            spawnObstacle(tracker);
            spawnPower();
            spawnSwitch();
            spawnDoor(tracker);
            spawnSides(tracker);
            tracker += spriteHeight;
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

        Iterator<GameObject> gameObjectIterator = gameObjects.iterator();
        while (gameObjectIterator.hasNext()) {
            GameObject gameObj = gameObjectIterator.next();
            gameObj.draw(sb);
            if (gameObj instanceof Movable){
                ((Movable) gameObj).scroll(gameSpeed);
                if (gameObj.y + spriteHeight < 0) gameObjectIterator.remove();
            }
        }
        tracker -= gameSpeed * Gdx.graphics.getDeltaTime();
        trackerBG -= gameSpeed * Gdx.graphics.getDeltaTime();

        if(touched){

            sb.draw(joystick.getJoystickImage(), joystick.getX(), joystick.getY());
            sb.draw(joystick.getJoystickCentreImage(), joystick.getCX(), joystick.getCY());
        }

        sb.end();

//		constantly check if any power/DangerZone's effect still lingers
        effectPassivePower();
        effectActivePower();
        effectDangerZone(player);
    }
    @Override
    public void update(float dt) {
    }

    @Override
    public void dispose() {
        // dispose of all the native resources
        for (GameObject gameObj:gameObjects) {
            gameObj.getImage().dispose();
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
                Obstacle obstacle = new Obstacle();
                obstacle.x = (spriteWidth * i) + 15;
                obstacle.y = 800;
                obstacle.width = spriteWidth;
                obstacle.height = spriteHeight;
                gameObjects.add(obstacle);
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
        gameObjects.add(backg);
        gameObjects.add(effect);
        gameObjects.add(danger);
        gameObjects.add(inter);
    }
    private void createSides(){
        int counter = 0;
        while (counter*spriteHeight <= 800) {
            for (int i = 0; i < 2; i++) {
                SideWall sideWall = new SideWall(spriteHeight, counter*spriteHeight, i);
                gameObjects.add(sideWall);
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
            if (path[i] == MapTile.OBSTACLES) {
                Obstacle obstacle = new Obstacle();
                obstacle.x = (spriteWidth * i) + 15;
                obstacle.y = in;
                obstacle.width = spriteWidth;
                obstacle.height = spriteHeight;
                gameObjects.add(obstacle);
            }
        }
        score += scoreIncrement;
        powerCounter += 1;
        doorCounter += 1;
    }
    private void spawnBg(){
        Background backg = new Background(800);
        Overlay effect = new Overlay(800);
        gameObjects.add(backg);
        gameObjects.add(effect);
    }
    private void spawnPower() {
        for (int i = 0; i < path.length; i++) {
            if (path[i] == MapTile.POWER) {
                Power power = new Power(PowerType.values()[(int)(Math.random()*PowerType.values().length)],i);
                gameObjects.add(power);
            }
        }
    }
    private void spawnSwitch(){
        if (this.doorSwitch[2] == 1) {
            Switch doorSwitch = new Switch(spriteWidth, spriteHeight, this.doorSwitch[0], this.doorSwitch[1]);
            gameObjects.add(doorSwitch);
            this.doorSwitch[2] = 0;
        }
    }
    private void spawnDoor(float in){
        for (int i = 0; i < path.length; i++) {
            if (path[i] == MapTile.DOOR) {
                Door door = new Door();
                door.x = (spriteWidth * i) + 15;
                door.y = in;
                door.width = spriteWidth;
                door.height = spriteHeight;
                gameObjects.add(door);
            }
        }
    }
    /**
     Spawn side walls to fill up the gap between the playing field and the actual maze
     */
    private void spawnSides(float in){
        for (int i = 0; i < 2; i++) {
            SideWall sideWall = new SideWall(spriteHeight,in,i);
            gameObjects.add(sideWall);
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

//		collide with normal wall obstacle
// 		collide with doors
        for (GameObject gameObj : gameObjects) {
            if (gameObj instanceof Collidable){
                if (((Collidable) gameObj).collide(player, this)){
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

//		collide with power up
        for (Power power:powers){
            if (player.overlaps(power)){
                if (isPassive(power)) {
                    player.setPassivePower(power.getType());
                    passivePowerState = true;
                    endPassivePowerTime = System.currentTimeMillis()+5000;
                }
                else player.setActivePower(power.getType());
                powers.remove(power);
            }
        }
    }


    private void removeBarriers(){
//		TODO: if notified by server (Ryan)
        doors.clear();
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

                passivePowerEffectTaken = true;
            }
            if (System.currentTimeMillis() >= endPassivePowerTime) {

                passivePowerState = false;
                passivePowerEffectTaken = false;
            }
        }
    }

    private void activateActivePower(){
        if (!player.getActivePower().equals("nothing")) {
            activePowerState = true;
            endActivePowerTime = System.currentTimeMillis()+5000;
        }
    }
    private void effectActivePower(){
        if (activePowerState) {
            if (!activePowerEffectTaken) {

                activePowerEffectTaken = true;
            }
            if (System.currentTimeMillis() >= endActivePowerTime) {

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

    protected boolean isPassive(Power newPower) {
        int index=0;
        for (int i=0; i<PowerType.values().length; i++) {
            if (newPower.getType().equals(PowerType.values()[i])) {
                index = i;
                break;
            }
        }
        return !(index<PowerType.values().length/2);
    }

    public ArrayList<GameObject> getGameObjects(){
        return gameObjects;
    }
}
