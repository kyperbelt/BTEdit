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

	Array<ICommand> commands;

	public CommandManager() {
		commands = new Array<ICommand>();
	}

}
