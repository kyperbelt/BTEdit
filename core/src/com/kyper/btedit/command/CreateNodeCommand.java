package com.kyper.btedit.command;

import com.badlogic.gdx.Gdx;
import com.kyper.btedit.BTreeEditor;
import com.kyper.btedit.BehaviorNode;

public class CreateNodeCommand implements ICommand {

	private BTreeEditor editor;
	private BehaviorNode node;
	private BehaviorNode parent;
	private int index;
	private boolean insertFirst;

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
	public CreateNodeCommand(BTreeEditor editor, BehaviorNode node, BehaviorNode parent, int index, boolean insertFirst) {
		this.editor = editor;
		this.node = node;
		this.parent = parent;
		this.index = index;
		this.insertFirst = insertFirst;
	}

	public CreateNodeCommand(BTreeEditor editor, BehaviorNode node, BehaviorNode parent, int index) {
		this(editor, node, parent, index, false);
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
				if (insertFirst)
				{
					parent.moveToFirst(node);
				}
			} else
			{
				parent.addNode(node, index);
				editor.setSelectedNode(node);
			}
		}
	}

	@Override
	public void undo() {
		if (parent == null) {

		} else {
				parent.removeNode(node);
				editor.setSelectedNode(parent);
		}
	}

	@Override
	public String desc() {
		return String.format(parent != null ? "Added %s to %s" : "Added %s", node.getNodeName(),
				parent != null ? parent.getNodeName() : "");
	}

}
