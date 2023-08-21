package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
	int serialNumber;
	boolean available;
	
	public Ewok(int serialNumber) {
		this.serialNumber = serialNumber;
		available = true;
	}
	
	/**
	 * Acquires an Ewok
	 */
	public void acquire() throws InterruptedException { // only one micoservice can acquire the specific ewok
		synchronized (this) {
			while ( !available )
				wait(); // wait until other microservice will finish and releas the ewok
		}
		
		available = false;
	}
	
	/**
	 * release an Ewok
	 */
	public void release() {
//		assert !available; // make sure it's not available
		available = true;
		synchronized (this) {  // we dont want to allow  two or more microsrvices to release at the same time because aquire is synconized also release
			notifyAll(); // wake up all the microservices that are waiting for this ewok
		}
	}
}
