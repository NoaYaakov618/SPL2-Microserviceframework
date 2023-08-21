package bgu.spl.mics.application.passiveObjects;
import java.lang.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {
    private AtomicInteger totalAttacks = new AtomicInteger(0);
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;



    private static class SingletonHolder {
        private static Diary instance = new Diary();
    }

    public static Diary getInstance() {
        return SingletonHolder.instance;
    }

    public AtomicInteger getTotalAttacks(){
        return totalAttacks;
    }
    public long getHanSoloFinish(){
        return HanSoloFinish;
    }
    public long getC3POFinish(){
        return C3POFinish;
    }
    public long getR2D2Deactivate(){
        return R2D2Deactivate;
    }

    public long getLeiaTerminate(){
        return LeiaTerminate;
    }
    public long getC3POTerminate(){
        return C3POTerminate;
    }
    public long getR2D2Terminate() {
        return R2D2Terminate;
    }
    public long getLandoTerminate(){
        return LandoTerminate;
    }
    public long getHanSoloTerminate(){
        return HanSoloTerminate;
    }

    public void setHanSoloTerminate(){
        HanSoloTerminate = System.currentTimeMillis();
    }
    public void setLeiaTerminate(){
        LeiaTerminate = System.currentTimeMillis();
    }
    public void setC3POTerminate(){
        C3POTerminate = System.currentTimeMillis();
    }
    public void setR2D2Terminate(){
        R2D2Terminate = System.currentTimeMillis();
    }
    public void setLandoTerminate(){
        LandoTerminate = System.currentTimeMillis();
    }

    public void setFinishHanSolo(){
        HanSoloFinish = System.currentTimeMillis();
    }
    public void setFinishC3PDF(){
        C3POFinish = System.currentTimeMillis();
    }
    public void setFinishR2D2(){
        R2D2Deactivate = System.currentTimeMillis();
    }



    public void setTotal(){
        int val;
        do{
            val = totalAttacks.get();
        }
        while (!totalAttacks.compareAndSet(val,val+1));
    }

    private Diary(){
        HanSoloFinish = 0;
        C3POFinish = 0;
        R2D2Deactivate =0;
        R2D2Terminate = 0;
        LeiaTerminate = 0;
        C3POTerminate = 0;
        LandoTerminate = 0;
        HanSoloTerminate = 0;



    }

}
