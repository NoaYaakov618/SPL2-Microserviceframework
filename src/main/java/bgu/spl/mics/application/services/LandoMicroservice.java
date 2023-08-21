package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.BroadcastTerminate;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.concurrent.CountDownLatch;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice extends MicroService {
	private long duration;
	private CountDownLatch[] cdl;
	
	public LandoMicroservice(long duration, CountDownLatch[] cdl) {
		super("Lando");
		this.duration = duration;
		this.cdl = cdl;
	}
	
	@Override
	protected void initialize() {
		subscribeEvent(BombDestroyerEvent.class, c -> {
			try {
				Thread.sleep(duration);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}

			complete(c, true);
			subscribeBroadcast(BroadcastTerminate.class, T -> {
				terminate();
				Diary.getInstance().setLandoTerminate();
				cdl[1].countDown();
			});
			sendBroadcast(new BroadcastTerminate());


		});
		
		this.cdl[0].countDown(); // tell i have done initialization
	}
}
