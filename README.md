## Introduction

This is a project done during the [**Elements of Software Construction**](http://people.sutd.edu.sg/~sunjun/teach/50-003-2016/) course. The project deliverables consist of a concurrent multiplayer game (set in Android) in which players are able to interact with each other in real time.
At the end of the course, the game will potentially be pushed onto the Google Play Store.

## Project contents

This project is mainly based on BadLogicGames' libGDX open source library.
#### Using the libGDX library
After generating the library in the folder of your choice, AndroidLauncher will initialize MacroHardv2 class, which essentially uses libGDX's library, with the following lines of code in the onCreate() method.
		
			@Override
        	protected void onCreate (Bundle savedInstanceState) {
        		super.onCreate(savedInstanceState);
        		_gameHelper = new GSGameHelper(this, GameHelper.CLIENT_GAMES);
        		_gameHelper.enableDebugLog(false);
        
        		GameHelperListener gameHelperListerner = new GameHelper.GameHelperListener() {
        
        			@Override
        			public void onSignInSucceeded() {
        				// TODO Auto-generated method stub
        
        			}
        
        			@Override
        			public void onSignInFailed() {
        				// TODO Auto-generated method stub
        
        			}
        		};
        		_gameHelper.setup(gameHelperListerner);
        		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        		config.useImmersiveMode = true;
        
        		initialize(new MacroHardv2(this), config);
        		Gdx.graphics.requestRendering();
        	}

####Contents
1. android
    - AndroidLauncher
    - GSGameHelper
2. core
    - ActionResolver
    - MacroHardv2
    - **customEnum**
        - Direction
        - MapTile
        - MessageCode
        - PowerType
        - Stage
        - StageType
    - **Interface**
        - Collidable
    - **objects**
        - ActivePowerIcon
        - Background
        - Boulder
        - CustomButton
        - DangerZone
        - Direction
        - Door
        - Fog
        - GameObject
        - Ghost
        - Hole
        - InnatePowerIcon
        - JoyStick
        - MagicCircle
        - Movable
        - Obstacle
        - Overlay
        - Player
        - PlayerEffect
        - Pointer
        - Power
        - Sand
        - SideWall
        - Spikes
        - Switch
         -Troll
        - UI
    - **states**
        - GameStateManager
        - InstructionState
        - LoadingState
        - MapMaker
        - MapSynchronizer
        - MenuState
        - PlayerCoordinateSender
        - PlayState
        - PlayStateHost
        - PlayStateNonHost
        - RestartState
        - SplashState
        - State
    
**Kindly refer to the individual classes for more information**

## Branches
Initial commits were done in the Master branch. 
The BranchedNetwork was made simply to isolate the multiplayer version from the singe player version, as a way to test and isolate bugs related to multiplayer components.
The BranchedPlayState was made to test the transition from MenuState to PlayState.


## Motivation

This game was done as part of a project under the Elements of Software Construction module, during term 5 in the Information Systems Technology and Design pillar in Singapore University of Technology and Design. The motivation beyond the purpose of solely an academic project, is the fulfilment of creating a complete game, and also value adding to the game by producing updates if the game is deemed successful.
## Installation

Please ensure your Android device's Version Release is at least 9.
Simply pull the git repo and run the application either on an Android Emulator or your Android device.

**As a means to improve the game, kindly screenshot and include the error log in the issues segment of the BranchedNetwork page. It would be good to let us know at which part of the game the bug occurred as well**

##### Note 
Request an invite to the Google Play Services game room list in order to play this game in multiplayer mode. Contact any of the contributors for this access.
Do change the directory of the storeFile found in the **build.gradle(Module: android)**

        storeFile file('C:/Users/User/Desktop/GDX/macrokey.jks')
        
**Android Emulator may not run the game if Google Play Services is not updated!**

## API Reference

Kindly refer to the LibGDX website for full API documentation. [LibGDX API](https://libgdx.badlogicgames.com/nightlies/docs/api/)


## Contributors

Lim Zhi Han Ryan [nayr43](https://github.com/nayr43)  
Muhammad Syuqri bin Johanna [dansyuqri](https://github.com/dansyuqri)  
Nguyen Dang Minh [ndmnh](https://github.com/ndmnh)  
Samuel Lim Jik Hao [sabbath65](https://github.com/sabbath65)  
Sng Han Jie [hansthefearless](https://github.com/hansthefearless)

## License

The MIT License (MIT)
Copyright (c) <year> <copyright holders>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

