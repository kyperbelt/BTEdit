package com.kyper.btedit.events;

public class NodeBankEvent implements IEvent{
	
	public static int LOADED = 0;
	public static int SAVED = 1;
	
	public int type;

	public NodeBankEvent(int type) {
		this.type = type;
	}
	
	@Override
	public int priority() {
		return 0;
	}
	

}
