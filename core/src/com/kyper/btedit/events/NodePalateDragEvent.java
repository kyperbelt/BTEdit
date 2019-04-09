package com.kyper.btedit.events;

import com.kyper.btedit.data.NodeType;

public class NodePalateDragEvent implements IEvent{

	public static final int TOUCHUP = 0;
	public static final int TOUCHDOWN = 1;
	
	public NodeType nodeType;
	public String name;
	public int eventType;
	
	public NodePalateDragEvent(String name,NodeType type,int eventType) {
		this.name = name;
		this.nodeType = type;
		this.eventType = eventType;
	}
	
	@Override
	public int priority() {
		return 0;
	}

}
