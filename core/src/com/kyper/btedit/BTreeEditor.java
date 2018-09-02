package com.kyper.btedit;

import java.io.File;
import java.io.FileFilter;

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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;
import com.kyper.btedit.BehaviorNode.NodeType;

public class BTreeEditor extends ApplicationAdapter {
	
	private SpriteBatch batch;
	private Sprite background;
	
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;

	public static String VERSION = "0.1"; 
	public static String TITLE = "BT Edit v"+VERSION;
	public static String DEFAULT_NAME = "Untitled";
	public static String DEFAULT_PATH = "";
	public static String PREF_NAME = "BTreeEditor_Config";
	public static String EXTENSION = "btree";
	final static String PERIOD = ".";
	final static String FORWARD_DASH = "/";
	final static String BACK_DASH = "\\";

	final static int CLOSE = 0;
	final static int OPEN = 1;
	final static int CREATE = 2;

	Stage stage;

	public Table tree_view;

	public Table button_window;

	VisTextButton create, open, save, saveas;

	public BehaviorNode current;
	public String project_name;
	public String last_save_path;

	public static final String DEFAULT_COMPOSITES = "SequenceNode \n" + "SelectorNode \n" + "RandomSequenceNode \n"
			+ "RandomSelectorNode \n";

	public static final String DEFAULT_SUPPLEMENT = "InvertNode \n" + "SucccessNode \n" + "RepeatNode \n"
			+ "RepeatUntilFailNode \n";

	public Preferences prefs;

	public Array<String> Composites;
	public Array<String> Supplements;
	public Array<String> Leafs;

	public VisWindow node_chooser;
	public VisSelectBox<String> nodetype_sel;
	public VisSelectBox<String> node_sel;
	public VisTextButton select;

	public VisWindow save_window;
	public VisTextButton save_project, close_project;

	public boolean dirty = false;
	public boolean busy = false;

	public FileChooser chooser;
	public FileChooser saver;

	public static JsonReader reader;

	public BehaviorNode editing; //node currently being edited

	Table tt;

	public BehaviorNode last_chosen_node;


	@Override
	public void create() {

		reader = new JsonReader();

		background = new Sprite(new Texture(Gdx.files.internal("background.png")));
		background.setColor(new Color(Color.WHITE.r,Color.WHITE.g,Color.WHITE.b,.2f));
		centerBackground();

		FileChooser.setDefaultPrefsName("com.kyper.btedit.filechooser");
		last_chosen_node = null;

		Composites = new Array<String>();
		Supplements = new Array<String>();
		Leafs = new Array<String>();

		project_name = "Untitled";

		prefs = Gdx.app.getPreferences(PREF_NAME);

		if (!prefs.contains("last_save_path")) {
			prefs.putString("last_save_path", "");
		}
		last_save_path = prefs.getString("last_save_path");
		VisUI.load(SkinScale.X1);
		stage = new Stage(new ScreenViewport());
		//stage.setDebugAll(true);

		InputAdapter input = new InputAdapter() {
			public boolean scrolled(int amount) {
				if (!busy) {
					tree_view.setScale(tree_view.getScaleX() - (amount * .1f));
					return true;
				} else {
					return false;
				}
			}
		};

		chooser = new FileChooser(Gdx.files.absolute(last_save_path), Mode.OPEN);
		chooser.getTitleTable().getCells().get(1).getActor().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				busy = false;
			}
		});
		chooser.setSelectionMode(SelectionMode.FILES);
		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.getName().endsWith(PERIOD + EXTENSION) || pathname.isDirectory())
					return true;
				return false;
			}
		});

		saver = new FileChooser(Gdx.files.absolute(last_save_path), Mode.SAVE);
		saver.getTitleTable().getCells().get(1).getActor().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				busy = false;
			}
		});
		Group root = stage.getRoot();
		tree_view = new Table();
		tree_view.setTransform(true);
		tree_view.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//tree_view.setFillParent(true);
		root.addActor(tree_view);

		button_window = new Table();
		button_window.setBackground(VisUI.getSkin().getDrawable("button"));
		button_window.setSize(200, 80);

		ClickListener listener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

				Actor a = event.getListenerActor();

				if (a == create) {
					if (shouldSave()) {
						saveProjectPrompt(CREATE);
					} else {
						createNewProjectPrompt();
					}
				}

				if (a == open) {
					if (shouldSave()) {
						saveProjectPrompt(OPEN);
					} else {
						openProject();
					}
				}

				if (a == save && current!=null) {
					if (isProjectUntitled())
						saveProjectAs(CLOSE);
					else
						saveProject();
				}

				if (a == saveas  && current!=null) {
					saveProjectAs(CLOSE);
				}
			}
		};	
		save_window = new VisWindow("Would you like to save changes?");
		save_window.setSize(350, 80);
		save_project = new VisTextButton("Save");
		close_project = new VisTextButton("Close");

		save_window.add().growX();
		save_window.add(save_project).pad(5);
		save_window.add(close_project).padRight(5);

		save_window.setModal(true);
		save_window.setMovable(false);

		node_chooser = new VisWindow("Node Select");
		//node_chooser.setMovable(false);
		node_chooser.setModal(true);
		node_chooser.setSize(500, 100);
		node_chooser.addCloseButton();
		nodetype_sel = new VisSelectBox<String>();
		nodetype_sel.setItems("Composite", "Supplement", "Leaf");
		nodetype_sel.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setCorrectNodes();
			}
		});
		node_sel = new VisSelectBox<String>();
		select = new VisTextButton("Select");

		node_chooser.add(new VisLabel("Type:")).align(Align.left);
		node_chooser.add(nodetype_sel).padRight(5).growX();
		node_chooser.add(new VisLabel("Node:")).align(Align.left);
		node_chooser.add(node_sel).growX().row();
		node_chooser.add(select).colspan(4).expandX().align(Align.right).padTop(5);
		node_chooser.getTitleTable().getCells().get(1).getActor().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				busy = false;
			}
		});

		open = new VisTextButton("Open");
		open.addListener(listener);
		create = new VisTextButton("New");
		create.addListener(listener);
		save = new VisTextButton("Save");
		save.addListener(listener);
		saveas = new VisTextButton("Save As");
		saveas.addListener(listener);

		button_window.add(create).pad(5);
		button_window.add(open).pad(5);
		button_window.add().pad(5);
		button_window.add(save).pad(5);
		button_window.add(saveas).pad(5);
		button_window.pack();

		tt = new Table();
		tt.align(Align.bottomLeft);
		tt.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		tt.add(button_window).align(Align.bottomLeft);
		stage.addActor(tt);

		Gdx.input.setInputProcessor(new InputMultiplexer(input, stage));

		FileHandle composites = Gdx.files.local("composite_nodes.txt");
		FileHandle supplements = Gdx.files.local("supplement_nodes.txt");
		FileHandle leafs = Gdx.files.local("leaf_nodes.txt");

		if (!composites.exists()) {
			composites.writeString(DEFAULT_COMPOSITES, false);
		}
		if (!supplements.exists()) {
			supplements.writeString(DEFAULT_SUPPLEMENT, false);
		}
		if (!leafs.exists()) {
			leafs.writeString("TestBehaviour", false);
		}

		String all_comps[] = composites.readString().split("\n");
		for (int i = 0; i < all_comps.length; i++) {
			Composites.add(all_comps[i].trim());
		}

		String all_supp[] = supplements.readString().split("\n");
		for (int i = 0; i < all_supp.length; i++) {
			Supplements.add(all_supp[i].trim());
		}

		String all_leafs[] = leafs.readString().split("\n");
		for (int i = 0; i < all_leafs.length; i++) {
			Leafs.add(all_leafs[i].trim());
		}

		setCorrectNodes();
		
		batch = new SpriteBatch();
	}

	public void centerBackground() {
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		background.setSize(width * .5f, height);
		Vector2 position = new Vector2(width * .5f - background.getWidth() * .5f,
				height * .5f - background.getHeight() * .5f);
		
		background.setPosition(position.x, position.y);

	}

	public void setDirty() {
		dirty = true;
		Gdx.graphics.setTitle(TITLE + " - " + project_name + " *(unsaved)");
	}

	public void setCorrectNodes() {

		Array<String> items = null;
		NodeType type = NodeType.valueOf(nodetype_sel.getSelected().toUpperCase());
		switch (type) {
		case COMPOSITE:
			items = Composites;
			break;
		case SUPPLEMENT:
			items = Supplements;
			break;
		case LEAF:
			items = Leafs;
			break;
		default:
			break;
		}

		node_sel.setItems(items);
	}

	/**
	 * create a new project
	 * 
	 * @param name
	 * @param root
	 */
	public void createNewProject(String name, BehaviorNode root) {
		resetTreeView();
		root.setPosition(tree_view.getWidth() * .45f, tree_view.getHeight() * .8f);
		current = root;
		project_name = name;
		Gdx.graphics.setTitle(TITLE + " - " + project_name);
		tree_view.addActor(root);
		setDirty();
	}

	public void createNewProjectPrompt() {
		busy = true;
		centerActor(node_chooser);
		select.clearListeners();
		select.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String nodetype = nodetype_sel.getSelected();
				String name = node_sel.getSelected();
				BehaviorNode node = new BehaviorNode(BTreeEditor.this, NodeType.valueOf(nodetype.toUpperCase()), name);
				createNewProject(DEFAULT_NAME, node);
				node_chooser.fadeOut();
				busy = false;
			}
		});
		stage.addActor(node_chooser);
	}

	public void createNewNode(final BehaviorNode parent) {
		busy = true;
		centerActor(node_chooser);
		select.clearListeners();
		select.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String nodetype = nodetype_sel.getSelected();
				String name = node_sel.getSelected();
				BehaviorNode node = new BehaviorNode(BTreeEditor.this, NodeType.valueOf(nodetype.toUpperCase()), name);
				if (parent != null) {
					parent.addNode(node);
				}
				node_chooser.fadeOut();
				busy = false;
			}
		});
		stage.addActor(node_chooser);
	}

	public void openProject() {
		busy = true;
		centerActor(chooser);
		chooser.setListener(new SingleFileChooserListener() {
			@Override
			protected void selected(FileHandle file) {
				if (!file.extension().equals(EXTENSION))
					throw new IllegalArgumentException(file.extension() + " is not a supported filetype");

				String name = file.name();
				String path = file.path().replaceAll(name, "");

				last_save_path = path;
				project_name = name;
				String data = file.readString();

				JsonValue value = reader.parse(data);
				current = BehaviorNode.fromJson(BTreeEditor.this, value);
				centerActor(current);
				resetTreeView();
				tree_view.addActor(current);
				busy = false;
				dirty = false;

				Gdx.graphics.setTitle(TITLE + " - " + project_name);
			}
		});

		stage.addActor(chooser);
	}

	public void resetTreeView() {
		tree_view.clear();
		tree_view.setPosition(0, 0);
		tree_view.setScale(1f);
	}

	public void saveProjectAs(final int type) {
		busy = true;
		
		saver.setDirectory(last_save_path);
		saver.setListener(new SingleFileChooserListener() {
			@Override
			protected void selected(FileHandle file) {
				busy = false;
				String name = file.name();
				project_name = name;
				String path = file.path().replaceAll(name, "");
				last_save_path = path;
				prefs.putString("last_save_path", last_save_path);
				String data = "{ \n";
				data += current.getJson(1);
				data += "\n}";
				file.writeString(data, false);
				if (type == CREATE)
					createNewProjectPrompt();
				if (type == OPEN)
					openProject();
				Gdx.graphics.setTitle(TITLE + " - " + project_name);
			}
		});

		centerActor(saver);
		stage.addActor(saver);
		dirty = false;
	}

	public void saveProject() {
		FileHandle file = Gdx.files.absolute(last_save_path + "/" + project_name);
		String data = "{ \n";
		data += current.getJson(1);
		data += "\n}";
		file.writeString(data, false);
		save_window.fadeOut();
		Gdx.graphics.setTitle(TITLE + " - " + project_name);
		busy = false;
		dirty = false;
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
		stage.draw();

		
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
		//stage.getViewport().apply();

		Vector2 po = new Vector2(0, Gdx.graphics.getHeight());
		po = stage.getViewport().unproject(po);
		tree_view.setSize(width, height);
		tt.setPosition(po.x, po.y);


	}

	public boolean shouldSave() {
		if (current != null && dirty)
			return true;
		else
			return false;
	}

	public boolean isProjectUntitled() {
		if (project_name.equalsIgnoreCase(DEFAULT_NAME))
			return true;
		else
			return false;
	}

	public void saveProjectPrompt(final int type) {
		busy = true;
		final Action close_action = Actions.sequence(Actions.fadeOut(.3f), new Action() {
			@Override
			public boolean act(float delta) {
				dirty = false;
				busy = false;
				Gdx.app.exit();
				return true;
			}
		});

		centerActor(save_window);
		stage.addActor(save_window);

		save_project.clearActions();
		save_project.clearListeners();

		close_project.clearActions();
		close_project.clearListeners();

		save_project.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {

				switch (type) {
				case CLOSE:
					if (isProjectUntitled()) {
						saveProjectAs(type);
						save_window.fadeOut();
					} else {
						saveProject();
						save_window.addAction(close_action);
					}
					break;
				case OPEN:
					break;
				case CREATE:

					if (isProjectUntitled()) {
						saveProjectAs(type);
						save_window.fadeOut();
					} else {
						saveProject();
						createNewProjectPrompt();
					}

					break;
				}
			}
		});

		close_project.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

				if (type == CLOSE) {
					save_window.addAction(close_action);
				} else {
					save_window.fadeOut();
					busy = false;
				}
			}
		});
	}

	private void centerActor(Actor a) {
		Vector2 position = new Vector2(Gdx.graphics.getWidth() * .5f - a.getWidth() * .5f,
				Gdx.graphics.getHeight() * .5f - a.getHeight() * .5f);
		a.setPosition(position.x, position.y);
	}

	public boolean exit() {
		if (busy)
			return false;
		if (shouldSave()) {
			saveProjectPrompt(CLOSE);
			return false;
		}
		return true;
	}

	@Override
	public void dispose() {
		prefs.flush();
		stage.dispose();
		batch.dispose();
		background.getTexture().dispose();
	}

}
