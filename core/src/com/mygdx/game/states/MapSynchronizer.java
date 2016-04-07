package com.mygdx.game.states;

import com.mygdx.game.MacroHardv2;
import com.mygdx.game.objects.Movable;

import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;

/**
 * Created by hj on 5/4/16.
 */
public class MapSynchronizer extends Movable{

    private boolean[] set;
    private float[] yOffset;
    private CountDownLatch HostL = new CountDownLatch(1);
    private CountDownLatch PlayerL1 = new CountDownLatch(1);
    private long latency;

    MapSynchronizer(){
        super(0, 450, 0, 0);
        set = new boolean[]{false, false, false};
        yOffset = new float[3];
    }

    public void set(int id){
        if (!set[id]) {
            set[id] = true;
            yOffset[id] = 450 - y;
        }
    }

    public float offset(float y, int id){
        return y - yOffset[id];
    }

    public boolean isSet(int id){
        return set[id];
    }

    public void sync(){
        if(MacroHardv2.actionResolver.getmyidint()==0){
            byte[] temp = new byte[4];
            //Message ID
            temp[0] = 5;
            //Origin of message
            temp[1] = (byte) MacroHardv2.actionResolver.getmyidint();
            //0 for ping, 1 for sleep
            temp[2] = 0;
            //Sleep duration
            temp[3] = 0;
            long start = System.currentTimeMillis();
            MacroHardv2.actionResolver.sendReliable(temp);
            try {
                HostL.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latency = (System.currentTimeMillis() - start)/2;
            //0 for ping, 1 for sleep
            temp[2] = 1;
            //Sleep duration
            temp[3] = (byte)latency;
            MacroHardv2.actionResolver.sendReliable(temp);
            try {
                sleep(latency);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Player
        else if (MacroHardv2.actionResolver.getmyidint() == 1){
            try {
                PlayerL1.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public CountDownLatch gethost(){
        return this.HostL;
    }
    public CountDownLatch getplayer1(){
        return this.PlayerL1;
    }


}
