package com.kyper.btedit.events;

public interface IEventListener<T extends IEvent> {
	
	public boolean react(T event);

}
