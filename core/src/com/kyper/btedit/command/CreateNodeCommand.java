package com.kyper.btedit.command;

import com.badlogic.gdx.Gdx;
import com.kyper.btedit.BTreeEditor;
import com.kyper.btedit.gui.BehaviorNode;

public class CreateNodeCommand implements ICommand {

	private BTreeEditor editor;
	private BehaviorNode node;
	private BehaviorNode parent;
	private int index;

	/**
	 * create and add the behavior node to the parent or create as root if no parent
	 * exists
	 * 
	 * @param editor
	 *            - current editor
	 * @param node
	 *            - the node being created and added
	 * @param parent
	 *            - the parent of the node being created and added
	 * @param index
	 *            - the index in which to place the node relative to the parent (-1)
	 *            just means ad
	 */
	public CreateNodeCommand(BTreeEditor editor, BehaviorNode node, BehaviorNode parent, int index) {
		this.editor = editor;
		this.node = node;
		this.parent = parent;
		this.index = index;
	}

	@Override
	public void execute() {

		if (parent == null) {
			editor.current = node;
			editor.tree_view.addActor(node);
			editor.setDirty();
		} else {
			if (index == -1) {
				parent.addNode(node);
				editor.setSelectedNode(node);
			}

		}
	}

	@Override
	public void undo() {
		if (parent == null) {

		} else {

			if (index == -1) {
				parent.removeNode(node);
				editor.setSelectedNode(parent);
			}
		}
	}

	@Override
	public String desc() {
		return String.format(parent != null ? "Added %s to %s" : "Added %s", node.getNodeName(),
				parent != null ? parent.getNodeName() : "");
	}

}
