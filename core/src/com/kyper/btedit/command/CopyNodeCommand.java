package com.kyper.btedit.command;

import com.kyper.btedit.BTreeEditor;
import com.kyper.btedit.BehaviorNode;

public class CopyNodeCommand implements ICommand{

	BTreeEditor edit;
	BehaviorNode node;
	
	public CopyNodeCommand(BTreeEditor edit,BehaviorNode node) {
		this.edit = edit;
		this.node = node;
	}
	
	@Override
	public void execute() {
		edit.setClipboard(node);
	}

	@Override
	public void undo() {
		//edit.setClipboard(null);
	}

	@Override
	public String desc() {
		return String.format("Node[%s] copied to clipboard", node.getNodeName());
	}

}
