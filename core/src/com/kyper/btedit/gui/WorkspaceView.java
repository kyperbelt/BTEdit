package com.kyper.btedit.gui;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTree;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kyper.btedit.BTConfig;
import com.kyper.btedit.BTreeEditor;

/**
 * workspace/folder view
 * 
 * @author john
 *
 */
public class WorkspaceView extends VisWindow {

	private final int MAXLEVEL = 3;
	private final int MAXCOUNT = 1000;

	private String path;
	private BTreeEditor editor;
	private VisScrollPane scrollPane;
	private int count = 0;

	public WorkspaceView(BTreeEditor editor) {
		super("Workspace");
		this.editor = editor;
		this.setResizable(false);
		this.setPosition(0, 0);
		this.setSize(300, Gdx.graphics.getHeight());
		this.setMovable(false);
		TableUtils.setSpacingDefaults(this);
		columnDefaults(0).left();
	}

	/**
	 * refresh all files in workspace
	 */
	public void refresh(String path) {
		this.path = path;
		FileHandle dir = Gdx.files.absolute(path);
		File file = dir.file();
		clearChildren();

		VisTree tree = new VisTree();

		count = 0;

		for (File d : file.listFiles()) {
			Node n = getNode(d.getAbsolutePath(), 1);
			if (n != null)
				tree.add(n);
		}

		scrollPane = new VisScrollPane(tree);
		add(scrollPane).grow();
	}

	public Node getNode(String path, int level) {
		count++;
		System.out.println("adding node -" + path);
		Node n = null;
		FileHandle dir = Gdx.files.absolute(path);
		File file = dir.file();
		if (isValid(file)) {
			
			if (file.isDirectory() && file.listFiles() != null) {
				n = new Node(new VisLabel(file.getName()));
				for (File d : file.listFiles()) {
					System.out.println("file:" + d.getName());
					if (d.isDirectory() && !path.contains("\\\\.") && level < MAXLEVEL) {
						n.add(getNode(d.getAbsolutePath(), level + 1));
					} else {
						if (d.getName().endsWith("." + BTConfig.EXTENSION)) {
							final String name = d.getName();
							VisLabel l = new VisLabel(name);
							l.setTouchable(Touchable.enabled);

							final Node node = new Node(l);
							n.add(node);

							l.addListener(new ClickListener() {
								@Override
								public void clicked(InputEvent event, float x, float y) {
									openProjectFromNode(node);
								}
							});

							System.out.println("added file " + d.getName());
						}
					}
				}
			}else {
				final String name = file.getName();
				VisLabel l = new VisLabel(name);
				l.setTouchable(Touchable.enabled);

				n = new Node(l);
				final Node node = n;

				l.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						openProjectFromNode(node);
					}
				});
			}
		}

		return n;
	}

	private boolean isValid(File file) {
		return (file.isDirectory() && !path.contains("\\\\.")) || file.getName().endsWith("." + BTConfig.EXTENSION);
	}

	public void openProjectFromNode(Node n) {
		String path = "";
		String name = ((VisLabel) n.getActor()).getText().toString();
		Node node = n.getParent();
		while (node != null) {
			path = "\\" + ((VisLabel) node.getActor()).getText().toString() + path;
			node = node.getParent();
		}
		String projectPath = this.path + path;
		editor.getWorkspace().open(projectPath, name);
	}

	/**
	 * tries to finds the nodes file in the project
	 * 
	 * @return
	 */
	public boolean nodesExist() {
		return false;
	}

}
