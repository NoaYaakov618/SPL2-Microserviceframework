package bgu.spl.mics;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Map.Entry;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	ConcurrentHashMap<Event, Future> mapEventToFutures = new ConcurrentHashMap<>();
	ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> mapMessegesForMicroservice = new ConcurrentHashMap<>();
	ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> mapWhoCanHandle = new ConcurrentHashMap<>();
	
	private static class SingletonHolder {
		private static MessageBus instance = new MessageBusImpl();
	}
	
	private MessageBusImpl() {}
	
	public static MessageBus getInstance() {
		return SingletonHolder.instance;
	}
	
	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		mapWhoCanHandle.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		mapWhoCanHandle.get(type).add(m);
	}
	
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		mapWhoCanHandle.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		mapWhoCanHandle.get(type).add(m);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = mapEventToFutures.get(e);
		future.resolve(result);
	}
	
	@Override
	public void sendBroadcast(Broadcast b) {
		ConcurrentLinkedQueue<MicroService> microServices = mapWhoCanHandle.get(b.getClass());
		if ( microServices == null ) {
			return;
		}
		for (MicroService m : microServices) {
			BlockingQueue<Message> q = mapMessegesForMicroservice.get(m);
			if ( q == null ) {
				continue;
			}
			q.add(b);
		}
		
	}
	
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = new Future<>();
		MicroService m;
		ConcurrentLinkedQueue<MicroService> microServices = mapWhoCanHandle.get(e.getClass());
		if ( microServices == null )
			return null;
		
		synchronized (e.getClass()) { // the even is locked until we finish order the Q
			m = microServices.poll(); // round robin manner
			if ( m == null ) {
				return null;
			}
			microServices.add(m);
		}

		synchronized (m) {  // make sure that messagebus dont add message when do unregister
			LinkedBlockingQueue<Message> messagesQueue = mapMessegesForMicroservice.get(m);
			if ( messagesQueue == null ) {
				return null;
			}
			mapEventToFutures.put(e, future);
			messagesQueue.add(e);
		}
		
		return future;
	}
	
	
	@Override
	public void register(MicroService m) {
		mapMessegesForMicroservice.putIfAbsent(m, new LinkedBlockingQueue<Message>());
	}
	
	@Override
	public void unregister(MicroService m) {
		for (Entry<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> entry : mapWhoCanHandle.entrySet()) {
			synchronized (entry.getKey()) { // two microservices can not work on the same Q
				entry.getValue().remove(m);
			}
		}
		LinkedBlockingQueue<Message> messagesQueue;
		synchronized (m) {
			messagesQueue = mapMessegesForMicroservice.remove(m);
		}
		while ( !messagesQueue.isEmpty() ) {
			Message a = messagesQueue.poll();
			if ( m instanceof Event ) {
				Event<?> e = (Event<?>) m;
				Future<?> future = mapEventToFutures.get(e);
				future.resolve(null);
			}
		}
	}
	
	
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		LinkedBlockingQueue<Message> messageQueue = mapMessegesForMicroservice.get(m); // blocking method
		return messageQueue.take();
	}
}
