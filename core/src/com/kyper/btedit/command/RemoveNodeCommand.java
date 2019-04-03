package com.kyper.btedit.command;

import com.kyper.btedit.BTreeEditor;
import com.kyper.btedit.data.NodeType;
import com.kyper.btedit.gui.NodeRepresentation;

public class RemoveNodeCommand implements ICommand {

	BTreeEditor editor;
	NodeRepresentation node;
	NodeRepresentation parent;
	NodeRepresentation child;
	private int index;

	public RemoveNodeCommand(BTreeEditor editor, NodeRepresentation node, NodeRepresentation parent) {
		this.editor = editor;
		this.node = node;
		this.parent = parent;
		this.child = null;
	}

	@Override
	public void execute() {
		index = node.getIndex();
		if (parent != null) {
			editor.setSelectedNode(null);
			if (node.getNode().getNodeType() == NodeType.SUPPLEMENT) {
				if (node.getChildrenCount() > 0) {
					// remove child from this node, then remove the node
					child = node.getFirstChild();
					node.removeNode(child);
					parent.removeNode(node);
					// then re-add the child to this parent
					parent.addNode(child, 0);
					editor.setSelectedNode(child);
				}
			} else {
				parent.removeNode(node);
			}
			
			
		}
	}

	@Override
	public void undo() {
		if (parent != null) {
			if (child != null) {
				parent.removeNode(child);
				node.addNode(child, 0);
			}
			parent.addNode(node, index);
		}
	}

	@Override
	public String desc() {
		return String.format(parent != null ? "Removed %s from %s " : "Removed %s", node.getNodeName(),
				parent != null ? parent.getNodeName() : "");
	}

}
