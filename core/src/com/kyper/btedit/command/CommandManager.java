package com.kyper.btedit.command;

import com.badlogic.gdx.utils.Array;

/**
 * 
 * Command manager created to decouple from main btree class and also allow for
 * multiple projects to have their own command history.
 * 
 * @author john
 *
 */
public class CommandManager {

	//current command index
	private int current = 0;
	
	Array<ICommand> commands;

	public CommandManager() {
		commands = new Array<ICommand>();
	}
	
	public boolean execute(ICommand command) {
		//TODO: add command to command list and then execute it
		
		
		return false;
	}
	
	public boolean undo() {
		
		//TODO: undo last command on top of the list and then move back and index
		
		return false;
	}

}
