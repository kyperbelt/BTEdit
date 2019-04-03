package com.kyper.btedit.events;

import com.kyper.btedit.project.Project;

public class ProjectClosedEvent implements IEvent{

	public Project project;
	
	public ProjectClosedEvent(Project p) {
		this.project = p;
	}
	
	@Override
	public int priority() {
		return 0;
	}

}
