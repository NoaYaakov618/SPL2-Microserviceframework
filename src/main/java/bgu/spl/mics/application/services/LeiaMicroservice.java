package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BroadcastTerminate;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Input;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private CountDownLatch[] cdl;
	
	public LeiaMicroservice(Attack[] attacks, CountDownLatch[] cdl) {
		super("Leia");
		this.attacks = attacks;
		this.cdl = cdl;
	}
	
	@Override
	protected void initialize() {
		subscribeBroadcast(BroadcastTerminate.class, c -> {
			Diary.getInstance().setLeiaTerminate();
			terminate();
			cdl[1].countDown();
		});

		try {
			this.cdl[0].await();
		}
		catch (InterruptedException ignored) { }
		
		for (Attack attack : attacks) {
			Attack newAttack = new Attack(attack.getSerials(), attack.getDuration());
			AttackEvent a = new AttackEvent(newAttack);
			sendEvent(a);
		}
	}
}
