package com.kyper.btedit.data;

import com.badlogic.gdx.utils.Array;
import com.kyper.btedit.properties.NodeProperties;

/**
 * node data
 * @author john
 *
 */
public class Node {
	
	Tree tree;
	
	String nodename;
	NodeType type;

	NodeProperties properties;

	
	Node parent;
	Array<Node> children;
	
	public Node() {
		children = new Array<Node>();
		properties = new NodeProperties();
	}
	
	public void setTree(Tree tree) {
		this.tree = tree;
	}
	
	public Tree getTree() {
		return tree;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public void setChildren(Array<Node> children) {
		this.children = children;
	}
	
	public Array<Node> getChildren(){
		return children;
	}
	
	
	public void setName(String name) {
		this.nodename = name;
	}
	
	public String getName() {
		return nodename;
	}
	
	public void setNodeType(NodeType type) {
		this.type = type;
	}
	
	public NodeType getNodeType() {
		return type;
	}
	
	public void setNodeProperties(NodeProperties properties) {
		this.properties = properties;
	}
	
	public NodeProperties getNodeProperties() {
		return properties;
	}

	
}
