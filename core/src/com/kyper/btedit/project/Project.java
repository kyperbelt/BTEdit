package com.kyper.btedit.project;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kyper.btedit.BTreeEditor;
import com.kyper.btedit.command.CommandManager;
import com.kyper.btedit.data.NodeTree;
import com.kyper.btedit.gui.GroupCamera;
import com.kyper.btedit.gui.NodeRepresentation;

/**
 * basic project class in where the project represents a separate behavior tree
 * file located in the same overall project folder. All projects will be
 * relative to the main project folder chosen at startup sort of like a
 * workspace.
 * 
 * @author john
 *
 */
public class Project {

	NodeTree tree;

	String name;
	String path;
	CommandManager commandManager;
	boolean dirty;

	// offsets of the node
	float x = 0;
	float y = 0;

	NodeRepresentation rootNode;
	GroupCamera camera;

	public Project(NodeTree tree, String name, String path) {
		this.tree = tree;
		this.name = name;
		this.path = path;
		commandManager = new CommandManager();
	}
	
	public GroupCamera getCamera() {
		return camera;
	}

	/**
	 * get the visual representation of the tree in the form of its root node
	 * 
	 * @return
	 */
	public NodeRepresentation getRootNodeRepresentation(BTreeEditor editor) {
		if (rootNode == null) {
			rootNode = NodeRepresentation.build(editor, getTree().getRoot());
			camera = new GroupCamera(0, 0, rootNode);
		}
		return rootNode;
	}

	/**
	 * get the x offset
	 * 
	 * @return
	 */
	public float getX() {
		return x;
	}

	/**
	 * get the y offset
	 * 
	 * @return
	 */
	public float getY() {
		return y;
	}

	/**
	 * set the offset of this projects root node in the visual representation
	 * 
	 * @param x
	 * @param y
	 */
	public void setOffset(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public void setTree(NodeTree tree) {
		this.tree = tree;
	}

	public NodeTree getTree() {
		return tree;
	}

	public void reAnchorRoot(Table treeContainer) {
		if (rootNode != null) {
			camera.setAnchorPos(treeContainer.getWidth() * .5f - rootNode.getWidth() * .5f,
					treeContainer.getHeight()*.95f  - rootNode.getHeight());
			camera.update();
		}
	}

}
