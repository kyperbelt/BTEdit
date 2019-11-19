package com.kyper.btedit.command;

import com.kyper.btedit.BTreeEditor;
import com.kyper.btedit.BehaviorNode;

public class CutNodeCommand implements ICommand{
	
	BTreeEditor edit;
	BehaviorNode parent;
	BehaviorNode node;
	private int index;
	
	public CutNodeCommand(BTreeEditor edit,BehaviorNode parent,BehaviorNode node) {
		this.edit = edit;
		this.parent = parent;
		this.node = node;
	}

	@Override
	public void execute() {
		index = node.getIndex();
		if (parent != null) {
			edit.setSelectedNode(parent);
			edit.setClipboard(node);
			parent.removeNode(node);
		}
	}

	@Override
	public void undo() {
		System.out.println("undo cut");
		if (parent == null) return;
				System.out.println("undo cut2: index" + index);
		parent.addNode(node, index);
		edit.setSelectedNode(node);
	}

	@Override
	public String desc() {
		return String.format("Node[%s] was cut from Node[%s]", node.getNodeName(),parent.getNodeName());
	}

}
