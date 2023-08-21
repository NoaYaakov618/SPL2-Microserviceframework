package bgu.spl.mics.application.passiveObjects;


import java.util.HashMap;
import java.util.LinkedList;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
	private HashMap<Integer, Ewok> ewoks;
	
	private static class SingletonHolder {
		private static Ewoks instance = new Ewoks();
	}
	
	public static Ewoks getInstance() {
		return SingletonHolder.instance;
	}
	
	private Ewoks() {
		ewoks = new HashMap<>();
	}
	
	public void initialize(int numOfEwoks) {
		for (int i = 0; i < numOfEwoks; i++)
			ewoks.put(i + 1, new Ewok(i + 1));
	}
	
	public void acquire(int[] required_ewoks) {
		LinkedList<Integer> required_ewoks_lst = new LinkedList<>();
		for (int required_ewok : required_ewoks)
			required_ewoks_lst.add(required_ewok);
        
        required_ewoks_lst.sort(Integer::compareTo); // to prevent dead lock
		try {
			for (int required_ewok : required_ewoks_lst)
			    ewoks.get(required_ewok).acquire();
		}
		catch (InterruptedException ignored) {}
	}
	
	public void release(int[] required_ewoks) {
		for (int required_ewok : required_ewoks)
			ewoks.get(required_ewok).release();
	}

}
