package com.kyper.btedit.command;

import com.kyper.btedit.BTreeEditor;
import com.kyper.btedit.gui.BehaviorNode;

public class MoveNodeCommand implements ICommand {

	private boolean left;
	private BTreeEditor editor;
	private BehaviorNode node;
	private BehaviorNode parent;

	public MoveNodeCommand(BTreeEditor editor, BehaviorNode node, BehaviorNode parent, boolean left) {
		this.editor = editor;
		this.node = node;
		this.parent = parent;
		this.left = left;
	}

	@Override
	public void execute() {
		if(parent!=null) {
			if(left) {
				parent.moveLeft(node);
			}else {
				parent.moveRight(node);
			}
		}
	}

	@Override
	public void undo() {
		if(parent!=null) {
			if(left) {
				parent.moveRight(node);
			}else {
				parent.moveLeft(node);
			}
		}
	}

	@Override
	public String desc() {
		return String.format(parent != null ? "Moved %s[%s] in %s" : "Undefined Error[could not move without parent]",
				node.getNodeName(), left ? "left" : "right", parent != null ? parent.getNodeName() : "");
	}

}
