package com.kyper.btedit.events;

/**
 * a new project selection was made via tabs
 * @author john
 *
 */
public class ProjectSelectionEvent implements IEvent{

	public int index;
	
	public ProjectSelectionEvent(int index) {
		this.index = index;
	}
	
	@Override
	public int priority() {
		return 0;
	}

}
