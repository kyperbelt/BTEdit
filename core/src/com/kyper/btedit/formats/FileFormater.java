package com.kyper.btedit.formats;

import com.kyper.btedit.data.NodeTree;

public interface FileFormater {
	
	/**
	 * name of this format
	 * @return
	 */
	public FileFormat getFormat();
	
	/**
	 * check if the data is of this format
	 * @param data
	 * @return
	 */
	public boolean isFormat(String data);
	
	
	/**
	 * generate a data string in the correct format from a tree
	 * @param tree
	 * @return
	 */
	public String toFormat(NodeTree tree);
	
	/**
	 * parse the data into a tree
	 * @param data
	 * @return
	 */
	public NodeTree toTree(String data);

}
