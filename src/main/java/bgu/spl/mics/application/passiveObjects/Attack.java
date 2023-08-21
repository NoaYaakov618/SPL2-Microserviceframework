package bgu.spl.mics.application.passiveObjects;

import java.util.List;


/**
 * Passive data-object representing an attack object.
 * You must not alter any of the given public methods of this class.
 * <p>
 * YDo not add any additional members/method to this class (except for getters).
 */
public class Attack {
    final int duration;
    final int[] serials;

    /**
     * Constructor.
     */
    public Attack(int[] serialNumbers, int duration) {
        this.serials = serialNumbers;
        this.duration = duration;
    }

    public int[] getSerials(){
        return serials;
    }

    public int getDuration(){
        return duration;
    }
}
