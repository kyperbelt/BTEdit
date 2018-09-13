package com.kyper.btedit.command;

public interface ICommand {
	
	/**
	 * execute the command here
	 */
	public void execute();
	
	/**
	 * undo the command
	 */
	public void undo();
	
	/**
	 * get a short description string ("cut Node" , "pasted Node" ect.)
	 * @return
	 */
	public String desc();
}
