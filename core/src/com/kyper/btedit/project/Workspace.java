package com.kyper.btedit.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.kyper.btedit.BTreeEditor;
import com.kyper.btedit.data.NodeBank;
import com.kyper.btedit.events.ProjectClosedEvent;
import com.kyper.btedit.events.ProjectOpenEvent;
import com.kyper.btedit.formats.FileFormat;

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
public class Workspace {

	Array<Project> projects;// currently opened projects
	int currentProject = 0;// currently viewed project

	private String path;
	private FileManager fileManager;

	private NodeBank nodeBank;

	private BTreeEditor editor;

	public Workspace(String path, BTreeEditor editor) {
		projects = new Array<Project>();
		nodeBank = new NodeBank(editor);
		nodeBank.loadNodeTemplates(Gdx.files.absolute(path+"\\"+"nodes.template"));

		/**
		 * create a file manager with the fileformat of json for now
		 */
		fileManager = new FileManager(nodeBank, FileFormat.Json);

		this.path = path;
		this.editor = editor;
	}
	
	public NodeBank getNodeBank() {
		return nodeBank;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public String getPath() {
		return path;
	}

	public Array<Project> getProjects() {
		return projects;
	}

	public boolean addProject(Project project) {
		if (projectAlreadyExists(project.name))
			return false;
		projects.add(project);
		return true;
	}
	
	public void open(String path,String name) {
		Project p = fileManager.open(path, name);
		if(p!=null && addProject(p))
			editor.getEventManager().queue(new ProjectOpenEvent(p));
	}

	private boolean projectAlreadyExists(String name) {
		for (int i = 0; i < projects.size; i++) {
			if (projects.get(i).getName().equals(name))
				return true;
		}
		return false;
	}

	public void removeProject(Project p) {
		if(projects.removeValue(p, true)) {
			editor.getEventManager().queue(new ProjectClosedEvent(p));
		}
	}

}
