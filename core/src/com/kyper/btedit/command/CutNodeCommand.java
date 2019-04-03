package com.kyper.btedit.command;

import com.kyper.btedit.BTreeEditor;
import com.kyper.btedit.gui.NodeRepresentation;

public class CutNodeCommand implements ICommand{
	
	BTreeEditor edit;
	NodeRepresentation parent;
	NodeRepresentation node;
	
	public CutNodeCommand(BTreeEditor edit,NodeRepresentation parent,NodeRepresentation node) {
		this.edit = edit;
		this.parent = parent;
		this.node = node;
	}

	@Override
	public void execute() {
		parent.removeNode(node);
		edit.setClipboard(node);
		edit.setSelectedNode(parent);
	}

	@Override
	public void undo() {
		parent.addNode(node);
		edit.setSelectedNode(node);
	}

	@Override
	public String desc() {
		return String.format("Node[%s] was cut from Node[%s]", node.getNodeName(),parent.getNodeName());
	}

}
