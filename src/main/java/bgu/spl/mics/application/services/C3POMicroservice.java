package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BroadcastTerminate;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;


import java.util.concurrent.CountDownLatch;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {
	private CountDownLatch[] cdl;
	private Ewoks ewoks = Ewoks.getInstance();

	public C3POMicroservice(CountDownLatch[] cdl) {
		super("C3PO");
		this.cdl = cdl;
	}
	
	@Override
	protected void initialize() {
		subscribeEvent(AttackEvent.class, attack -> {
			ewoks.acquire(attack.getAttack().getSerials());
			try {
				Thread.sleep(attack.getAttack().getDuration());
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}

			complete(attack, true);
			ewoks.release(attack.getAttack().getSerials());
			Diary.getInstance().setTotal();
			Diary.getInstance().setFinishC3PDF();
			cdl[2].countDown();
			if (cdl[2].getCount() == 0){
				sendEvent(new DeactivationEvent());
			}
		});
        
        subscribeBroadcast(BroadcastTerminate.class, c -> {
            terminate();
            Diary.getInstance().setC3POTerminate();
			cdl[1].countDown();
        });


		cdl[0].countDown();
	}
}

