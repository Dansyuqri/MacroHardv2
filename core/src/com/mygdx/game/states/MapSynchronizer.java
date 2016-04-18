package com.mygdx.game.states;

import com.mygdx.game.MacroHardv2;
import com.mygdx.game.customEnum.MessageCode;
import com.mygdx.game.objects.Movable;

import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;

/**
 * Created by hj on 5/4/16.
 */
public class MapSynchronizer extends Movable{

    private CountDownLatch HostL = new CountDownLatch(1);
    private CountDownLatch PlayerL1 = new CountDownLatch(1);
    private long MysyncRender, OthersyncRender;
    private long latency;

    MapSynchronizer(){
        super(0, 450, 0, 0);
        this.MysyncRender = 0;
        this.OthersyncRender=0;
    }

    public void updateSyncRender(){
        this.MysyncRender += 1;
    }
    public void setLatency(long lag){
        latency = lag;
    }

    public long getMyRender(){
        return this.MysyncRender;
    }

    public long getOtherRender(){
        return this.OthersyncRender;
    }

    public void sendSyncRender(){
        MacroHardv2.actionResolver.sendPing(wrapSyncRender(MysyncRender));
    }

    public void setHostSyncRender(long sync){
        this.OthersyncRender = sync;
    }


    private byte[] wrapSyncRender(long syncrender){
        String SyncRenderString = Long.toString(syncrender);
        byte[] SyncRenderBytes = SyncRenderString.getBytes();
        byte[] result = new byte[SyncRenderBytes.length + 1];
        result[0] = MessageCode.SYNC_RENDER;
        for (int i = 0; i < SyncRenderBytes.length; i++) {
            result[i+1] = SyncRenderBytes[i];
        }
        return result;
    }

    private byte[] wrapLatency(long latency){
        String SyncString = Long.toString(latency);
        byte[] SyncBytes = SyncString.getBytes();
        byte[] result = new byte[SyncBytes.length + 1];
        result[0] = MessageCode.PLAYERSTART;
        for (int i = 0; i < SyncBytes.length; i++) {
            result[i+1] = SyncBytes[i];
        }
        return result;
    }

    public void sync(){
        if(MacroHardv2.actionResolver.getmyidint()==0){
            byte[] temp = new byte[4];
            //Message ID
            temp[0] = MessageCode.SYNCING;
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
            MacroHardv2.actionResolver.sendReliable(wrapLatency(latency));
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
    public float getLatency(){
        return latency/1000;
    }
    public void sendMessage(int messageCode, int id){
        byte[] message = new byte[2];
        message[0] = (byte) messageCode;
        message[1] = (byte) id;
        MacroHardv2.actionResolver.sendReliable(message);
    }

}
