package com.kyper.btedit.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.LinkLabel.LinkLabelListener;
import com.kyper.btedit.BTConfig;
import com.kyper.btedit.BTreeEditor;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * a welcome page that includes recent project workspaces to open as well as the
 * option to open a new workspace
 * 
 * @author john
 *
 */
public class WelcomePage extends VisWindow{

	private static final int WIDTH = 400;
	private static final int HEIGHT = 200;
	
	
	VisLabel recentLabel;
	LinkLabel recent;
	VisTextButton open;
	BTreeEditor editor;
	
	public WelcomePage(BTreeEditor editor) {
		super("Welcome");

		this.editor = editor;
		setMovable(false);
		setResizable(false);
		setSize(WIDTH, HEIGHT);
		setCenterOnAdd(true);
		align(Align.top);
		setModal(true);
		
		recentLabel = new VisLabel("Recent:");
		String recentText = editor.prefs.getString(BTConfig.RECENT_PROJECT);
		
		recent = new LinkLabel(recentText == null ? "n/a":recentText);
		recent.setWidth(WIDTH);
		recent.setWrap(true);
		
		recent.setListener(new LinkLabelListener() {
			@Override
			public void clicked(String url) {
				if(url.equals("n/a"))
					return;
				WelcomePage.this.editor.openWorkSpace(url);
				
			}
		});
		
		open = new VisTextButton("open");
		open.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WelcomePage.this.editor.openWorkspaceChooser();
			}
		});
		
		add().padBottom(20).row();
		{
			Table t = new Table();
			t.add(recentLabel).growX().row();
			t.add(recent).growX().align(Align.topLeft);
			add(t).align(Align.topLeft).growX();
		}
		add(open).align(Align.topLeft).padRight(20);
		
	}
	
	
	

}
