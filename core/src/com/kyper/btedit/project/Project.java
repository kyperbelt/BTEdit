package com.kyper.btedit.project;

import com.kyper.btedit.command.CommandManager;
import com.kyper.btedit.data.NodeTree;

/**
 * basic project class in where the project represents a separate behavior tree file located in the same overall project folder.
 * All projects will be relative to the main project folder chosen at startup sort of like a workspace.
 * @author john
 *
 */
public class Project {
	
	NodeTree tree;
	String name;
	String path;
	CommandManager commandManager;
	
	
	public Project(NodeTree tree,String name,String path) {
		this.name = name;
		this.path = path;
		commandManager = new CommandManager();
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
	
	public CommandManager getCommandManager() {
		return commandManager;
	}
	
	public void setTree(NodeTree tree) {
		this.tree = tree;
	}
	
	public NodeTree getTree() {
		return tree;
	}
	

}
