package com.kyper.btedit.events;

import com.kyper.btedit.data.Node;
import com.kyper.btedit.gui.NodeRepresentation;

/**
 * something has changed in one of the nodes of the current project and should be handled.(requires saving or discarding)
 * @author john
 *
 */
public class NodeChangeEvent implements IEvent{

	public static final int NODE_MOVED = 0;
	public static final int NODE_ADDED = 1;
	public static final int NODE_DELETED = 2;
	public static final int NODE_CHANGED = 3;
	
	public NodeRepresentation nodeRepresentation;
	public Node node;
	public int type;
	
	public NodeChangeEvent(NodeRepresentation nodeRepresentation,Node node,int type) {
		this.type = type;
		this.nodeRepresentation = nodeRepresentation;
		this.node = node;
	}
	
	@Override
	public int priority() {
		return 0;
	}

}
