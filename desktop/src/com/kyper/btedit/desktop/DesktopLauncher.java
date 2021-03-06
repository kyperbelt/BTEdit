package com.kyper.btedit.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kyper.btedit.BTreeEditor;

public class DesktopLauncher {
	
	public static void main (String[] arg) {
		
		final BTreeEditor bt = new BTreeEditor();
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = BTreeEditor.WIDTH;
		config.height = BTreeEditor.HEIGHT;
		config.title = BTreeEditor.TITLE;
		new LwjglApplication(bt, config) {
			@Override
			public void exit() {
				if(bt.exit())
					super.exit();
			}
		};
	}
}
