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

    private CountDownLatch HostL = new CountDownLatch(2);
    private CountDownLatch PlayerL1 = new CountDownLatch(1);
    private CountDownLatch PlayerL2 = new CountDownLatch(1);
    private long starttime, largestsleep;
    private long[] SyncRender = new long[3];
    private long[] latency = new long[3];

    MapSynchronizer(){
        super(0, 450, 0, 0);
        this.starttime = 0;
    }

    public void updateSyncRender(){
        this.SyncRender[MacroHardv2.actionResolver.getmyidint()] += 1;
    }

    public long[] getRenderList(){
        return this.SyncRender;
    }

    public void setPlayerSyncRender(long x, int player){
        SyncRender[player] = x;
    }

    public void sendSyncRender(){
        if(MacroHardv2.actionResolver.getmyidint() == 0){
            MacroHardv2.actionResolver.sendPing(wrapSyncRender(SyncRender[0]));
        }
//        else if(MacroHardv2.actionResolver.getmyidint() == 1){
//            MacroHardv2.actionResolver.sendPing(wrapSyncRender(SyncRender[1]));
//        }
//        else if(MacroHardv2.actionResolver.getmyidint() == 2){
//            MacroHardv2.actionResolver.sendPing(wrapSyncRender(SyncRender[2]));
//        }
    }

    private byte[] wrapSyncRender(long syncrender){
        String SyncRenderString = Long.toString(syncrender);
        byte[] SyncRenderBytes = SyncRenderString.getBytes();
        byte[] result = new byte[SyncRenderBytes.length + 2];
        result[0] = MessageCode.SyncRender;
        result[1] = (byte)MacroHardv2.actionResolver.getmyidint();
        for (int i = 0; i < SyncRenderBytes.length; i++) {
            result[i+2] = SyncRenderBytes[i];
        }
        return result;
    }

    public void sync(){
        if(MacroHardv2.actionResolver.getmyidint()==0){
            byte[] temp = new byte[5];
            //Message ID
            temp[0] = MessageCode.SYNCING;
            //Origin of message
            temp[1] = (byte) MacroHardv2.actionResolver.getmyidint();
            //0 for ping, 1 for sleep
            temp[2] = 0;
            //Sleep duration
            temp[3] = 0;
            starttime = System.currentTimeMillis();
            MacroHardv2.actionResolver.sendReliable(temp);
            System.out.println("HEHE: HOST SENT PING IS WAITING");
            try {
                HostL.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(latency[1] > latency [2]){
                largestsleep = latency[1];
            }
            else{
                largestsleep = latency[2];
            }
            //0 for ping, 1 for sleep
            temp[2] = 1;
            //Sleep duration
            temp[3] = (byte)(largestsleep - latency[1]);
            temp[4] = (byte)(largestsleep - latency[2]);
            System.out.println("time to sleep:" + temp[3]);
            System.out.println("time to sleep:" + temp[4]);
            System.out.println("HEHE: HOST SENDING PLAYERS TO START");
            MacroHardv2.actionResolver.sendReliable(temp);
            try {
                sleep(largestsleep);
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
        else if (MacroHardv2.actionResolver.getmyidint() == 2){
            try {
                PlayerL2.await();
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
    public CountDownLatch getplayer2(){
        return this.PlayerL2;
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
    public long getstarttime(){
        return this.starttime;
    }
    public void setlatency(long lag,int player){
        this.latency[player] = lag;
    }

}
