//TODOS:
//Once online and can google ;)
//1. Have file choosers respect project folder etc.
//1a. File Choosers on save should add extension or ensure extension is there? Think it doesn't change
//when project changes
//2. When first running, don't crash with bad path - need working directory as default, not blank
//3. Have it remember last unique project, and have a toggle / fast option between projects

package com.kyper.btedit;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooserListener;
import com.kyper.btedit.command.ICommand;
import com.kyper.btedit.data.NodeTemplate;
import com.kyper.btedit.events.EventManager;
import com.kyper.btedit.events.IEventListener;
import com.kyper.btedit.events.NodeBankEvent;
import com.kyper.btedit.events.ProjectClosedEvent;
import com.kyper.btedit.events.ProjectOpenEvent;
import com.kyper.btedit.events.ProjectSelectionEvent;
import com.kyper.btedit.gui.GroupCamera;
import com.kyper.btedit.gui.NodePalate;
import com.kyper.btedit.gui.NodeRepresentation;
import com.kyper.btedit.gui.ProjectTabs;
import com.kyper.btedit.gui.PropertyDisplay;
import com.kyper.btedit.gui.WelcomePage;
import com.kyper.btedit.gui.WorkspaceView;
import com.kyper.btedit.project.Project;
import com.kyper.btedit.project.Workspace;

public class BTreeEditor extends ApplicationAdapter {

	private SpriteBatch batch;
	private Sprite background;

	final static int CLOSE = 0;
	final static int OPEN = 1;
	final static int CREATE = 2;

	boolean m_drag = false;
	float m_dragScreenX;
	float m_dragScreenY;

	public Group tree_view;

	public Table button_window;

	private NodeRepresentation selected;
	private NodeRepresentation clipboard;
	public NodeRepresentation current;

	VisTextButton create, open, save, saveas, config;
	VisLabel projectLabel;

	public Array<String> items = new Array<String>();

	public String project_name;
	public String project_path;
	public String project_type;
	public String project_ext;
	public String project_path_recent;
	public String last_save_path;

	public String m_currentProjectFolderName = null;
	public NodeTemplate m_defaultRootTemplate = null;

	public Array<NodeTemplate> composite_nodes;
	public Array<NodeTemplate> supplement_nodes;
	public Array<NodeTemplate> leaf_nodes;

	public VisWindow node_chooser;
	public VisSelectBox<String> nodetype_sel;
	public VisSelectBox<String> node_sel;
	public VisTextButton select;

	public VisTextButton insert;

	public VisWindow save_window;
	public VisTextButton save_project, close_project;

	public boolean dirty = false;

	public PropertyDisplay property_display;

	public FileChooser chooser;
	public FileChooser saver;

	public Array<ICommand> commands;
	public int command_index = 0;

	public NodeRepresentation editing; // node currently being edited

	Table tt;

	public NodeRepresentation last_chosen_node;

	// config stuff
	public VisWindow config_window;
	public VisLabel node_file_label, project_ext_label, project_type_label;
	public VisTextField node_file_textfield, project_ext_textfield, project_type_textfield;
	public VisTextButton node_file_change_button, recent_project_button;
	public FileChooser nodefile_chooser;
	public VisTextButton accept_config;

	public GroupCamera node_camera;

	// new-------------------------------------------------------

	private Stage stage;
	private WelcomePage welcomePage;
	private FileChooser projectFolderChooser;
	private boolean busy = false;
	private Workspace workspace;
	private WorkspaceView workspaceView;
	private ProjectTabs tabs;
	private NodePalate palate;

	private EventManager eventManager;

	// layout
	private Table root;
	private Table workspaceContainer; // left side container for workspaceView
	private Table midContainer; // the middle container for the tabs and current tree view
	private Table tabContainer; // container for the tabs;
	private Table treeContainer; // contains the tree currently being worked on
	private Table buttonContainer; // container for the buttons with actions that can be done for the current
									// project

	public Preferences prefs;

	// ----------------------------------------------------------

	@Override
	public void create() {
		VisUI.load(SkinScale.X1);

		// VisUI.getSkin().get("default",LabelStyle.class).fontColor = Color.DARK_GRAY;
		Assets.loadTextures();
		Assets.createStyles();

		// --events
		eventManager = new EventManager();
		eventManager.addListener(new IEventListener<ProjectOpenEvent>() {

			public boolean react(ProjectOpenEvent event) {
				System.out.println("project opened:" + event.projectName + " project-path:" + event.projectPath);
				tabs.addTab(event.projectName);
				palate.toFront();
				return true;
			};

		}, ProjectOpenEvent.class);
		eventManager.addListener(new IEventListener<ProjectSelectionEvent>() {
			@Override
			public boolean react(ProjectSelectionEvent event) {

				Project selectedProject = workspace.getProjects().get(event.index);
				NodeRepresentation n = selectedProject.getRootNodeRepresentation(BTreeEditor.this);

				treeContainer.clear();
				treeContainer.addActor(n);
				n.setAnchored(false);
				n.layout(true);
				resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				selectedProject.reAnchorRoot(treeContainer);

				
				palate.toFront();
				System.out.println("project index selected :" + event.index);
				return true;
			}

		}, ProjectSelectionEvent.class);
		eventManager.addListener(new IEventListener<ProjectClosedEvent>() {
			@Override
			public boolean react(ProjectClosedEvent event) {
				System.out.println("projectClosed : " + event.project.getName());
				
				return false;
			}
		}, ProjectClosedEvent.class);
		
		eventManager.addListener(new IEventListener<NodeBankEvent>() {
			@Override
			public boolean react(NodeBankEvent event) {
				if(event.type == NodeBankEvent.LOADED) {
					palate.remove();
					int width = Gdx.graphics.getWidth();
					int height = Gdx.graphics.getHeight();
					palate.setPosition(width-palate.getWidth(), height * .85f - palate.getHeight());
					stage.addActor(palate);
					palate.refreshPalate(workspace.getNodeBank());
					
				}
				return false;
			}
		}, NodeBankEvent.class);

		// --EVENTS END

		prefs = Gdx.app.getPreferences(BTConfig.PREF_NAME);

		ScreenViewport sv = new ScreenViewport();

		stage = new Stage(sv);
		stage.setDebugAll(BTConfig.DEBUG);

		root = new Table();
		root.setFillParent(true);
		stage.getRoot().addActor(root);
		
		palate = new NodePalate();

		// --containers
		workspaceContainer = new Table();
		midContainer = new Table();
		tabContainer = new Table();
		treeContainer = new Table();
		treeContainer.setClip(true);
		treeContainer.setTransform(true);
		treeContainer.setFillParent(true);
		buttonContainer = new Table();

		root.add(workspaceContainer).width(300).growY();
		root.add(midContainer).grow();

		int barHeight = 30;

		midContainer.align(Align.top);
		midContainer.add(tabContainer).padTop(20).align(Align.topLeft).height(barHeight).growX().row();
		midContainer.add(treeContainer).grow().row();
		
		midContainer.add(buttonContainer).align(Align.right).height(barHeight).growX();

		background = new Sprite(new Texture(Gdx.files.internal("background.png")));
		background.setColor(new Color(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, .2f));
		centerBackground();

		welcomePage = new WelcomePage(this);
		stage.addActor(welcomePage);

		tabs = new ProjectTabs(this);
		tabContainer.add(tabs).left().grow();

	

		// ---workspace
		createWorkSpaceChooser();
		workspaceView = new WorkspaceView(this);

		batch = new SpriteBatch();
		treeContainer.setSize(Gdx.graphics.getWidth() - workspaceView.getWidth(), Gdx.graphics.getHeight() - 80);
		
		// property_display = new PropertyDisplay(this);
		//
		// commands = new Array<ICommand>();
		//

		//
		// FileChooser.setDefaultPrefsName("com.kyper.btedit.filechooser");
		// last_chosen_node = null;
		//
		// composite_nodes = new Array<NodeTemplate>();
		// supplement_nodes = new Array<NodeTemplate>();
		// leaf_nodes = new Array<NodeTemplate>();
		//
		//
		// if (!prefs.contains(BTConfig.NODES_FILE)) {
		// FileHandle nodes = Gdx.files.local(BTConfig.DEFAULT_NODES_FILE);
		// if (!nodes.exists()) {
		// nodes.writeString(Utils.DEFAULT_NODES, false);
		// }
		//
		// String absolutePath = nodes.file().getAbsolutePath();
		// prefs.putString(BTConfig.NODES_FILE, absolutePath);
		// FileHandle f = Gdx.files.absolute(absolutePath);
		// prefs.putString(BTConfig.LAST_SAVE_PATH, f.parent().path());
		// prefs.putString(BTConfig.PROJECT_PATH, f.parent().path());
		// }
		//
		// if (prefs.contains(BTConfig.RECENT_PROJECT)) {
		// project_path_recent = prefs.getString(BTConfig.RECENT_PROJECT);
		// } else {
		// project_path_recent = null;
		// }
		//
		// if (!prefs.contains(BTConfig.LAST_SAVE_PATH)) {
		// prefs.putString(BTConfig.LAST_SAVE_PATH, BTConfig.DEFAULT_PATH);
		// }
		//
		// if (!prefs.contains(BTConfig.PROJECT_PATH)) {
		// prefs.putString(BTConfig.PROJECT_PATH, BTConfig.DEFAULT_PATH);
		// }
		//
		// last_save_path = prefs.getString(BTConfig.LAST_SAVE_PATH);
		//
		// setCurrentProjectFolderName(prefs.getString(BTConfig.PROJECT_PATH));
		// project_path = prefs.getString(BTConfig.PROJECT_PATH);
		//

		GestureAdapter gesture = new GestureAdapter() {
			@Override
			public boolean pan(float x, float y, float deltaX, float deltaY) {
				// TODO Auto-generated method stub
				return super.pan(x, y, deltaX, deltaY);
			}
		};
		
		InputAdapter input = new InputAdapter() {
			
			boolean scroll = false;
			Vector2 trackPos = new Vector2();
			Vector2 camPos = new Vector2();
			
			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				if(scroll) {// && treeContainer.hit((float)screenX, (float)screenY, true)!=null) {
					System.out.println(String.format(" dragged x=%s , y=%s",screenX,screenY));
					if(workspace!=null && tabs.getSelectedIndex()!= -1) {
						Project p = workspace.getProjects().get(tabs.getSelectedIndex());
						GroupCamera cam = p.getCamera();
						camPos.set(Gdx.input.getX(),Gdx.input.getY());
						camPos.sub(trackPos);
						
						cam.translate(camPos.x * 1.5f, -camPos.y * 1.5f);
						trackPos.add(camPos);
						cam.update(true);
					}
				}
				
				return false;
			}
			
			
			
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				if(button == 1) {
					System.out.println("touch down");
					scroll = true;
					trackPos.set(Gdx.input.getX(),Gdx.input.getY());
				}
				return false;
			}
			
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				if(button == 1) {
					scroll = false;
				}
				return false;
			}
			
			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				
				
				return false;
			}
			
			
			@Override
			public boolean keyDown(int keycode) {
			
				return false;
			}
			
			@Override
			public boolean keyUp(int keycode) {
				return false;
			}
			
			
			
			// public boolean scrolled(int amount) {
			// if (!busy) {
			// float sa = 0.05f * amount;
			// float newScale = node_camera.getZoom() - sa;
			// if (newScale < 0.2f)
			// newScale = .2f;
			// if (newScale > 1f)
			// newScale = 1f;
			//
			// node_camera.setZoom(newScale);
			// // float dx = sa * stage.getWidth() / 2f;
			// // float dy = sa * stage.getHeight();
			// // node_camera.translate(-dx, dy);
			//
			// node_camera.update();
			// return true;
			// } else {
			// return false;
			// }
			// }
			//
			// @Override
			// public boolean keyTyped(char character) {
			//
			// return super.keyTyped(character);
			// }
			//
			// @Override
			// public boolean keyUp(int keycode) {
			//
			// if (busy)
			// return false;
			//
			// if (keycode == BTConfig.UNDO_KEY &&
			// Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
			// stage.unfocusAll();
			// undo();
			//
			// commands.size));
			// return true;
			// } else
			//
			// if (keycode == BTConfig.REDO_KEY &&
			// Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
			// stage.unfocusAll();
			// redo();
			//
			// return true;
			// }
			//
			// return super.keyUp(keycode);
			// }
			//
			// @Override
			// public boolean keyDown(int keycode) {
			// if (busy)
			// return false;
			//
			// if (keycode == BTConfig.UNDO_KEY &&
			// Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
			// stage.unfocusAll();
			// return true;
			// } else
			//
			// if (keycode == REDO_KEY && Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
			// stage.unfocusAll();
			// return true;
			// }
			//
			// return super.keyDown(keycode);
			// }
			//
			// @Override
			// public boolean touchDragged(int screenX, int screenY, int pointer) {
			// if (m_drag) {
			// float dx = (screenX - m_dragScreenX);
			// float dy = (screenY - m_dragScreenY);
			// m_dragScreenX = screenX;
			// m_dragScreenY = screenY;
			// // stage.getViewport().getCamera().translate(dx,dy,0);
			// node_camera.translate(-dx, -dy);
			// node_camera.update();
			// }
			// return true;
			// }
			//
			// @Override
			// public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			// m_drag = false;
			// if (button == 1 && busy!=true) {
			// m_dragScreenX = screenX;
			// m_dragScreenY = screenY;
			// m_drag = true;
			// }
			// return false;
			// }
			//
			// @Override
			// public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			// if (m_drag && button == 1) {
			// m_drag = false;
			// }
			//
			// return false;
			// }
		};
		
		
		
		// TODO: add input before stage to pickup shortcut keys -- see below as its
		// commented out
		Gdx.input.setInputProcessor(new InputMultiplexer(input,stage));
		
		//
		// chooser = new FileChooser(Gdx.files.external(last_save_path), Mode.OPEN);
		// chooser.getTitleTable().getCells().get(1).getActor().addListener(new
		// ClickListener() {
		// @Override
		// public void clicked(InputEvent event, float x, float y) {
		// busy = false;
		// }
		// });
		//
		// chooser.setSelectionMode(SelectionMode.FILES);
		// chooser.setFileFilter(new FileFilter() {
		// @Override
		// public boolean accept(File pathname) {
		// if (pathname.getName().endsWith(PERIOD + project_ext) ||
		// pathname.isDirectory())
		// return true;
		// return false;
		// }
		// });
		//
		// saver = new FileChooser(Gdx.files.external(last_save_path), Mode.SAVE);
		// saver.getTitleTable().getCells().get(1).getActor().addListener(new
		// ClickListener() {
		// @Override
		// public void clicked(InputEvent event, float x, float y) {
		// busy = false;
		// }
		// });
		//

		//
		// Group root = stage.getRoot();
		// tree_view = new Group();
		// tree_view.setTransform(true);
		// tree_view.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// // tree_view.setFillParent(true);
		// root.addActor(tree_view);
		//
		// node_camera = new GroupCamera(Gdx.graphics.getWidth(),
		// Gdx.graphics.getHeight(), tree_view);
		//
		// button_window = new Table();
		// button_window.setBackground(VisUI.getSkin().getDrawable("button"));
		// button_window.setSize(200, 80);
		//
		// ClickListener listener = new ClickListener() {
		// @Override
		// public void clicked(InputEvent event, float x, float y) {
		//
		// Actor a = event.getListenerActor();
		//
		// if (a == create) {
		// if (shouldSave()) {
		// saveProjectPrompt(CREATE);
		// } else {
		// createNewProjectPrompt();
		// }
		// }
		//
		// if (a == open) {
		// if (shouldSave()) {
		// saveProjectPrompt(OPEN);
		// } else {
		// openProject();
		// }
		// }
		//
		// if (a == save && current != null) {
		// if (isProjectUntitled())
		// saveProjectAs(CLOSE);
		// else
		// saveProject();
		// }
		//
		// if (a == saveas && current != null) {
		// saveProjectAs(CLOSE);
		// }
		//
		// if (a == config) {
		// openConfig();
		// }
		// }
		// };
		// save_window = new VisWindow("Would you like to save changes?");
		// save_window.setSize(350, 80);
		// save_project = new VisTextButton("Save");
		// close_project = new VisTextButton("Close");
		//
		// save_window.add().growX();
		// save_window.add(save_project).pad(5);
		// save_window.add(close_project).padRight(5);
		//
		// save_window.setModal(true);
		// save_window.setMovable(false);
		//
		// node_chooser = new VisWindow("Node Select");
		// // node_chooser.setMovable(false);
		// node_chooser.setModal(true);
		// node_chooser.setSize(500, 130);
		// node_chooser.addCloseButton();
		// nodetype_sel = new VisSelectBox<String>();
		// nodetype_sel.setItems("Composite", "Supplement", "Leaf");
		// nodetype_sel.addListener(new ChangeListener() {
		// @Override
		// public void changed(ChangeEvent event, Actor actor) {
		// setCorrectNodes();
		// }
		// });
		//
		// node_sel = new VisSelectBox<String>();
		// select = new VisTextButton("As Child");
		// insert = new VisTextButton("As Parent");
		//
		// node_chooser.add(new VisLabel("Type:")).align(Align.left);
		// node_chooser.add(nodetype_sel).padRight(5).growX();
		// node_chooser.add(new VisLabel("Node:")).align(Align.left);
		// node_chooser.add(node_sel).growX().row();
		//
		// node_chooser.add(insert).colspan(4).expandX().align(Align.right).padTop(5).row();
		// node_chooser.add(select).colspan(4).expandX().align(Align.right).padTop(5);
		//
		// node_chooser.getTitleTable().getCells().get(1).getActor().addListener(new
		// ClickListener() {
		// @Override
		// public void clicked(InputEvent event, float x, float y) {
		// busy = false;
		// }
		// });
		//
		// open = new VisTextButton("Open");
		// open.addListener(listener);
		// create = new VisTextButton("New");
		// create.addListener(listener);
		// save = new VisTextButton("Save");
		// save.addListener(listener);
		// saveas = new VisTextButton("Save As");
		// saveas.addListener(listener);
		// config = new VisTextButton("Project");
		// config.addListener(listener);
		//
		// projectLabel = new VisLabel();
		// projectLabel.setWidth(400);
		// projectLabel.setColor(Color.GREEN);
		//
		// button_window.add(create).pad(5);
		// button_window.add(open).pad(5);
		// button_window.add().pad(5);
		// button_window.add(save).pad(5);
		// button_window.add(saveas).pad(5);
		// button_window.add(config).pad(5);
		// button_window.add(projectLabel).pad(10);
		// button_window.pack();
		//
		// tt = new Table();
		// tt.align(Align.bottomLeft);
		// tt.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// tt.add(button_window).align(Align.bottomLeft);
		// stage.addActor(tt);
		//

		//
		// FileHandle nodes = Gdx.files.absolute(prefs.getString(NODES_FILE));
		// if (!nodes.exists()) {
		// nodes.writeString(Utils.DEFAULT_NODES, false);
		// }
		//
		// loadNodeTemplates(nodes);
		//
		// // handle project defaults now as well
		// FileHandle proj = Gdx.files.absolute(project_path + "/" + PROJ_FILE);
		// if (!proj.exists()) {
		// proj = Gdx.files.absolute(project_path + "/" + PROJ_FILE);
		// proj.writeString(Utils.DEFAULT_PROJ, false);
		// }
		//
		// loadProject(proj);
		// setProjectLabel();

		// FileHandle composites = Gdx.files.local("composite_nodes.txt");
		// FileHandle supplements = Gdx.files.local("supplement_nodes.txt");
		// FileHandle leafs = Gdx.files.local("leaf_nodes.txt");

		// if (!composites.exists()) {
		// composites.writeString(DEFAULT_COMPOSITES, false);
		// }
		// if (!supplements.exists()) {
		// supplements.writeString(DEFAULT_SUPPLEMENT, false);
		// }
		// if (!leafs.exists()) {
		// leafs.writeString("TestBehaviour", false);
		// }

		// String all_comps[] = composites.readString().split("\n");
		// for (int i = 0; i < all_comps.length; i++) {
		// Composites.add(all_comps[i].trim());
		// }
		//
		// String all_supp[] = supplements.readString().split("\n");
		// for (int i = 0; i < all_supp.length; i++) {
		// Supplements.add(all_supp[i].trim());
		// }
		//
		// String all_leafs[] = leafs.readString().split("\n");
		// for (int i = 0; i < all_leafs.length; i++) {
		// Leafs.add(all_leafs[i].trim());
		// }
		//
		// setCorrectNodes();

	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public Table getRoot() {
		return root;
	}

	public Table getTreeContainer() {
		return treeContainer;
	}

	public ProjectTabs getTabs() {
		return tabs;
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	public void openWorkspaceChooser() {
		projectFolderChooser.remove();
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				busy = true;
				stage.addActor(projectFolderChooser);
			}
		});

	}

	public boolean exit() {
		Gdx.app.log("exit()", "exit starting");
		if (busy) {
			Gdx.app.log("exit()", "editor is busy - exit failed");
			return false;
		}
		// if (shouldSave()) {
		// Gdx.app.log("exit()", "saveProjectPrompt must be shown");
		// saveProjectPrompt(CLOSE);
		// Gdx.app.log("exit()", "saveProjectPrompt am back from now.");
		// return false;
		// }
		Gdx.app.log("exit()", "exit finishing");
		return true;
	}

	@Override
	public void dispose() {
		prefs.flush();
		Assets.disposeTextures();
		stage.dispose();
		batch.dispose();
		background.getTexture().dispose();
	}

	public void centerBackground() {
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		background.setSize(width * .5f, height);
		Vector2 position = new Vector2(width * .5f - background.getWidth() * .5f,
				height * .5f - background.getHeight() * .5f);

		background.setPosition(position.x, position.y);

	}

	public void centerActor(Actor a) {
		Vector2 position = new Vector2(stage.getViewport().getScreenWidth() * .5f - a.getWidth() * .5f,
				stage.getViewport().getScreenHeight() * .5f - a.getHeight() * .5f);
		a.setPosition(position.x, position.y);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		background.draw(batch);
		batch.end();

		stage.act();

		stage.getViewport().apply();
		// ----recenter actors

		// --------

		stage.draw();

		eventManager.process(true);
	}

	@Override
	public void resize(int width, int height) {

		stage.getViewport().update(width, height, true);
		stage.getViewport().apply();

		root.remove();
		stage.addActor(root);

		centerActor(welcomePage);

		treeContainer.setSize(width - workspaceView.getWidth(), height - 80);

		Vector2 po = new Vector2(0, Gdx.graphics.getHeight());
		po = stage.getViewport().unproject(po);

		if (tabs.getSelectedIndex() != -1)
			workspace.getProjects().get(tabs.getSelectedIndex()).reAnchorRoot(treeContainer);

		// node_camera.resize(width, height);
		// node_camera.update();
		//
		// resetTreeViewSize();
		// tt.setPosition(po.x, po.y);
		
		if(palate!=null)
			palate.toFront();

	}

	// -----methods to tidy create

	private void createWorkSpaceChooser() {
		projectFolderChooser = new FileChooser(Gdx.files.external(""), Mode.OPEN);
		projectFolderChooser.getTitleLabel().setText("Choose Workspace");
		projectFolderChooser.setListener(new FileChooserListener() {
			@Override
			public void selected(Array<FileHandle> files) {
				busy = false;
				FileHandle dir = files.first();

				String workSpacePath = dir.file().getAbsolutePath();
				Gdx.app.log("workspaceChooser", "dir chosen:" + workSpacePath);
				openWorkSpace(workSpacePath);
			}

			@Override
			public void canceled() {
				Gdx.app.log("workspaceChooser", "cancelled");
			}

		});
		projectFolderChooser.getTitleTable().getCells().get(1).getActor().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				busy = false;
			}
		});

		projectFolderChooser.setSelectionMode(SelectionMode.DIRECTORIES);
	}

	/**
	 * attempt to open the workspace - return false if unable to open path
	 * 
	 * @param path
	 * @return
	 */
	public boolean openWorkSpace(String path) {
		if (path.isEmpty())
			return false;
		System.out.println("workspace opened :" + path);
		workspace = new Workspace(path, this);
		prefs.putString(BTConfig.RECENT_PROJECT, path);
		welcomePage.remove();
		workspaceContainer.add(workspaceView).grow();
		workspaceView.refresh(path);
		busy = false;
		return true;
	}

	// -----

	//
	// public void setClipboard(BehaviorNode clipboard) {
	// this.clipboard = clipboard;
	// }
	//
	// public BehaviorNode getClipboard() {
	// return clipboard;
	// }
	//
	// public void setSelectedNode(BehaviorNode selected) {
	// if(this.selected != null) {
	// this.selected.node_table.setColor(Color.WHITE);
	// if(selected != null)
	// selected.node_table.setColor(Color.LIGHT_GRAY);
	// }
	// this.selected = selected;
	//
	// }
	//
	// public BehaviorNode getSelectedNode() {
	// return this.selected;
	// }
	//
	// public void loadProject(FileHandle proj) {
	// JsonValue root = new JsonReader().parse(proj);
	//
	// JsonValue p_type = root.get("type");
	// JsonValue p_ext = root.get("ext");
	// project_ext = p_ext.asString();
	// project_type = p_type.asString();
	// Gdx.app.log("loadProject", "ext=" + p_ext);
	// }
	//
	// /**
	// * get the node templates from the file
	// *
	// * @param nodes
	// */

	//
	// public void setCurrentProjectFolderName(String path) {
	// String[] p = path.split("/");
	// int i = p.length;
	// m_currentProjectFolderName = p[i - 1];
	// }
	//
	// public String makeRecentProjectLabel(String path) {
	// String[] p = path.split("/");
	// int i = p.length;
	// return p[i - 1];
	// }
	//
	// public void changeCurrentProject(FileHandle f) {
	// if (f.path() != project_path) {
	// project_path_recent = project_path;
	// prefs.putString(RECENT_PROJECT, project_path_recent);
	// last_save_path = f.path();
	// project_path = f.path();
	//
	// // check if default_nodes.json is there, create it if not, and load new
	// // templates
	// String nodesFilePath = f.path() + "/" + DEFAULT_NODES_FILE;
	// FileHandle nodes = Gdx.files.absolute(nodesFilePath);
	// if (!nodes.exists()) {
	// nodes.writeString(Utils.DEFAULT_NODES, false);
	// }
	//
	// loadNodeTemplates(nodes);
	// prefs.putString(NODES_FILE, nodesFilePath);
	// prefs.putString(LAST_SAVE_PATH, f.path());
	// prefs.putString(PROJECT_PATH, f.path());
	//
	// FileHandle proj = Gdx.files.absolute(project_path + "/" + PROJ_FILE);
	// if (!proj.exists()) {
	// proj.writeString(Utils.DEFAULT_PROJ, false);
	// }
	//
	// loadProject(proj);
	//
	// project_ext_textfield.setText(project_ext);
	// project_type_textfield.setText(project_type);
	// setCurrentProjectFolderName(f.path());
	// node_file_textfield.setText(m_currentProjectFolderName);
	// setProjectLabel();
	//
	// if (project_path_recent == null) {
	// recent_project_button.setVisible(false);
	// recent_project_button.setText("None");
	// } else {
	// recent_project_button.setVisible(true);
	// recent_project_button.setText(makeRecentProjectLabel(project_path_recent));
	// }
	// }
	// }
	//
	// public void setProjectLabel() {
	// projectLabel.setText(m_currentProjectFolderName + " : ." + project_ext + " :
	// " + project_type);
	// }
	//
	// public void openConfig() {
	// if (config_window == null) {
	// config_window = new VisWindow("Open / Create Project");
	//
	// node_file_label = new VisLabel("Project Folder:");
	// node_file_textfield = new VisTextField();
	//
	// project_ext_label = new VisLabel("File Extension:");
	// project_ext_textfield = new VisTextField();
	// project_type_label = new VisLabel("Project Type:");
	// project_type_textfield = new VisTextField();
	//
	// project_ext_textfield.setText(project_ext);
	// project_type_textfield.setText(project_type);
	//
	// VisLabel vlabel = new VisLabel("Last Project:");
	// recent_project_button = new VisTextButton("---------------------------");
	// recent_project_button.addListener(new ClickListener() {
	// @Override
	// public void clicked(InputEvent event, float x, float y) {
	// FileHandle file = Gdx.files.absolute(project_path_recent);
	// changeCurrentProject(file);
	// }
	// });
	//
	// node_file_change_button = new VisTextButton("Change");
	// node_file_change_button.addListener(new ClickListener() {
	// @Override
	// public void clicked(InputEvent event, float x, float y) {
	// projectFolderChooser.setDirectory(project_path);
	// centerActor(projectFolderChooser);
	// busy = true;
	// projectFolderChooser.setListener(new SingleFileChooserListener() {
	// @Override
	// protected void selected(FileHandle f) {
	// changeCurrentProject(f);
	//
	// busy = false;
	// dirty = false;
	// }
	// });
	//
	// stage.addActor(projectFolderChooser);
	// }
	// });
	//
	// accept_config = new VisTextButton("Close");
	// accept_config.addListener(new ClickListener() {
	// @Override
	// public void clicked(InputEvent event, float x, float y) {
	// /*
	// * String filepath = node_file_textfield.getText();
	// * if(Gdx.files.external(filepath).exists()) { prefs.putString(NODES_FILE,
	// * node_file_textfield.getText()); }
	// */
	// busy = false;
	// project_ext = project_ext_textfield.getText();
	// project_type = project_type_textfield.getText();
	//
	// writeProjectFile();
	// setProjectLabel();
	//
	// config_window.remove();
	// }
	// });
	//
	// config_window.setSize(500, 280);
	// config_window.add(vlabel).padTop(5).pad(5);
	// config_window.add(recent_project_button).growX().row();
	//
	// config_window.add(node_file_label).pad(5);
	// config_window.add(node_file_textfield).growX();
	// config_window.add(node_file_change_button).pad(5).growX().row();
	// config_window.add(project_ext_label).padTop(5).pad(5).growX();
	// config_window.add(project_ext_textfield).growX().row();
	//
	// config_window.add(project_type_label).padTop(5).pad(5).growX();
	// config_window.add(project_type_textfield).growX().row();
	//
	// config_window.add().growX();
	// config_window.add(accept_config).align(Align.right);
	// config_window.setModal(true);
	// config_window.addCloseButton();
	// }
	//
	// if (project_path_recent == null) {
	// recent_project_button.setVisible(false);
	// recent_project_button.setText("None");
	// } else {
	// recent_project_button.setVisible(true);
	// recent_project_button.setText(makeRecentProjectLabel(project_path_recent));
	// }
	//
	// if (m_currentProjectFolderName != null) {
	// node_file_textfield.setText(m_currentProjectFolderName);
	// }
	//
	// centerActor(config_window);
	// stage.addActor(config_window);
	//
	// }
	//

	//

	//

	//
	// public void setDirty() {
	// dirty = true;
	// Gdx.graphics.setTitle(TITLE + " - " + project_name + " *(unsaved)");
	// }
	//
	// public void setCorrectNodes() {
	//
	// NodeType type = NodeType.valueOf(nodetype_sel.getSelected().toUpperCase());
	// switch (type) {
	// case COMPOSITE:
	// NodeTemplate.templatesToStringArray(composite_nodes, items);
	// break;
	// case SUPPLEMENT:
	// NodeTemplate.templatesToStringArray(supplement_nodes, items);
	// break;
	// case LEAF:
	// NodeTemplate.templatesToStringArray(leaf_nodes, items);
	// break;
	// default:
	// break;
	// }
	//
	// node_sel.setItems(items);
	// }
	//
	// /**
	// * create a new project
	// *
	// * @param name
	// * @param root
	// */
	// public void createNewProject(String name, BehaviorNode root) {
	// Gdx.app.log("createNewProject", "start " + name);
	// resetTreeView();
	// centerNode(root);
	// NodeTemplate template = getTemplate(root.getNodeName(),
	// root.getNode().getNodeType());
	// if (template != null) {
	// Gdx.app.log("createNewProject", "template = " + template);
	// template.properitize(root.getNode());
	// root.createProperties();
	// root.layout();
	// }
	//
	// project_name = name;
	// CreateNodeCommand command = new CreateNodeCommand(this, root, null, -1);
	// addAndExecuteCommand(command);
	//
	// Gdx.graphics.setTitle(TITLE + " - " + project_name);
	//
	// }
	//
	// public void centerNode(BehaviorNode root) {
	// float ax = root.getAnchorX();
	// float ay = root.getAnchorY();
	// float w = root.getWidth();
	// float h = root.getHeight();
	// float sw = tree_view.getWidth();
	// float sh = tree_view.getHeight();
	//
	// float nx = (sw * .5f) - (w * .5f);
	// float ny = (sh * .9f) - (h * 1f);
	//
	// root.setPosition(nx, ny);
	// root.setAnchorPos(nx, ny + root.getHeight());
	// }
	//
	// public void createNewProjectPrompt() {
	//
	// if (m_defaultRootTemplate != null) {
	// String nodetype = nodetype_sel.getSelected();
	// String name = node_sel.getSelected();
	// Node newNode = new Node();
	// newNode.setNodeType(NodeType.valueOf(nodetype.toUpperCase()));
	// newNode.setName(name);
	// BehaviorNode node = new BehaviorNode(BTreeEditor.this, newNode);
	// createNewProject(DEFAULT_NAME, node);
	// node.updateArrows();
	// busy = false;
	// } else {
	// // show node chooser only if default not defined for new project
	// busy = true;
	// centerActor(node_chooser);
	// select.clearListeners();
	// insert.clearListeners();
	// select.addListener(new ClickListener() {
	// @Override
	// public void clicked(InputEvent event, float x, float y) {
	// String nodetype = nodetype_sel.getSelected();
	// String name = node_sel.getSelected();
	// Node newNode = new Node();
	// newNode.setNodeType(NodeType.valueOf(nodetype));
	// newNode.setName(name);
	// BehaviorNode node = new BehaviorNode(BTreeEditor.this, newNode);
	// createNewProject(DEFAULT_NAME, node);
	// node.updateArrows();
	// node_chooser.fadeOut();
	// busy = false;
	// }
	// });
	//
	// insert.addListener(new ClickListener() {
	// @Override
	// public void clicked(InputEvent event, float x, float y) {
	// String nodetype = nodetype_sel.getSelected();
	// String name = node_sel.getSelected();
	// Node newNode = new Node();
	// newNode.setNodeType(NodeType.valueOf(nodetype.toUpperCase()));
	// newNode.setName(name);
	// BehaviorNode node = new BehaviorNode(BTreeEditor.this, newNode);
	// createNewProject(DEFAULT_NAME, node);
	// node.updateArrows();
	// node_chooser.fadeOut();
	// busy = false;
	// }
	// });
	//
	// stage.addActor(node_chooser);
	// }
	// }
	//

	//
	// public void centerCamera(Actor node) {
	// // Gdx.app.log("CenterCamera", "nodex = " + node.getX());
	// // stage.getViewport().getCamera().lookAt(node.getX(), node.getY(),
	// // stage.getViewport().getCamera().position.z);
	// }
	//
	// public void createNewNode(final BehaviorNode parent) {
	// busy = true;
	// centerActor(node_chooser);
	// select.clearListeners();
	// insert.clearListeners();
	//
	// select.addListener(new ClickListener() {
	// @Override
	// public void clicked(InputEvent event, float x, float y) {
	// String nodetype = nodetype_sel.getSelected();
	// String name = node_sel.getSelected();
	// Node newNode = new Node();
	// newNode.setNodeType(NodeType.valueOf(nodetype));
	// newNode.setName(name);
	//
	// BehaviorNode node = new BehaviorNode(BTreeEditor.this, newNode);
	// NodeTemplate template = getTemplate(name,
	// NodeType.valueOf(nodetype.toUpperCase()));
	// if (template != null) {
	// template.properitize(newNode);
	// node.createProperties();
	// }
	// if (parent != null) {
	// CreateNodeCommand c = new CreateNodeCommand(BTreeEditor.this, node, parent,
	// -1);
	// addAndExecuteCommand(c);
	// }
	// node_chooser.fadeOut();
	// busy = false;
	// centerCamera(node);
	// }
	// });
	//
	// insert.addListener(new ClickListener() {
	// @Override
	// public void clicked(InputEvent event, float x, float y) {
	// String nodetype = nodetype_sel.getSelected();
	// String name = node_sel.getSelected();
	// Node newNode = new Node();
	// newNode.setNodeType(NodeType.valueOf(nodetype));
	// newNode.setName(name);
	// BehaviorNode node = new BehaviorNode(BTreeEditor.this,newNode);
	// NodeTemplate template = getTemplate(name,
	// NodeType.valueOf(nodetype.toUpperCase()));
	// if (template != null) {
	// template.properitize(newNode);
	// node.createProperties();
	// }
	//
	// if (nodetype.equals("Leaf")) {
	// // can't actually add above, so adds below.
	// if (parent != null) {
	// CreateNodeCommand c = new CreateNodeCommand(BTreeEditor.this, node, parent,
	// -1);
	// addAndExecuteCommand(c);
	// }
	// node_chooser.fadeOut();
	// busy = false;
	// return;
	// }
	//
	// if (parent != null) {
	// BehaviorNode parentParent = parent.parent;
	//
	// if (parentParent != null)
	// parentParent.removeNode(parent);
	//
	// node.addNode(parent);
	//
	// if (parentParent != null)
	// parentParent.addNode(node);
	// else {
	// // we've replaced root node!
	// resetTreeView();
	// node.setPosition(tree_view.getWidth() * .45f, tree_view.getHeight() * .8f);
	// project_name = name;
	// Gdx.graphics.setTitle(TITLE + " - " + project_name);
	// tree_view.removeActor(current);
	// current = node;
	// tree_view.addActor(node);
	// setDirty();
	// }
	//
	// }
	// centerCamera(node);
	// node_chooser.fadeOut();
	// busy = false;
	// }
	// });
	//
	// stage.addActor(node_chooser);
	// }
	//
	// public void openProject() {
	// busy = true;
	// centerActor(chooser);
	// chooser.setDirectory(last_save_path);
	// chooser.setListener(new SingleFileChooserListener() {
	// @Override
	// protected void selected(FileHandle file) {
	// if (!file.extension().equals(project_ext))
	// throw new IllegalArgumentException(file.extension() + " is not a supported
	// filetype");
	//
	// String name = file.name();
	// String path = file.path().replaceAll(name, "");
	//
	// last_save_path = path;
	// project_name = name;
	// String data = file.readString();
	//
	// JsonValue value = reader.parse(data);
	// current = BehaviorNode.fromJson(BTreeEditor.this, value);
	// centerNode(current);
	// current.layout();
	// resetTreeView();
	// tree_view.addActor(current);
	// busy = false;
	// dirty = false;
	//
	// Gdx.graphics.setTitle(TITLE + " - " + project_name);
	// }
	// });
	//
	// stage.addActor(chooser);
	// }
	//
	// public void resetTreeView() {
	// tree_view.clear();
	// node_camera.setPosition(0, 0);
	// node_camera.setZoom(1f);
	// node_camera.update();
	// resetTreeViewSize();
	// }
	//
	// public void resetTreeViewSize() {
	// tree_view.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	//
	// }
	//
	// public void saveProjectAs(final int type) {
	// busy = true;
	//
	// saver.setDirectory(last_save_path);
	// saver.setListener(new SingleFileChooserListener() {
	// @Override
	// protected void selected(FileHandle file) {
	// busy = false;
	// String name = file.name();
	// if (!name.contains("." + project_ext)) {
	// String path = file.path() + "." + project_ext;
	// file.delete();
	// file = Gdx.files.absolute(path);
	// }
	//
	// project_name = name;
	// String path = file.parent().path();
	// last_save_path = path;
	// prefs.putString("last_save_path", last_save_path);
	// String data = "{ \n";
	// data += current.getJson(1);
	// data += "\n}";
	// file.writeString(data, false);
	// if (type == CREATE)
	// createNewProjectPrompt();
	// if (type == OPEN)
	// openProject();
	// Gdx.graphics.setTitle(TITLE + " - " + project_name);
	// }
	// });
	//
	// centerActor(saver);
	// stage.addActor(saver);
	// dirty = false;
	// }
	//
	// public void saveProject() {
	// FileHandle file = Gdx.files.absolute(last_save_path + "/" + project_name);
	// String data = "{ \n";
	// data += current.getJson(1);
	// data += "\n}";
	// file.writeString(data, false);
	// save_window.fadeOut();
	// Gdx.graphics.setTitle(TITLE + " - " + project_name);
	// busy = false;
	// dirty = false;
	// }
	//
	// /**
	// * cuts the command list at the current command index and then adds a command
	// to
	// * the command list and executes it
	// *
	// * @param command
	// */
	// public void addAndExecuteCommand(ICommand command) {
	// commands.truncate(command_index + 1);
	// commands.add(command);
	// command.execute();
	// command_index = commands.size - 1;
	// String s = String.format("Execute: %s", command.desc());
	// System.out.println(s);
	// }
	//
	// /**
	// * undo the current command index and then walk back the command_index
	// */
	// public void undo() {
	// if (command_index - 1 >= 0) {
	// ICommand c = commands.get(command_index);
	// c.undo();
	// command_index -= 1;
	// String s = String.format("Undo: %s", c.desc());
	// System.out.println(s);
	// }
	//
	// }
	//
	// /**
	// * walk forward the command index and execute the next command
	// */
	// public void redo() {
	// if (command_index + 1 < commands.size) {
	// command_index++;
	// ICommand c = commands.get(command_index);
	// c.execute();
	// String s = String.format("Redo: %s", c.desc());
	// System.out.println(s);
	// }
	// }
	//

	//
	// public boolean shouldSave() {
	// if (current != null && dirty)
	// return true;
	// else
	// return false;
	// }
	//
	// public boolean isProjectUntitled() {
	// if (project_name.equalsIgnoreCase(DEFAULT_NAME))
	// return true;
	// else
	// return false;
	// }
	//
	// public void saveProjectPrompt(final int type) {
	// busy = true;
	// final Action close_action = Actions.sequence(Actions.fadeOut(.3f), new
	// Action() {
	// @Override
	// public boolean act(float delta) {
	// dirty = false;
	// busy = false;
	// Gdx.app.exit();
	// return true;
	// }
	// });
	//
	// centerActor(save_window);
	// stage.addActor(save_window);
	//
	// save_project.clearActions();
	// save_project.clearListeners();
	//
	// close_project.clearActions();
	// close_project.clearListeners();
	//
	// save_project.addListener(new ClickListener() {
	//
	// @Override
	// public void clicked(InputEvent event, float x, float y) {
	//
	// switch (type) {
	//
	// case CLOSE:
	// if (isProjectUntitled()) {
	// saveProjectAs(type);
	// save_window.fadeOut();
	// } else {
	// saveProject();
	// save_window.addAction(close_action);
	// }
	// break;
	// case OPEN:
	// break;
	// case CREATE:
	//
	// if (isProjectUntitled()) {
	// saveProjectAs(type);
	// save_window.fadeOut();
	// } else {
	// saveProject();
	// createNewProjectPrompt();
	// }
	//
	// break;
	// }
	// }
	// });
	//
	// close_project.addListener(new ClickListener() {
	// @Override
	// public void clicked(InputEvent event, float x, float y) {
	//
	// if (type == CLOSE) {
	// save_window.addAction(close_action);
	// dirty = false;
	// busy = false;
	// } else {
	// save_window.fadeOut();
	// busy = false;
	// }
	// }
	// });
	// }
	//

	//
	// public void writeProjectFile() {
	// FileHandle proj = Gdx.files.absolute(project_path + "/" + PROJ_FILE);
	// if (!proj.exists()) {
	// return;
	// }
	//
	// String tmp = "{\r\n" + //
	// " \"type\": \"%TYPE%\",\r\n \"ext\": \"%EXT%\" \r\n}";
	//
	// tmp = tmp.replaceAll("%TYPE%", project_type);
	// tmp = tmp.replaceAll("%EXT%", project_ext);
	//
	// // Gdx.app.log("TMP",tmp);
	//
	// proj.writeString(tmp, false);
	// }

}
