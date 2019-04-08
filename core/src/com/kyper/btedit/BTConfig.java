package com.kyper.btedit;

import com.badlogic.gdx.Input.Keys;

/**
 * basic configuration file
 * @author john
 *
 */
public class BTConfig {

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final boolean DEBUG = true;
	
	public static String VERSION = "0.1";
	public static String TITLE = "BT Edit v" + VERSION;
	public static String DEFAULT_NAME = "Untitled";
	public static String DEFAULT_PATH = "";
	public static String PREF_NAME = "BTreeEditor_Config";
	public static String EXTENSION = "btree";
	final static String PERIOD = ".";
	final static String FORWARD_DASH = "/";
	final static String BACK_DASH = "\\";

	public final static int UNDO_KEY = Keys.Z;
	public final static int REDO_KEY = Keys.Y;
	public final static int CUT_KEY = Keys.X;
	public final static int COPY_KEY = Keys.C;
	public final static int PASTE_KEY = Keys.V;

	public static final String LAST_SAVE_PATH = "last_save_path";
	public static final String RECENT_PROJECT = "recent_project_path";
	public static final String PROJECT_PATH = "project_path";
	public static final String NODES_FILE = "nodes_file";
	public static final String DEFAULT_NODES_FILE = "default.nodes";
	public static final String PROJ_FILE = "btree.proj";
	
}
