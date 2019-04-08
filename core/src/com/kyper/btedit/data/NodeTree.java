package com.kyper.btedit.data;

/**
 * the class representing the tree file- 
 * contains all the tree node data and any necessary meta data
 * @author john
 *
 */
public class NodeTree {
	
	Node root;
	
	public void setRoot(Node root) {
		if(root == null)
			throw new UnsupportedOperationException("root cannot be null");
		this.root = root;
	}
	
	public Node getRoot() {
		return root;
	}

}
