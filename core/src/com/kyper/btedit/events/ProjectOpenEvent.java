package com.kyper.btedit.events;

import com.kyper.btedit.project.Project;

public class ProjectOpenEvent implements IEvent{

	public String projectPath;
	public String projectName;
	public Project project;
	
	protected int priority = 0;
	
	public ProjectOpenEvent(Project p) {
		this.projectPath = p.getPath();
		this.projectName = p.getName();
		this.project = p;
	}
	
	@Override
	public int priority() {
		return priority;
	}

}
