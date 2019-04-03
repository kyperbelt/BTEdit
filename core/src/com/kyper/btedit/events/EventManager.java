package com.kyper.btedit.events;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
@SuppressWarnings({ "rawtypes", "unchecked" })
public class EventManager {
	
	Array<IEvent> events;
	
	
	ObjectMap<Class, Array<IEventListener>> eventListeners;
	
	public EventManager() {
		events = new Array<IEvent>();
		eventListeners = new ObjectMap<Class, Array<IEventListener>>();
	}
	
	/**
	 * add a listener for a specific type of event
	 * @param listener
	 * @param clazz
	 */
	public void addListener(IEventListener listener,Class<? extends IEvent> clazz) {
		
		Array<IEventListener> listeners = eventListeners.get(clazz);
		if(listeners == null) {
			listeners = new Array<IEventListener>();
			eventListeners.put(clazz, listeners);
		}
		listeners.add(listener);
	}
	
	/**
	 * add an event to the queue to be proccessed later
	 * @param event
	 */
	public void queue(IEvent event) {
		events.add(event);
	}
	
	/**
	 * process all events 
	 * @param priority - sort events by priority
	 */
	
	public void process(boolean priority) {
		if(priority) {
			//sort events here
		
		}
		
		for (int i = 0; i < events.size; i++) {
			IEvent event = events.get(i);
			Class c = event.getClass();
			Array<IEventListener> elisteners = eventListeners.get(c);
			for (int j = 0; j < elisteners.size; j++) {
				IEventListener listener = elisteners.get(j);
				if(listener.react(event))break;
			}
		}
		
		//clear all events
		events.clear();
	}
	
	public void process() {
		process(false);
	}
	

}
