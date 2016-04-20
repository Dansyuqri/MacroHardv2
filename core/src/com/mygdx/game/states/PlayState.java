package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MacroHardv2;
import com.mygdx.game.customEnum.Direction;
import com.mygdx.game.customEnum.MapTile;
import com.mygdx.game.customEnum.MessageCode;
import com.mygdx.game.customEnum.PowerType;
import com.mygdx.game.customEnum.Stage;
import com.mygdx.game.customEnum.StateType;
import com.mygdx.game.objects.ActivePowerIcon;
import com.mygdx.game.objects.Background;
import com.mygdx.game.objects.Boulder;
import com.mygdx.game.objects.Fog;
import com.mygdx.game.objects.GameObject;
import com.mygdx.game.objects.Ghost;
import com.mygdx.game.objects.Hole;
import com.mygdx.game.objects.InnatePowerIcon;
import com.mygdx.game.objects.MagicCircle;
import com.mygdx.game.objects.Movable;
import com.mygdx.game.objects.Overlay;
import com.mygdx.game.objects.Door;
import com.mygdx.game.objects.Obstacle;
import com.mygdx.game.objects.PlayerEffect;
import com.mygdx.game.objects.Pointer;
import com.mygdx.game.objects.Power;
import com.mygdx.game.objects.Sand;
import com.mygdx.game.objects.SideWall;
import com.mygdx.game.objects.Spikes;
import com.mygdx.game.objects.Switch;
import com.mygdx.game.objects.JoyStick;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.Troll;
import com.mygdx.game.objects.UI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Syuqri on 3/7/2016.
 */
public abstract class PlayState extends State{

    //Synchronising
    public static final float deltaCap = 0.025f;
    protected boolean sync = false;

    //objects
    protected Semaphore mapPro;
    protected Semaphore mapCon;
    protected Semaphore mapMod;
    protected Semaphore seedSem;

    private JoyStick joystick;
    protected Player player;
    private Pointer pointer;
    private PlayerEffect playerEffects;
    private Vector3[] touchPos = {new Vector3(),new Vector3()};

    protected PlayerCoordinateSender coordSender;
    private MapSynchronizer mapSynchronizer;
    protected Random mapRandomizer;

    //values
    protected long seed, threadsleep;
    protected final int GAME_WIDTH = 9;
    protected final int playerID;
    private boolean running;

    private AtomicBoolean end = new AtomicBoolean(false);
    private boolean touchHeld, gotSwitch = false, onSwitch = false, onCircle = false, disconnectboolean = false;
    protected float gameSpeed, speedIncrease, dangerZoneSpeedLimit;
    protected AtomicInteger slowGameDown, freezeMaze, playerSpeed, score;
    protected final int dangerZone;
    public float tracker;
    public float trackerBG;
    BitmapFont yourBitmapFontName;
    public Stage stage, nextStage;
    float animateTime;
    float[] angle = new float[2];
    private int disconnectThreshold;

    //boolean arrays
    public MapTile[] path = createArray(MapTile.EMPTY);

    //Arraylists
    protected ArrayList<MapTile[]> mapBuffer = new ArrayList<MapTile[]>();

    private ArrayList<ArrayList<GameObject>> gameObjects = new ArrayList<ArrayList<GameObject>>();
    protected ArrayList<GameObject> players = new ArrayList<GameObject>(
            Arrays.asList(
                    new Player[]{
                            new Player(0), new Player(1)
                    }));
    protected ArrayList<GameObject> obstacles = new ArrayList<GameObject>();
    private ArrayList<GameObject> sideWalls = new ArrayList<GameObject>();
    protected ArrayList<GameObject> switches = new ArrayList<GameObject>();
    protected ArrayList<GameObject> doors = new ArrayList<GameObject>();
    protected ArrayList<GameObject> powers = new ArrayList<GameObject>();
    protected ArrayList<GameObject> spikes = new ArrayList<GameObject>();
    protected ArrayList<GameObject> holes = new ArrayList<GameObject>();
    private ArrayList<GameObject> bg = new ArrayList<GameObject>();
    private ArrayList<GameObject> effects = new ArrayList<GameObject>();
    private ArrayList<GameObject> ui = new ArrayList<GameObject>();
    private ArrayList<GameObject> icons = new ArrayList<GameObject>();
    protected ArrayList<GameObject> ghosts = new ArrayList<GameObject>();
    private ArrayList<GameObject> fogs = new ArrayList<GameObject>();
    private ArrayList<GameObject> sands = new ArrayList<GameObject>();
    private ArrayList<GameObject> boulders = new ArrayList<GameObject>();
    private ArrayList<GameObject> mCircles = new ArrayList<GameObject>();
    private ArrayList<GameObject> trolls = new ArrayList<GameObject>();

    //final values
    final int tileLength = 50;

    //map making objects
    private int doorCounter, powerCounter, spikeCounter, stageCounter;
    private ArrayList<MapTile[]> memory;
    private boolean[] current = createArray(true);
    protected MapMaker mapMaker;

    protected ScheduledThreadPoolExecutor backgroundTaskExecutor = new ScheduledThreadPoolExecutor(10);

    protected PlayState(GameStateManager gsm, int playerID) {
        super(gsm);
        Gdx.input.setCatchBackKey(true);
        this.playerID = playerID;
        player = (Player) players.get(playerID);
        pointer = new Pointer(playerID);
        playerEffects = new PlayerEffect(player);

        mapPro = new Semaphore(15);
        mapCon = new Semaphore(-4);
        mapMod = new Semaphore(1);
        seedSem = new Semaphore(0);

        touchHeld = false;

        //camera initialization
        cam = new OrthographicCamera(480,800);
        cam.setToOrtho(false, 480, 800);

        //object initialization
        joystick = new JoyStick();

        //resetting counters
        Door.reset();
        Hole.reset();
        Obstacle.reset();
        Switch.reset();
        MagicCircle.reset();
        Boulder.reset();

        //misc values initialization
        threadsleep = 25;
        gameSpeed = 75;
        speedIncrease = (float) 0.07;
        playerSpeed = new AtomicInteger(200);
        dangerZone = 350;
        slowGameDown = new AtomicInteger(1);
        freezeMaze = new AtomicInteger(1);
        doorCounter = 0;
        powerCounter = 0;
        spikeCounter = 0;
        dangerZoneSpeedLimit = 250;
        stage = Stage.DUNGEON;
        nextStage = Stage.DUNGEON;
        tracker = 800;
        trackerBG = 800;
        score = new AtomicInteger(0);
        yourBitmapFontName = new BitmapFont();
        animateTime = 0f;

        gameObjects.add(bg);
        gameObjects.add(spikes);
        gameObjects.add(holes);
        gameObjects.add(sands);
        gameObjects.add(powers);
        gameObjects.add(doors);
        gameObjects.add(obstacles);
        gameObjects.add(sideWalls);
        gameObjects.add(switches);
        gameObjects.add(mCircles);
        gameObjects.add(boulders);
        gameObjects.add(players);
        gameObjects.add(ghosts);
        gameObjects.add(trolls);
        gameObjects.add(fogs);
        gameObjects.add(effects);
        gameObjects.add(ui);
        gameObjects.add(icons);

        memory = new ArrayList<MapTile[]>();
        MapTile[] init = createArray(MapTile.EMPTY);
        for (int i = 0; i < 5; i++){
            memory.add(init);
        }

        mapMaker = new MapMaker(this);
        mapMaker.start();

        createBg();
        createObstacle();
        createSides();

        mapSynchronizer = new MapSynchronizer();

        coordSender = new PlayerCoordinateSender(this);
        running = false;

        addIcon(new InnatePowerIcon(player.getInnatePower()));
    }
    @Override
    protected void handleInput() {
        boolean hasMoved = false;
        for (int i = 0; i < 2; i++) {
            if(Gdx.input.isTouched(i)) {
                touchPos[i].set(Gdx.input.getX(i), Gdx.input.getY(i), 0);
                cam.unproject(touchPos[i]);

                float relativex = touchPos[i].x - (joystick.getX());
                float relativey = touchPos[i].y - (joystick.getY());

                if ((touchHeld ||
                        (Math.abs(relativex) < joystick.getJoystickWidth() / 2 &&
                                Math.abs(relativey) < joystick.getJoystickWidth() / 2)) && !hasMoved) {
                    touchHeld = true;
                    hasMoved = true;

                    //calculates the relevant numbers needed for omnidirectional movement
                    angle[playerID] = (float) Math.atan2(relativey, relativex);
                    float cos = (float) Math.cos(angle[playerID]);
                    float sin = (float) Math.sin(angle[playerID]);
                    float ratio;
                    //setting joystick centre coordinates
                    if ((Math.abs(relativex) < joystick.getJoystickWidth() / 2 &&
                            Math.abs(relativey) < joystick.getJoystickWidth() / 2)) {
                        ratio = (float) ((Math.pow(relativex, 2) + Math.pow(relativey, 2)) / Math.pow(joystick.getJoystickWidth() / 2, 2));
                        joystick.setCX(touchPos[i].x - joystick.getJoystickCenterWidth() / 2);
                        joystick.setCY(touchPos[i].y - joystick.getJoystickCenterHeight() / 2);
                    } else {
                        ratio = 1;
                        joystick.setCX(joystick.getX() - joystick.getJoystickCenterWidth() / 2 + joystick.getJoystickWidth() / 2 * cos);
                        joystick.setCY(joystick.getY() - joystick.getJoystickCenterHeight() / 2 + joystick.getJoystickWidth() / 2 * sin);
                    }

                    movePlayer(cos, sin, (float) Math.pow(ratio, 0.5));
                }

                Iterator<GameObject> iconIterator = icons.iterator();
                while (iconIterator.hasNext()) {
                    GameObject tempIcon = iconIterator.next();
                    if (tempIcon.contains(touchPos[i].x, touchPos[i].y)) {
                        if (tempIcon instanceof ActivePowerIcon) {
                            activateActivePower();
                            iconIterator.remove();
                        } else if (tempIcon instanceof InnatePowerIcon) {
                            if (((InnatePowerIcon) tempIcon).isAvailable()) {
                                activateInnatePower();
                            }
                        }
                    }
                }
            }
        }

        if (!Gdx.input.isTouched()){
            touchHeld = false;
            joystick.setCX(joystick.getX() - joystick.getJoystickCenterWidth() / 2);
            joystick.setCY(joystick.getY() - joystick.getJoystickCenterHeight()/2);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        mapSynchronizer.updateSyncRender();
        if (mapSynchronizer.getSyncTele()) {
            if (!checkObstacleCollisionTeleport()) {
                mapSynchronizer.setSyncTele(false);
            }
        }
        long start = System.currentTimeMillis();
        //Host
        if(!sync){
            sync = true;
            mapSynchronizer.sync();
        }


        if (!running){
            running = true;
            backgroundTaskExecutor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    coordSender.send();
                    mapSynchronizer.sendSyncRender();
                }
            }, 0, 1, TimeUnit.MILLISECONDS);
            backgroundTaskExecutor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 2; i++) {
                        if (((Player) players.get(i)).x != ((Player) players.get(i)).getPrev_x() ||
                                Math.abs(((Player) players.get(i)).y - (((Player) players.get(i)).getPrev_y() - gameSpeed / slowGameDown.get() * freezeMaze.get() * deltaCap)) > 5) {
                            if (angle[i] > 3 * (Math.PI) / 8 && angle[i] <= 5 * (Math.PI) / 8) {
                                ((Player) players.get(i)).setOrientation(Direction.NORTH);
                            } else if (angle[i] > 7 * (Math.PI) / 8 || angle[i] <= -7 * (Math.PI) / 8) {
                                ((Player) players.get(i)).setOrientation(Direction.WEST);
                            } else if (angle[i] < -3 * (Math.PI) / 8 && angle[i] >= -5 * (Math.PI) / 8) {
                                ((Player) players.get(i)).setOrientation(Direction.SOUTH);
                            } else if (angle[i] > -(Math.PI) / 8 && angle[i] <= (Math.PI) / 8) {
                                ((Player) players.get(i)).setOrientation(Direction.EAST);
                            } else if (angle[i] > (Math.PI) / 8 && angle[i] <= 3 * (Math.PI) / 8) {
                                ((Player) players.get(i)).setOrientation(Direction.NORTHEAST);
                            } else if (angle[i] > 5 * (Math.PI) / 8 && angle[i] <= 7 * (Math.PI) / 8) {
                                ((Player) players.get(i)).setOrientation(Direction.NORTHWEST);
                            } else if (angle[i] > -3 * (Math.PI) / 8 && angle[i] <= -(Math.PI) / 8) {
                                ((Player) players.get(i)).setOrientation(Direction.SOUTHEAST);
                            } else if (angle[i] > -7 * (Math.PI) / 8 && angle[i] <= -5 * (Math.PI) / 8) {
                                ((Player) players.get(i)).setOrientation(Direction.SOUTHWEST);
                            }
                        }
                    }
                }
            }, 0, 30, TimeUnit.MILLISECONDS);

        }

        handleInput();

        while (tracker < 1000) {
            try {
                mapCon.acquire();
                mapMod.acquire();
                path = mapBuffer.remove(0);
                mapMod.release();
                mapPro.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            score.getAndIncrement();
            spawnObjects();
            spawnSides(tracker + tileLength);
            tracker += tileLength;
        }

        if (trackerBG <= 1000) {
            spawnBg();
            trackerBG += 200;
        }
        // tell the camera to update its matrices.
        cam.update();
        // tell the SpriteBatch to render in the coordinate system specified by the camera.
        sb.setProjectionMatrix(cam.combined);

        // begin a new batch and draw the player and all objects
        sb.begin();
        draw(sb);
        sb.draw(joystick.getJoystickCentreImage(), joystick.getCX(), joystick.getCY());
        yourBitmapFontName.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        yourBitmapFontName.draw(sb, "score: " + score, 350, 20);



        sb.end();

        checkSwitchCollision();
        checkBoundaryCollision();
        checkFatalCollision();

        checkDangerZone(player);

        tracker -= gameSpeed / slowGameDown.get() * freezeMaze.get() * deltaCap;
        trackerBG -= gameSpeed / slowGameDown.get() * freezeMaze.get()  * deltaCap;

        for (ArrayList<GameObject> gameObj: gameObjects){
            for (GameObject gameObject : gameObj) {
                if (gameObject instanceof Movable) {
                    synchronized ((Object) gameSpeed) {
                        ((Movable) gameObject).scroll(gameSpeed / slowGameDown.get() * freezeMaze.get());
                    }
                }
            }
        }

        pointer.set(player);
        playerEffects.set(player);

        for (GameObject icon:icons) {
            if (icon instanceof InnatePowerIcon) {
                ((InnatePowerIcon) icon).refreshIcon();
            }
        }

        mapSynchronizer.scroll(gameSpeed / slowGameDown.get() * freezeMaze.get() * deltaCap);

        if (end.get()) {
            goToRestartState();
        }

        long time = System.currentTimeMillis() - start;
        if(mapSynchronizer.getMyRender()>mapSynchronizer.getOtherRender()){
            threadsleep = 25 + 3*(mapSynchronizer.getMyRender()-mapSynchronizer.getOtherRender());
            if (threadsleep > 200){
                threadsleep = 200;
            }
        }
        else{
            threadsleep = 25;
        }


        if (time < threadsleep){
            try {
                Thread.currentThread().sleep(threadsleep - time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(disconnectboolean == false){
            disconnectThreshold++;
            if (disconnectThreshold > 40){
                goToRestartState();
            }
        }
        else{
            disconnectThreshold = 0;
        }
        disconnectboolean = false;

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
                Obstacle obstacle = new Obstacle((tileLength * i) + 15, 1000, tileLength, tileLength, stage);
                obstacles.add(obstacle);
            }
        }
    }

    private void createBg(){
        int counter = 0;
        while (counter*200 < 800) {
            Background backg = new Background(counter*200, stage);
            Overlay effect = new Overlay(counter*200, stage);
            bg.add(backg);
            effects.add(effect);
            counter++;
        }
        UI inter = new UI(0);
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
                    switches.add(new Switch((tileLength * (i % GAME_WIDTH) + 15), tracker, tileLength, tileLength, stage));
                    break;
                case OBSTACLES:
                    obstacles.add(new Obstacle((tileLength * (i % GAME_WIDTH) + 15), tracker, tileLength, tileLength, stage));
                    break;
                case POWER:
                    powers.add(new Power(PowerType.values()[(int)(Math.random() * (PowerType.values().length-3) + 1)], tileLength * (i % GAME_WIDTH) + 15, tracker, tileLength, tileLength));
                    break;
                case DOOR:
                    doors.add(new Door((tileLength * (i % GAME_WIDTH)) + 15, tracker, tileLength, tileLength, stage));
                    break;
                case SPIKES:
                    if (score.get() > 100 && stage == Stage.DUNGEON) {
                        spikes.add(new Spikes((tileLength * (i % GAME_WIDTH)) + 20, tracker + 5, 40, 40));
                    }
                    break;
                case HOLE:
                    if (stage == Stage.ICE){
                        holes.add(new Hole((tileLength * (i % GAME_WIDTH)) + 15, tracker, tileLength, tileLength,stage));
                    }
                    break;
                case SAND:
                    if (stage == Stage.DESERT){
                        sands.add(new Sand((tileLength * (i % GAME_WIDTH)) + 15, tracker, tileLength, tileLength));
                    }
                    break;
                case BOULDER:
                    if (stage == Stage.DESERT){
                        boulders.add(new Boulder((tileLength * (i % GAME_WIDTH)) + 15, tracker, 50, 50, stage));
                    }
                    break;
                case M_CIRCLE:
                    if (stage == Stage.DESERT){
                        mCircles.add(new MagicCircle((tileLength * (i % GAME_WIDTH)) + 15, tracker, tileLength, tileLength, stage));
                    }
                    break;
            }
        }
        if (score.get() > 150) {
            if (stage == Stage.DUNGEON || stage == Stage.DESERT) {
                if (score.get() % 15 == 0) {
                    ghosts.add(new Ghost((tileLength * 9) + 15, tracker + 2, 46, 46, stage));
                }
            }
            if (stage == Stage.ICE){
                if (score.get() % 10 == 0) {
                    trolls.add(new Troll((tileLength * mapRandomizer.nextInt(9)) + 15, tracker, tileLength, tileLength));
                }
            }
        }
    }

    private void spawnBg(){
        Background backg = new Background(trackerBG, stage);
        Overlay effect = new Overlay(trackerBG, stage);
        bg.add(backg);
        effects.add(effect);
        if (stage == Stage.ICE){
            gsm.startMusic("Howling Wind.mp3", (float) 1);
            Fog fog = new Fog(0,trackerBG);
            fogs.add(fog);
        }
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
    private void movePlayer(float x, float y, float ratio){
        synchronized (Player.class) {
            float prevx = player.x;
            float prevy = player.y;
            player.x += ratio * x * playerSpeed.get() * deltaCap;
            player.y += ratio * y * playerSpeed.get() * deltaCap;
            if (checkObstacleCollision()) {
                player.x = prevx;
                player.y = prevy;

                if (x > 0) {
                    player.x += ratio * playerSpeed.get() * deltaCap;
                } else {
                    player.x -= ratio * playerSpeed.get() * deltaCap;
                }
                if (checkObstacleCollision()) {
                    player.x = prevx;
                }
                if (y > 0) {
                    player.y += ratio * playerSpeed.get() * deltaCap;
                } else {
                    player.y -= ratio * playerSpeed.get() * deltaCap;
                }
                if (checkObstacleCollision()) {
                    player.y = prevy;
                }

                if (player.x > 465 - player.width) {
                    player.x = 465 - player.height;
                }

                if (player.x < 15) {
                    player.x = 15;
                }

                if (player.y > 750) {
                    player.y = 750;
                }
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

    private void checkBoundaryCollision(){
        // collision with screen boundaries
        synchronized (Player.class) {
            if (player.x > 465 - player.width) {
                player.x = 465 - player.height;
            }

            if (player.x < 15) {
                player.x = 15;
            }

            if (player.y > 750) {
                player.y = 750;
            }
        }

        if (player.y < 150){
            goToRestartState();
        }
    }
    private boolean checkObstacleCollision(){

//    		collides with normal wall obstacle
        Iterator<GameObject> obstacleIterator = obstacles.iterator();
        while (obstacleIterator.hasNext()){
            Obstacle obstacle = (Obstacle)obstacleIterator.next();
            if (obstacle.collides(player, this) || obstacle.isDestroyed()){
//              DESTROY_WALL implementation
                if (player.getCanDestroy()) {
                    obstacle.setToDestroy(true);
                    if (!obstacle.isDestroyed()){
                        mapSynchronizer.sendMessage(MessageCode.DESTROY_WALL, obstacle.getId());
                    }
                    gsm.startMusic("WallDestroySound.wav", (float) 1);
                }
                return true;
            }
        }
        //		collides with doors
        Iterator<GameObject> doorIterator = doors.iterator();
        while (doorIterator.hasNext()){
            Door door = (Door)doorIterator.next();
            if (door.collides(player, this) || door.isDestroyed()){
//              DESTROY_WALL implementation
                if (player.getCanDestroy() || door.isDestroyed()) {
                    if (!door.isDestroyed()){
                        mapSynchronizer.sendMessage(MessageCode.DESTROY_DOOR, door.getId());
                    }
                    doorIterator.remove();
                    break;
                }
                return true;
            }
        }

        // collides with boulders
        Iterator<GameObject> boulderIterator = boulders.iterator();
        while (boulderIterator.hasNext()){
            Boulder boulder = (Boulder)boulderIterator.next();
            if (boulder.collides(player, this) || boulder.isDestroyed()){
//              DESTROY_WALL implementation
                if (player.getCanDestroy()) {
                    boulder.setToDestroy(true);
                    if (!boulder.isDestroyed()){
                        mapSynchronizer.sendMessage(MessageCode.DESTROY_BOULDER, boulder.getId());
                    }
                    gsm.startMusic("WallDestroySound.wav", (float) 1);
                }
                return true;
            }
        }

        return false;
    }

    private boolean checkObstacleCollisionTeleport(){

//    		collides with normal wall obstacle
        synchronized (Player.class) {
            for (GameObject obstacle1 : obstacles) {
                Obstacle obstacle = (Obstacle) obstacle1;
                if (obstacle.collides(player, this)) {
                    mapSynchronizer.syncTele(player, obstacle);
                    return true;
                }
            }
            //		collides with doors
            for (GameObject door1 : doors) {
                Door door = (Door) door1;
                if (door.collides(player, this)) {
                    mapSynchronizer.syncTele(player, door);
                    return true;
                }
            }

            // collides with boulders
            for (GameObject boulder1 : boulders) {
                Boulder boulder = (Boulder) boulder1;
                if (boulder.collides(player, this)) {
                    mapSynchronizer.syncTele(player, boulder);
                    return true;
                }
            }
        }
        return false;
    }

    private void checkFatalCollision(){
        if (!player.getIsInvicible()) {
            //      collide with spikes
            for (GameObject spike : spikes) {
                if (((Spikes) spike).collides(player, this)) {
                    goToRestartState();
                }
            }

            //      collide with ghosts/mummies
            for (GameObject ghost : ghosts) {
                if (((Ghost) ghost).collides(player, this)) {
                    goToRestartState();
                }
            }

            // collides with holes
            for (GameObject hole1 : holes) {
                final Hole hole = ((Hole) hole1);
                if (hole.collides(player, this)) {
                    if (hole.isBroken()) {
                        goToRestartState();
                    } else if (!hole.isBreakHole()) {
                        gsm.startMusic("IceBreak.mp3", (float) 1);
                        hole.setBreakHole(true);
                        mapSynchronizer.sendMessage(MessageCode.BREAK_HOLE, hole.getId());
                    }
                }

                //            if (hole.isBroken()) {
                //                hole.setBroken();
                //                mapSynchronizer.sendMessage(MessageCode.DESTROY_WALL, hole.x + tileLength / 2, hole.y + tileLength / 2);
                //            }
            }
            // collide with trolls
            for (GameObject troll: trolls){
                if (((Troll) troll).collides(player, this)){
                    goToRestartState();
                }
            }
        }
    }

    /**
     Method to calculate and randomize the wall, power, switch etc. placement. It keeps randomly
     generating a sequence until it fulfils the condition where the player can reach to the next
     layer of walls. I.e, no dead ends. Also ensures that the power is spawned on a 'box' not
     occupied by a wall and that switches are reachable.
     */

    private void checkSwitchCollision(){
        //		collides with switch

        boolean open = false;
        for (GameObject eachSwitch:switches){
            if (((Switch) eachSwitch).collides(player, this)){
                open = true;
                if (!onSwitch) {
                    gsm.startMusic("GateSound.wav", (float) 3);
                    MacroHardv2.actionResolver.sendReliable(new byte[]{MessageCode.OPEN_DOORS, (byte) ((Switch) eachSwitch).getId()});
                }
                ((Switch) eachSwitch).setOn();
            } else {
                ((Switch) eachSwitch).setOff();
            }
        }

        if (!open && onSwitch) {
            MacroHardv2.actionResolver.sendReliable(new byte[]{MessageCode.CLOSE_DOORS});
        }

        onSwitch = open;

        for (GameObject eachSwitch:switches) {
            if (((Switch) eachSwitch).isOtherOn()) {
                ((Switch) eachSwitch).setOn();
                open = true;
            }
        }

        if (open) {
            for (GameObject door : doors) {
                ((Door) door).setOpen();
            }
        } else {
            for (GameObject door: doors){
                ((Door)door).setClose();
            }
        }

        //		collides with power up
        Iterator<GameObject> powerIterator = powers.iterator();
        while (powerIterator.hasNext()) {
            Power tempPower = (Power) powerIterator.next();
            if (tempPower.collides(player, this)) {
                gsm.startMusic("PowerUpSound.wav",(float)0.5);
                synchronized (Player.class) {
                    player.setActivePower(tempPower.getType());
                }
                this.addIcon(new ActivePowerIcon(tempPower.getType()));
                powerIterator.remove();
            }
        }

        // collides with quicksand
        Iterator<GameObject> sandIterator = sands.iterator();
        boolean sandCollide = false;
        while (sandIterator.hasNext()){
            Sand quickSand = (Sand) sandIterator.next();
            if (quickSand.collides(player, this)){
                if (!player.isSlowed()){
                    synchronized (Player.class) {
                        playerSpeed.getAndSet(playerSpeed.get() - 150);
                        player.setIsSlowed(true);
                    }
                    sandCollide = true;
                }
            }
        }
        if (!sandCollide && player.isSlowed()){
            synchronized (Player.class) {
                playerSpeed.getAndSet(playerSpeed.get() + 150);
                player.setIsSlowed(false);
            }
        }

        // collides with magic circle

        boolean onOneCircle = false;
        for (GameObject eachCircle: mCircles){
            boolean stepped = false;
            if (((MagicCircle)eachCircle).collides(player, this) && !onOneCircle){
                stepped = true;
                onOneCircle = true;
            }

            if (stepped && !((MagicCircle)eachCircle).isOn()){
                MacroHardv2.actionResolver.sendReliable(
                        new byte[]{MessageCode.MAGIC_CIRCLE_ON, (byte) ((MagicCircle) eachCircle).getId()});
            }

            if (!stepped && ((MagicCircle)eachCircle).isOn() && !((MagicCircle)eachCircle).getOtherOn()){
                MacroHardv2.actionResolver.sendReliable(
                        new byte[]{MessageCode.MAGIC_CIRCLE_OFF, (byte) ((MagicCircle)eachCircle).getId()});
            }

            if (stepped || ((MagicCircle) eachCircle).getOtherOn()) {
                ((MagicCircle) eachCircle).setOn();
            } else {
                ((MagicCircle) eachCircle).setOff();
            }
        }

        //checking circles
        boolean desBoulder = true;
        for (GameObject eachCircle: mCircles){
            if (!((MagicCircle)eachCircle).isOn()){
                desBoulder = false;
            }
        }
        if (desBoulder){
            for (GameObject eachBoulder: boulders){
                ((Boulder)eachBoulder).setToDestroy(true);
                mapSynchronizer.sendMessage(MessageCode.DESTROY_BOULDER, ((Boulder) eachBoulder).getId());
            }
        }
    }

    /**
     Methods handling power-ups/affecting game attributes
     */

    private void checkDangerZone(Player p) {
        if (p.getY()<=dangerZone && gameSpeed <=dangerZoneSpeedLimit) {
            if (!(player.getActivePower().equals(PowerType.FREEZE_MAZE) || player.getActivePower().equals(PowerType.SLOW_GAME_DOWN))) {
                synchronized ((Object) gameSpeed ) {
                    gameSpeed += speedIncrease;
                    sendGameSpeed();
                }
            }
        }
    }

    private void activateActivePower(){
        switch (player.getActivePower()) {
            case FREEZE_MAZE:
                freezeMaze.compareAndSet(1, 0);
                MacroHardv2.actionResolver.sendReliable(sendFreeze(freezeMaze.get()));
                backgroundTaskExecutor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        player.setActivePower(PowerType.NOTHING);
                        freezeMaze.compareAndSet(0, 1);
                        MacroHardv2.actionResolver.sendReliable(sendFreeze(freezeMaze.get()));
                    }
                }, 3, TimeUnit.SECONDS);
                break;
            case SPEED_PLAYER_UP:
                synchronized (Player.class) {
                    playerSpeed.getAndSet(playerSpeed.get() + 50);
                    backgroundTaskExecutor.schedule(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (Player.class) {
                                player.setActivePower(PowerType.NOTHING);
                                playerSpeed.getAndSet(playerSpeed.get() - 50);
                            }
                        }
                    }, 7, TimeUnit.SECONDS);
                }
                break;
            case SLOW_GAME_DOWN:
                gsm.pauseMusic("Dance Of Death.mp3");
                gsm.startMusic("TimeSlowSound.wav", (float) 3);
                slowGameDown.compareAndSet(1, 2);
                MacroHardv2.actionResolver.sendReliable(sendSlow(slowGameDown.get()));
                backgroundTaskExecutor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        gsm.startMusic("Dance Of Death.mp3", (float) 0.1);
                        synchronized (Player.class) {
                            player.setActivePower(PowerType.NOTHING);
                        }
                        slowGameDown.compareAndSet(2, 1);
                        MacroHardv2.actionResolver.sendReliable(sendSlow(slowGameDown.get()));
                    }
                }, 5, TimeUnit.SECONDS);
                break;
            case INVINCIBLE:
                synchronized (Player.class) {
                    player.setIsInvicible(true);
                    player.setActivePower(PowerType.NOTHING);
                }
                backgroundTaskExecutor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (Player.class) {
                            player.setIsInvicible(false);
                        }
                    }
                }, 4, TimeUnit.SECONDS);
                break;
        }
    }

    private void activateInnatePower(){
        switch (player.getInnatePower()) {
            case DESTROY_WALL:
                changeInnatePowerIcon(false);
                synchronized (Player.class) {
                    player.setCanDestroy(true);
                }
                backgroundTaskExecutor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (Player.class) {
                            player.setCanDestroy(false);
                        }
                    }
                }, player.effectTime, TimeUnit.SECONDS);
                backgroundTaskExecutor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (Player.class) {
                            player.setInnatePower(PowerType.DESTROY_WALL);
                        }
                        changeInnatePowerIcon(true);
                    }
                }, player.coolDown, TimeUnit.SECONDS);
                break;
            case TELEPORT:
                changeInnatePowerIcon(false);
                synchronized (Player.class) {
                    player.setInnatePower(PowerType.NOTHING);
                }
                byte[] message = sendTeleport(playerID, player.x, player.y);
                MacroHardv2.actionResolver.sendReliable(message);
                backgroundTaskExecutor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (Player.class) {
                            player.setInnatePower(PowerType.TELEPORT);
                        }
                        changeInnatePowerIcon(true);
                    }
                }, player.coolDown, TimeUnit.SECONDS);
                break;
        }
    }

    protected MapTile[] createArray(MapTile m){
        MapTile[] array = new MapTile[GAME_WIDTH];
        for (int i = 0; i < GAME_WIDTH; i++) {
            array[i] = m;
        }
        return array;
    }

    protected boolean[] createArray(boolean b){
        boolean[] array = new boolean[GAME_WIDTH];
        for (int i = 0; i < GAME_WIDTH; i++) {
            array[i] = b;
        }
        return array;
    }

    public void draw(SpriteBatch sb){
        animateTime += Gdx.graphics.getDeltaTime();
        for (ArrayList<GameObject> gameObjList: gameObjects) {
            Iterator<GameObject> gameObjectIterator = gameObjList.iterator();
            while (gameObjectIterator.hasNext()){
                GameObject gameObject = gameObjectIterator.next();
                if (gameObject.y < -gameObject.height){
                    if (!(gameObject instanceof Player)) {
                        gameObjectIterator.remove();
                        continue;
                    }
                }
                if (gameObject instanceof Player){
                    if (((Player)gameObject).x != ((Player)gameObject).getPrev_x() ||
                            Math.abs(((Player)gameObject).y - (((Player)gameObject).getPrev_y() - gameSpeed / slowGameDown.get() * freezeMaze.get()  * deltaCap)) > 5 ){
                        ((Player) gameObject).setCurrentFrame(animateTime, true);
                    } else {
                        synchronized (Player.class) {
                            ((Player) gameObject).setDirection();
                        }
                    }
                    synchronized (Player.class) {
                        ((Player) gameObject).setPrevCoord(((Player) gameObject).x, ((Player) gameObject).y);
                    }
                }

                else if (gameObject instanceof Obstacle && ((Obstacle)gameObject).isToDestroy()){
                    ((Obstacle)gameObject).setWallDestroyTime(((Obstacle) gameObject).getWallDestroyTime() + Gdx.graphics.getDeltaTime());
                    ((Obstacle)gameObject).setCurrentFrame(((Obstacle) gameObject).getWallDestroyTime(), true);
                    if (((Obstacle)gameObject).getWallDestroyTime() > 0.4){
                        ((Obstacle)gameObject).setDestroyed(true);
                        gameObjectIterator.remove();
                    }
                }

                if (gameObject instanceof Troll) {
                    ((Troll) gameObject).setCurrentFrame(animateTime, true);
                    if (((Troll) gameObject).collides(obstacles, this)){
                        gsm.startMusic("WallDestroySound.wav", (float) 1);
                    }
                }

                else if (gameObject instanceof Hole && ((Hole)gameObject).isBreakHole() && !((Hole)gameObject).isBroken()) {
                    ((Hole) gameObject).setHoleDestroyTime(((Hole) gameObject).getHoleDestroyTime() + Gdx.graphics.getDeltaTime());
                    ((Hole)gameObject).setCurrentFrame(((Hole) gameObject).getHoleDestroyTime(), true);
                    if (((Hole)gameObject).getHoleDestroyTime() > 2.9){
                        ((Hole)gameObject).setBroken();
                    }
                }

                else if (gameObject instanceof Boulder && ((Boulder)gameObject).isToDestroy()){
                    ((Boulder)gameObject).setBoulderDestroyTime(((Boulder) gameObject).getBoulderDestroyTime() + Gdx.graphics.getDeltaTime());
                    ((Boulder)gameObject).setCurrentFrame(((Boulder) gameObject).getBoulderDestroyTime(), true);
                    if (((Boulder)gameObject).getBoulderDestroyTime() > 0.4){
                        ((Boulder)gameObject).setDestroyed(true);
                        gameObjectIterator.remove();
                    }
                }
                gameObject.draw(sb);
            }
        }
        pointer.draw(sb);
        playerEffects.draw(sb);
    }

    public void addIcon(GameObject icon) {
        icons.add(icon);
    }
    public void changeInnatePowerIcon(boolean available) {
        for (GameObject icon:icons) {
            if (icon instanceof InnatePowerIcon) {
                ((InnatePowerIcon) icon).setAvailable(available);
            }
        }
    }

    private void sendGameSpeed(){
        byte[] message = new byte[3];
        message[0] = MessageCode.CHANGE_GAME_SPEED;
        message[1] = (byte) (gameSpeed / 10);
        message[2] = (byte) ((gameSpeed * 10) % 100);
        MacroHardv2.actionResolver.sendReliable(message);
    }

    public void update(byte[] message) {
        //update player coordinates
        if(message != null){
            switch(message[0]) {

                //other player's coordinates
                case MessageCode.PLAYER_POSITION:
                    disconnectboolean=true;
                    int other = (int) message[1];
                    float x = (float) message[2] * 10 + (float) message[3] / 10;
                    float y = (float) message[4] * 10 + (float) message[5] / 10;
                    players.get(other).x = x;
                    players.get(other).y = y;
                    angle[other] = ((float)Math.atan2(y - ((Player)players.get(other)).getPrev_y(), x - ((Player)players.get(other)).getPrev_x()));
                    break;

                //map
                case MessageCode.MAP_SEED:
                    byte[] seedStringBytes = new byte[message.length - 1];
                    System.arraycopy(message, 1, seedStringBytes, 0, message.length - 1);
                    String seedString = new String(seedStringBytes);
                    seed = Long.decode(seedString);
                    mapRandomizer = new Random(seed);
                    seedSem.release();
                    break;

                //open doors
                case MessageCode.OPEN_DOORS:
                    gsm.startMusic("GateSound.wav", (float) 3);
                    for (GameObject swi: switches) {
                        if (((Switch)swi).getId() == message[1]){
                            ((Switch)swi).setOtherOn();
                        }
                    }
                    break;

                //close doors
                case MessageCode.CLOSE_DOORS:
                    for (GameObject swi: switches) {
                        ((Switch)swi).setOtherOff();
                    }
                    break;

                case MessageCode.MAGIC_CIRCLE_ON:
                    synchronized (MagicCircle.class) {
                        for (GameObject magiccircle: mCircles) {
                            if (((MagicCircle)magiccircle).getId() == message[1]){
                                ((MagicCircle)magiccircle).OtherOn();
                            }
                        }
                    }
                    break;

                case MessageCode.MAGIC_CIRCLE_OFF:
                    synchronized (MagicCircle.class) {
                        for (GameObject magiccircle: mCircles) {
                            if (((MagicCircle)magiccircle).getId() == message[1]){
                                ((MagicCircle)magiccircle).OtherOff();
                            }
                        }

                    }
                    break;

                //game speed update
                case MessageCode.CHANGE_GAME_SPEED:
                    synchronized ((Object) gameSpeed) {
                        gameSpeed = ((float) message[1] * 10 + (float) message[2] / 10);
                    }
                    break;

                //end game
                case MessageCode.END_GAME:
                    end = new AtomicBoolean(true);
                    MacroHardv2.actionResolver.sendReliable(new byte[]{MessageCode.END_GAME});
                    if (message.length > 1) {
                        byte[] scoreBytes = new byte[message.length - 1];
                        System.arraycopy(message, 1, scoreBytes, 0, scoreBytes.length);
                        score = new AtomicInteger(Integer.decode(new String(scoreBytes)));
                    }
                    break;
                case MessageCode.PLAYERSTART:
                    byte[] syncBytes = new byte[message.length - 1];
                    System.arraycopy(message, 1, syncBytes, 0, message.length - 1);
                    String syncString = new String(syncBytes);
                    mapSynchronizer.setLatency(Long.decode(syncString));
                    mapSynchronizer.getplayer1().countDown();
                    break;
                case MessageCode.SYNCING:
                    if(message[2] == 0 && (MacroHardv2.actionResolver.getmyidint() != 0)){
                        byte[] temp = new byte[4];
                        //Message ID
                        temp[0] = MessageCode.SYNCING;
                        //Origin of message
                        temp[1] = (byte) MacroHardv2.actionResolver.getmyidint();
                        //0 for ping, 1 for sleep
                        temp[2] = 0;
                        //Sleep duration
                        temp[3] = 0;
                        MacroHardv2.actionResolver.sendReliable(temp);
                        break;
                    }
                    else if(message[2] == 0 && (MacroHardv2.actionResolver.getmyidint() == 0)){
                        mapSynchronizer.gethost().countDown();
                        break;
                    }
                    break;

                case MessageCode.BREAK_HOLE:
                    for (GameObject hole: holes) {
                        if (((Hole)hole).getId() == message[1]){
                            ((Hole)hole).setBreakHole(true);
                            break;
                        }
                    }
                    break;

                case MessageCode.DESTROY_BOULDER:
                    for (GameObject boulder: boulders) {
                        if (((Boulder) boulder).getId() == message[1]){
                            ((Boulder) boulder).setToDestroy(true);
                            break;
                        }
                    }
                    break;

                case MessageCode.DESTROY_WALL:
                    for (GameObject obstacle: obstacles) {
                        if (((Obstacle)obstacle).getId() == message[1]){
                            ((Obstacle)obstacle).setToDestroy(true);
                            break;
                        }
                    }
                    break;
                case MessageCode.DESTROY_DOOR:
                    for (GameObject door: doors) {
                        if (((Door)door).getId() == message[1]){
                            ((Door)door).setDestroyed(true);
                            break;
                        }
                    }
                    break;
                case MessageCode.FREEZE:
                    freezeMaze.set( message[1] * 10 + message[2] / 10);
                    break;
                case MessageCode.SLOWGAME:
                    slowGameDown.set(message[1] * 10 + message[2] / 10);
                    break;
                case MessageCode.TELEPORT:
                    float x1 = (float) message[2] * 10 + (float) message[3] / 10;
                    float y1 = (float) message[4] * 10 + (float) message[5] / 10 - gameSpeed*mapSynchronizer.getLatency();
                    synchronized (Player.class) {
                        player.x = x1;
                        player.y = y1;
                    }
                    mapSynchronizer.setSyncTele(true);
                    break;
                case MessageCode.SYNC_RENDER:
                    byte[] syncRenderBytes = new byte[message.length - 1];
                    System.arraycopy(message, 1, syncRenderBytes, 0, message.length - 1);
                    String syncRenderString = new String(syncRenderBytes);
                    mapSynchronizer.setHostSyncRender(Long.decode(syncRenderString));
                    break;
            }
        }
    }

    public void goToRestartState() {
        if (!end.get()) {
            MacroHardv2.actionResolver.sendReliable(wrapEndScore(score.get()));
        }

        long time = System.currentTimeMillis();
        while (!end.get()){
            if(System.currentTimeMillis() - time > 2000){
                break;
            }
        }

        mapMaker.interrupt();
        backgroundTaskExecutor.shutdownNow();
        MacroHardv2.actionResolver.leaveroom();
        gsm.set(new RestartState(gsm, getScore()), StateType.NON_PLAY);
    }

    private byte[] wrapEndScore(int score){
        byte[] scoreBytes = Integer.toString(score).getBytes();
        byte[] retVal = new byte[scoreBytes.length + 1];
        retVal[0] = MessageCode.END_GAME;
        System.arraycopy(scoreBytes, 0, retVal, 1, scoreBytes.length);
        return retVal;
    }

    public int getScore() {
        return score.get();
    }

    private static byte[] sendTeleport(int id, float x, float y){
        byte[] result = new byte[6];
        result[0] = MessageCode.TELEPORT;
        result[1] = (byte) id;
        result[2] = (byte) (x/10);
        result[3] = (byte)((x*10)%100);
        result[4] = (byte)(y/10);
        result[5] = (byte)((y*10)%100);
        return result;
    }

    private static byte[] sendFreeze(float x){
        byte[] result = new byte[3];
        result[0] = MessageCode.FREEZE;
        result[1] = (byte) (x/10);
        result[2] = (byte)((x*10)%100);
        return result;
    }

    private static byte[] sendSlow(float x) {
        byte[] result = new byte[3];
        result[0] = MessageCode.SLOWGAME;
        result[1] = (byte) (x / 10);
        result[2] = (byte) ((x * 10) % 100);
        return result;
    }

    /************************************************
     * MAP GENERATION METHODS HERE
     ************************************************
     */

    private MapTile[] generator(MapTile[] new_row){
        boolean test = false;
        while (!test) {
            int temp = mapRandomizer.nextInt(9)+1;
            for (int i = 0; i < temp; i++) {
                int coord = mapRandomizer.nextInt(9);
                new_row[coord] = MapTile.EMPTY;
            }
            for (int i = 0; i < new_row.length; i++) {
                if ((new_row[i] == MapTile.EMPTY) && current[i]) {
                    test = true;
                    break;
                }
            }
        }
        return new_row;
    }

    private boolean[] updatePath(MapTile[] new_row, boolean[] current){
        int out_index = 0;
        for (int k = 0; k < new_row.length; k++) {
            if ((new_row[k] == MapTile.EMPTY) && current[k]) {
                current[k] = true;
                out_index = k;
            } else {
                current[k] = false;
            }
        }

        for (int j = 1; j < new_row.length; j++) {
            if (out_index + j < new_row.length) {
                if ((new_row[out_index + j] == MapTile.EMPTY) && current[out_index + j - 1]) {
                    current[out_index + j] = true;
                }
                else {
                    break;
                }
            }
            if (out_index - j >= 0) {
                if ((new_row[out_index - j] == MapTile.EMPTY) && current[out_index - j + 1]) {
                    current[out_index - j] = true;
                }
                else {
                    break;
                }
            }
        }
        return current;
    }

    private MapTile[] genSpikes(MapTile[] new_row){
        int spikeNo = mapRandomizer.nextInt(3);
        for (int j = 0; j < spikeNo; j++) {
            int pos = mapRandomizer.nextInt(9);
            if (new_row[pos] == MapTile.OBSTACLES){
                new_row[pos] = MapTile.SPIKES;
            }
        }
        return new_row;
    }

    private MapTile[] genHole(MapTile[] new_row){
        int holeNo = mapRandomizer.nextInt(3);
        for (int j = 0; j < holeNo; j++) {
            int pos = mapRandomizer.nextInt(9);
            if (new_row[pos] == MapTile.EMPTY){
                new_row[pos] = MapTile.HOLE;
            }
        }
        return new_row;
    }

    private MapTile[] genQuickSand(MapTile[] new_row){
        int holeNo = mapRandomizer.nextInt(3);
        for (int j = 0; j < holeNo; j++) {
            int pos = mapRandomizer.nextInt(9);
            if (new_row[pos] == MapTile.EMPTY){
                new_row[pos] = MapTile.SAND;
            }
        }
        return new_row;
    }

    private MapTile[] genPower(MapTile[] new_row){
        int temp = mapRandomizer.nextInt(9);
        if (new_row[temp] == MapTile.EMPTY){
            new_row[temp] = MapTile.POWER;
            powerCounter = 0;
        }
        return new_row;
    }

    private MapTile[] genDoor(MapTile[] new_row){
        for (int i = 0; i < current.length; i++) {
            if (new_row[i] == MapTile.EMPTY) {
                new_row[i] = MapTile.DOOR;
            }
        }
        return new_row;
    }

    private MapTile[] genBoulder(MapTile[] new_row){
        int counter = 0;
        for (int i = 0; i < current.length; i++){
            if (current[i] && counter < 2){
                new_row[i] = MapTile.BOULDER;
                counter++;
            }
        }
        return new_row;
    }

    private MapTile[] genCircle(ArrayList<MapTile[]> memory, boolean[] current, MapTile[] new_row, int number){
        int i;
        for (i = 0; i < current.length; i++) {
            if (current[i]){
                break;
            }
        }

        int j = 0;

        int counter = 0;
        while (true) {
            if (counter > 5) {
                if (memory.get(j)[i] != MapTile.M_CIRCLE) {
                    break;
                }
            }
            if (number == 0){
                if (i > 0 && memory.get(j)[i - 1] != MapTile.OBSTACLES) {
                    i--;
                    counter++;
                }
                else if (j < 2 && memory.get(j + 1)[i] != MapTile.OBSTACLES) {
                    j++;
                    counter++;
                }
                else if (i < 8 && memory.get(j)[i + 1] != MapTile.OBSTACLES) {
                    i++;
                    counter++;
                }
                else if (j > 0 && memory.get(j - 1)[i] != MapTile.OBSTACLES) {
                    j--;
                    counter++;
                }
            }
            else if (number == 1){
                if (i < 8 && memory.get(j)[i + 1] != MapTile.OBSTACLES) {
                    i++;
                    counter++;
                }
                else if (j < 2 && memory.get(j + 1)[i] != MapTile.OBSTACLES) {
                    j++;
                    counter++;
                }
                else if (i > 0 && memory.get(j)[i - 1] != MapTile.OBSTACLES) {
                    i--;
                    counter++;
                }
                else if (j > 0 && memory.get(j - 1)[i] != MapTile.OBSTACLES) {
                    j--;
                    counter++;
                }
            }
        }
        this.memory.get(j)[i] = MapTile.M_CIRCLE;
        if (j == 0){
            new_row[i] = MapTile.M_CIRCLE;
            return new_row;
        } else {
            try {
                mapMod.acquire();
                mapBuffer.get(mapBuffer.size() - j)[i] = MapTile.M_CIRCLE;
                mapMod.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private MapTile[] genSwitch(ArrayList<MapTile[]> memory, boolean[] current, MapTile[] new_row){
        int i;
        for (i = 0; i < current.length; i++) {
            if (current[i]){
                break;
            }
        }

        int j = 0;

        int counter = 0;
        while (true) {
            if (counter > 4 && memory.get(j)[i] == MapTile.EMPTY) {
                break;
            }
            int dir = mapRandomizer.nextInt(4);
            switch (dir){
                case 0:
                    if (i > 0 && memory.get(j)[i - 1] != MapTile.OBSTACLES) {
                        i--;
                        counter++;
                    }
                    break;
                case 1:
                    if (j < 2 && memory.get(j + 1)[i] != MapTile.OBSTACLES) {
                        j++;
                        counter++;
                    }
                    break;
                case 2:
                    if (i < 8 && memory.get(j)[i + 1] != MapTile.OBSTACLES) {
                        i++;
                        counter++;
                    }
                    break;
                case 3:
                    if (j > 0 && memory.get(j - 1)[i] != MapTile.OBSTACLES) {
                        j--;
                        counter++;
                    }
                    break;
            }
        }
        if (j == 0){
            new_row[i] = MapTile.SWITCH;
            return new_row;
        } else {
            try {
                mapMod.acquire();
                mapBuffer.get(mapBuffer.size() - j)[i] = MapTile.SWITCH;
                mapMod.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    void wallCoord(){
        stageCounter++;
        if (stageCounter % 60 == 4) {
            stage = nextStage;
        }

        if (stageCounter % 60 == 0 && stageCounter > 0){
            nextStage = Stage.values()[mapRandomizer.nextInt(3)];
            if (stage == Stage.DUNGEON){
                if (nextStage == Stage.DUNGEON){
                    stage = Stage.DUNGEON;
                } else if (nextStage == Stage.ICE){
                    stage = Stage.TRANS_DUN_ICE;
                } else if (nextStage == Stage.DESERT){
                    stage = Stage.TRANS_DUN_DES;
                }
            }
            else if (stage == Stage.ICE){
                if (nextStage == Stage.DUNGEON){
                    stage = Stage.TRANS_ICE_DUN;
                } else if (nextStage == Stage.ICE){
                    stage = Stage.ICE;
                } else if (nextStage == Stage.DESERT){
                    stage = Stage.TRANS_ICE_DES;
                }
            }
            else if (stage == Stage.DESERT){
                if (nextStage == Stage.DUNGEON){
                    stage = Stage.TRANS_DES_DUN;
                } else if (nextStage == Stage.ICE){
                    stage = Stage.TRANS_DES_ICE;
                } else if (nextStage == Stage.DESERT){
                    stage = Stage.DESERT;
                }
            }
        }
        powerCounter += 1;
        doorCounter = (doorCounter + 1)%30;
        spikeCounter += 1;
        MapTile[] new_row = createArray(MapTile.OBSTACLES);

        // random generator
        new_row = generator(new_row);

        // updating the path array
        current = updatePath(new_row, current);

        // creating random spikes
        if (spikeCounter > 3) {
            if (stage == Stage.DUNGEON) {
                new_row = genSpikes(new_row);
            } else if (stage == Stage.ICE) {
                new_row = genHole(new_row);
            } else if (stage == Stage.DESERT){
                new_row = genQuickSand(new_row);
            }
            spikeCounter = 0;
        }

        // updating the memory
        memory.remove(memory.size() - 1);
        memory.add(0, new_row);

        // spawning door
        if (doorCounter == 15) {
            new_row = genDoor(new_row);
        }

        // spawning boulder
        if (stage == Stage.DESERT && doorCounter == 4){
            new_row = genBoulder(new_row);
        }

        //spawning magic circles
        if (stage == Stage.DESERT && doorCounter == 3){
            for (int i = 0; i < 2; i++) {
                MapTile[] result;
                if ((result = genCircle(memory, current, new_row, i)) != null){
                    new_row = result;
                }
            }
        }

        // spawning door switch
        if (doorCounter == 14 || doorCounter == 18) {
            MapTile[] result;
            if ((result = genSwitch(memory, current, new_row)) != null){
                new_row = result;
            }
        }

        // spawning power ups after a certain time. 2 is for testing. 8 is used
        if (powerCounter > (mapRandomizer.nextInt(10) + 10)) {
            new_row = genPower(new_row);
        }

        try {
            mapPro.acquire();
            mapMod.acquire();

            mapBuffer.add(new_row);

            mapMod.release();
            mapCon.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}