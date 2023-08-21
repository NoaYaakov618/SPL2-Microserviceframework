package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.BroadcastTerminate;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.concurrent.CountDownLatch;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
	private long duration;
	private CountDownLatch[] cdl;
	
	public R2D2Microservice(long duration, CountDownLatch[] cdl) {
		super("R2D2");
		this.duration = duration;
		this.cdl = cdl;
	}
	
	
	@Override
	protected void initialize() {
		subscribeBroadcast(BroadcastTerminate.class, c -> {terminate();
		Diary.getInstance().setR2D2Terminate();
		cdl[1].countDown();
		});
		
		subscribeEvent(DeactivationEvent.class, deactivation -> {
			try {
				Thread.sleep(duration);
			}
			catch (InterruptedException ignored) {
			}
			
			sendEvent(new BombDestroyerEvent());
			complete(deactivation, true);
			Diary.getInstance().setFinishR2D2();
		});
		
		this.cdl[0].countDown();
	}
}
