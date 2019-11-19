package com.kyper.btedit.command;

import com.kyper.btedit.BTreeEditor;
import com.kyper.btedit.BehaviorNode;

public class PasteNodeCommand implements ICommand{

	private BTreeEditor editor;
	private BehaviorNode node;
	private BehaviorNode clone;
	private BehaviorNode parent;
	//private int index;

	public PasteNodeCommand(BTreeEditor editor, BehaviorNode parent, BehaviorNode node) {
		this.editor = editor;
		this.node = node;
		this.parent = parent;
	}

	@Override
	public void execute() {
		clone = node.getCopy();
		this.parent.addNode(clone);
		clone.layout();
	}

	@Override
	public void undo() {
		if (parent == null) {

		} else {
				parent.removeNode(clone);
				editor.setSelectedNode(parent);
		}
	}

	@Override
	public String desc() {
		return String.format("Node[%s] pasted from clipboard.", node.getNodeName());
	}

}
