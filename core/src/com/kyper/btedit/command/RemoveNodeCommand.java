package com.kyper.btedit.command;

import com.kyper.btedit.BTreeEditor;
import com.kyper.btedit.BehaviorNode;

public class RemoveNodeCommand implements ICommand{

	BTreeEditor editor;
	BehaviorNode node;
	BehaviorNode parent;
	private int index;
	
	public RemoveNodeCommand(BTreeEditor editor,BehaviorNode node,BehaviorNode parent) {
		this.editor = editor;
		this.node = node;
		this.parent = parent;
	}
	
	@Override
	public void execute() {
		index = node.getIndex();
		if(parent!=null) {
			parent.removeNode(node);
		}
	}

	@Override
	public void undo() {
		if(parent!=null) {
			parent.addNode(node, index);
		}
	}

	@Override
	public String desc() {
		return String.format(parent!=null ? "Removed %s from %s ":"Removed %s", node.getNodeName(),parent!=null ? parent.getNodeName():"");
	}

}
