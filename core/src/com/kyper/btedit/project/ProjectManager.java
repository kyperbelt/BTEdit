package com.kyper.btedit.project;

import com.badlogic.gdx.utils.Array;
import com.kyper.btedit.FileManager;

/**
 * the main project manager or "workspace". All our projects that we have open
 * will be contained in this project manager and shall be part of the same
 * workspace. If we want to open projects from different workspaces we must
 * specify it with some sort of "import" method that will be later added. The
 * reason to keep everything localized relative to this root folder is to that
 * it can be easily integrated into our projects for actual use without much
 * tinkering on that end.
 * 
 * @author john
 *
 */
public class ProjectManager {

	Array<Project> projects;
	int currentProject = 0;

	FileManager fileManager;

	public ProjectManager() {
		projects = new Array<Project>();
		fileManager = new FileManager();
	}

}
