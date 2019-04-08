package com.kyper.btedit.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kyper.btedit.BTreeEditor;
import com.kyper.btedit.events.IEvent;
import com.kyper.btedit.events.ProjectClosedEvent;
import com.kyper.btedit.events.ProjectSelectionEvent;

/**
 * tabs for opened projects
 * 
 * @author john
 *
 */
public class ProjectTabs extends Table {

	Array<Tab> tabs;
	int selected = -1;
	BTreeEditor editor;

	public ProjectTabs(BTreeEditor editor) {
		this.editor = editor;
		tabs = new Array<ProjectTabs.Tab>();
		align(Align.left);
	}

	/**
	 * add a tab with the given name
	 * 
	 * @param tabName
	 */
	public void addTab(String tabName) {
		Tab tab = new Tab(tabName, this);
		tabs.add(tab);
		setSelectedIndex(tabs.size - 1);
		refresh();
	}

	/**
	 * switch the two tabs
	 * 
	 * @param index1
	 * @param index2
	 */
	public void switchTabs(int index1, int index2) {
		tabs.swap(index1, index2);
	}

	/**
	 * remove the tab and also remove it from the workspace
	 * 
	 * @param tab
	 */
	public void removeTab(Tab tab) {
		int index = tabs.indexOf(tab, true);
		if(index == selected)
			selected--;
		tabs.removeValue(tab, true);
		setSelectedIndex(selected);
		editor.getEventManager().queue(new ProjectClosedEvent(editor.getWorkspace().getProjects().get(index)));
	}

	public void setSelectedIndex(int index) {
		if(this.selected != index && index != -1)
			editor.getEventManager().queue(new ProjectSelectionEvent(index));
		this.selected = index;
		refresh();
	}
	
	public int getSelectedIndex() {
		return selected;
	}

	public void changeTabName(String newName, int tabIndex) {
		tabs.get(tabIndex).setName(newName);
	}
	
	public Tab getSelectedTab() {
		return tabs.size > 0 ? tabs.get(selected) : null;
	}

	public void refresh() {
		clearChildren();
		for (int i = 0; i < tabs.size; i++) {
			Tab tab = tabs.get(i);
			if (i == selected) {
				tab.setSelected(true);
			} else {
				tab.setSelected(false);
			}

			add(tab).padLeft(2);
		}
	}

	public void clear() {
		tabs.clear();
		refresh();
		selected = 0;
	}

	private class Tab extends Table {

		String name;
		VisLabel nameLabel;
		VisTextButton closeButton;
		ProjectTabs tabs;
		boolean selected = false;

		public Tab(String name, ProjectTabs tabs) {
			this.tabs = tabs;
			this.name = name;
			setBackground(VisUI.getSkin().getDrawable("button"));
			closeButton = new VisTextButton("X");
			closeButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					//TODO: Add some sort of check to make sure that the project is safe to close
					Tab.this.tabs.removeTab(Tab.this);
				}
			});
			this.nameLabel = new VisLabel(name);
			add(this.nameLabel).growX();
			add(closeButton);

		}

		@Override
		public float getMinWidth() {
			return 100;
		}
		
		@Override
		public float getMaxWidth() {
			if(selected)
				return 200;
			else
				return 100;
		}

		public VisTextButton getCloseButton() {
			return closeButton;
		}

		public void setName(String name) {
			this.name = name;
			this.nameLabel.setText(name);
		}

		public String getName() {
			return this.nameLabel.getText().toString();
		}

		public void setSelected(boolean selected) {
			if (selected) {
				setColor(Color.RED);
				setShortened(false);
				setWidth(200);
				System.out.println("selected " + name);
			} else {
				setColor(Color.WHITE);
				setWidth(80);
				setShortened(true);
			}
			this.selected = selected;
		}

		public void setShortened(boolean shortened) {
			if (shortened)
				if (nameLabel.getText().length > 8)
					nameLabel.setText(name.substring(0, 8) + "...");
				else
					nameLabel.setText(name);
		}
	}

}
