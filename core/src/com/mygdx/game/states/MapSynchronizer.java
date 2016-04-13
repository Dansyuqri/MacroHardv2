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
    private long[] latency = new long[3];

    MapSynchronizer(){
        super(0, 450, 0, 0);
        this.MysyncRender = 0;
        this.OthersyncRender=0;
    }

    public void updateSyncRender(){
        this.MysyncRender += 1;
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
        result[0] = MessageCode.SyncRender;
        for (int i = 0; i < SyncRenderBytes.length; i++) {
            result[i+1] = SyncRenderBytes[i];
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
            latency[1] = (System.currentTimeMillis() - start)/2;
            //0 for ping, 1 for sleep
            temp[2] = 1;
            //Sleep duration
            temp[3] = (byte)latency[1];
            MacroHardv2.actionResolver.sendReliable(temp);
            try {
                sleep(latency[1]);
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
    public float getLatency(int player){
        return latency[player]/1000;
    }
    public void sendMessage(int messageCode, float x, float y){
        byte[] message = new byte[5];
        message[0] = (byte) messageCode;
        message[1] = (byte) (x/10);
        message[2] = (byte)((x*10)%100);
        message[3] = (byte) (y/10);
        message[4] = (byte)((y*10)%100);
        MacroHardv2.actionResolver.sendReliable(message);
    }

}
