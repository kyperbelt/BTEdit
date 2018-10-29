package com.kyper.btedit;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kyper.btedit.NodeProperties.NodeProperty;
import com.kyper.btedit.command.MoveNodeCommand;
import com.kyper.btedit.command.RemoveNodeCommand;

public class BehaviorNode extends Group {
	
	private static final float WIDTH = 200;
	private static final float HEIGHT = 120;
	private static final float ROOTPAD = 20;
	private static final float FADE = .4f;

	public static int NWIDTH = 200;
	public static int NHEIGHT = 100;

	public static enum NodeType {
		SUPPLEMENT, COMPOSITE, LEAF
	}

	String nodename;
	public NodeType type;

	Table node_table;
	Table header;
	VisLabel name_label;
	Table button_table;
	ImageButton left;
	ImageButton right;
	ImageButton add;
	ImageButton remove;
	ImageButton down;
	Table property_table;
	Table property_container;

	private boolean properties_shown = false;

	private float anchor_x;
	private float anchor_y;
	private boolean anchored = true;

	private float original_x;
	private float original_y;

	private ClickListener listener;

	public NodeProperties properties;

	BTreeEditor editor;

	protected BehaviorNode parent;
	protected Array<BehaviorNode> children;

	Action laytou_action = new Action() {
		@Override
		public boolean act(float delta) {
			layout();
			return true;
		}
	};

	boolean p_check = false;

	public BehaviorNode(final BTreeEditor editor, NodeType type, String name) {
		properties = new NodeProperties();
		this.nodename = name;
		this.editor = editor;
		this.type = type;
		setSize(NWIDTH, NHEIGHT);
		children = new Array<BehaviorNode>();

		listener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor a = event.getListenerActor();

				if (a == add) {
					editor.createNewNode(BehaviorNode.this);
				}

				if (a == remove) {
					if (parent != null) {
						editor.addAndExecuteCommand(new RemoveNodeCommand(editor, BehaviorNode.this, parent));
					}
				}

				if (a == left) {
					editor.addAndExecuteCommand(new MoveNodeCommand(editor, BehaviorNode.this, parent, true));
				}

				if (a == right) {
					editor.addAndExecuteCommand(new MoveNodeCommand(editor, BehaviorNode.this, parent, false));
				}

			}
		};

		

		createNodeTable();
		addActor(node_table);

	}
	
	protected void setNodeParent(BehaviorNode parent) {
		this.parent = parent;
	}
	
	private void createNodeTable() {
		node_table = new Table();
		node_table.setSize(WIDTH, HEIGHT);
		node_table.align(Align.topLeft);

		NinePatch bg_patch = new NinePatch(getTexture(), 14, 14, 28, 14);
		NinePatchDrawable bg = new NinePatchDrawable(bg_patch);

		node_table.setBackground(bg);
		node_table.padTop(0);

		header = new Table();

		node_table.add(header).height(28).pad(0).align(Align.topLeft).growX().row();

		name_label = new VisLabel(nodename);
		header.add(name_label);

		button_table = new Table();
		button_table.align(Align.left);

		node_table.add(button_table).height(28).growX().row();

		add = new ImageButton(Assets.Styles.addButton);
		add.addListener(listener);
		remove = new ImageButton(Assets.Styles.removeButton);
		remove.addListener(listener);
		left = new ImageButton(Assets.Styles.leftButton);
		left.addListener(listener);
		right = new ImageButton(Assets.Styles.rightButton);
		right.addListener(listener);

		button_table.add(left).padRight(5).size(28);
		button_table.add(right).size(28);
		button_table.add().growX();
		button_table.add(add).padRight(8).size(28);
		button_table.add(remove).size(28).row();

		node_table.add().height(28).row();

		property_table = new Table();

		node_table.add(property_table).grow();

		down = new ImageButton(Assets.Styles.downButton);
		down.addListener(listener);

		property_table.add(new VisLabel("Properties"));
		property_table.add().growX();
		property_table.add(down).height(28).row();

		// property_table.add();

		property_container = new Table();
		property_table.add(property_container).colspan(3).grow();
		property_container.setVisible(false);

	}
	
	public void setAnchorPos(float x,float y) {
		anchor_x = x;
		anchor_y = y;
	}
	
	public float getAnchorX() {
		return anchor_x;
	}
	
	public float getAnchorY() {
		return anchor_y;
	}
	
	public void setOriginalPos(float x,float y) {
		original_x = x;
		original_y = y;
	}

	public void createProperties() {
		Array<NodeProperty> nps = properties.getProperties();
		for (int i = 0; i < nps.size; i++) {
			Table p = nps.get(i).getPropertyTable(editor);
			property_container.add(p).growX().align(Align.center).row();
		}
	}

	public void showProperties() {
		property_container.add(property_table).align(Align.topLeft).grow();
	}

	public void hideProperties() {
		Cell<Table> cell = property_container.getCell(property_table);
		cell.pad(0);
		property_table.remove();
		property_container.layout();
	}

	public String getNodeName() {
		return nodename;
	}

	public int getChildrenCount() {
		return children.size;
	}

	public BehaviorNode getFirstChild() {
		return children.get(0);
	}

	public void addNode(BehaviorNode node) {
		node.parent = this;
		children.add(node);
		if (type == NodeType.SUPPLEMENT)
			add.setVisible(false);

		// As a node was added to this parent, update all children's arrows
		// to reflect new possible moves.
		updateArrows();
		updateArrowsOnChildren();

		editor.setDirty();
		

		layout();
	}

	public void addNode(BehaviorNode node, int index) {
		if (index != -1) {
			node.parent = this;
			children.insert(index, node);
			if (type == NodeType.SUPPLEMENT)
				add.setVisible(false);

			// As a node was added to this parent, update all children's arrows
			// to reflect new possible moves.
			updateArrows();
			updateArrowsOnChildren();

			editor.setDirty();
			

			layout();
		} else {
			addNode(node);
		}
	}

//	public void rebuildChildTable() {
//		child_table.reset();
//
//		for (int i = 0; i < children.size; i++) {
//			BehaviorNode n = children.get(i);
//			child_table.add(n).pad(10).align(Align.top);
//		}
//
//		child_table.invalidate();
//		reLayout();
//
//	}

	public void updateArrowsOnChildren() {
		for (int i = 0; i < children.size; i++) {
			if (children.get(i) instanceof BehaviorNode)
				children.get(i).updateArrows();
		}
	}

	public void updateArrows() {
		left.setVisible(false);
		right.setVisible(false);

		if (this.parent == null)
			return;
		int cnt = this.parent.children.size;
		if (cnt < 2)
			return;

		left.setVisible(true);
		right.setVisible(true);

		if (this == this.parent.children.get(0)) {
			left.setVisible(false);
		} else if (this == this.parent.children.get(cnt - 1)) {
			right.setVisible(false);
		}

	}

	public void removeNode(BehaviorNode node) {

		children.removeValue(node, true);
		node.remove();
		layout();
		if (type == NodeType.SUPPLEMENT)
			add.setVisible(true);
		editor.setDirty();

		// As a node was removed from this parent, update all children's arrows
		// to reflect new possible moves.
		updateArrowsOnChildren();

	}

	public void removeNodeLeaveChild(BehaviorNode node) {
		// children.
	}

	public int getIndex() {
		return parent != null ? parent.children.indexOf(this, true) : -1;
	}

	private int arrayNumber(BehaviorNode node) {
		for (int i = 0; i < children.size; i++) {
			if (node == children.get(i))
				return i;
		}

		return -1;
	}

	public void moveLeft(BehaviorNode node) {
		int pos = arrayNumber(node);
		if (pos < 0)
			return;

		int newPos = pos - 1;
		children.swap(newPos, pos);
		editor.setDirty();
		updateArrowsOnChildren();
	}

	public void moveRight(BehaviorNode node) {
		int pos = arrayNumber(node);
		if (pos < 0)
			return;

		int newPos = pos + 1;
		children.swap(newPos, pos);
		editor.setDirty();
		updateArrowsOnChildren();
	}

	public void layout() {
		float w = node_table.getWidth() + (ROOTPAD * 2);
		float h = node_table.getHeight() + (ROOTPAD * 2);
		
		if(children.size!=0)
			w = 0;
		
		//get max height and width
		float nodes_height = 0;
		for (int i = 0; i < children.size; i++) {
			BehaviorNode n = children.get(i);
			w+=n.getWidth();
			if(nodes_height < n.getHeight())
				nodes_height = n.getHeight();
			n.remove();
		}
		
		h+=nodes_height;
		

		setSize(w, h);
		
		centerNodeTable();
		
		//organize child nodes
		float last_x = 0;
		for (int i = 0; i < children.size; i++) {
			BehaviorNode n = children.get(i);
			n.setPosition(last_x, (node_table.getY()));
			n.setAnchorPos(n.getX(), n.getY());
			last_x+=n.getWidth();
			addActor(n);
			n.layout();
		}
		
		
		if(anchored) {
			setPosition(getX(),anchor_y-getHeight());
		}
	}
	
	private void centerNodeTable() {
		node_table.setPosition(getWidth() * .5f - (node_table.getWidth() * .5f),
				getHeight() - (ROOTPAD + node_table.getHeight()));
		setOriginalPos(getWidth() * .5f - (node_table.getWidth() * .5f), 
				getHeight() - (ROOTPAD + HEIGHT));
	}

	@Override
	public void act(float delta) {
		if (!p_check) {
			if (parent == null) {
				remove.setVisible(false);
			}
		}
		super.act(delta);
	}

	public String getJson(int indent) {
		String json = Utils.tab(indent) + "\"" + nodename + "\" :" + "{\n";
		json += properties.toJson(indent + 1) + ",\n";
		json += Utils.tab(indent + 1) + "\"children\" : {";
		for (int i = 0; i < children.size; i++) {

			json += "\n" + children.get(i).getJson(indent + 2);
			if (i + 1 < children.size)
				json += ",\n";
			else
				json += "\n";
		}
		json += children.size == 0 ? "}\n" : Utils.tab(indent + 1) + "}\n";
		json += Utils.tab(indent) + "}";
		return json;
	}

	public static BehaviorNode fromJson(BTreeEditor editor, JsonValue json) {
		BehaviorNode n = null;
		String name = json.name();
		if (name == null || name.isEmpty())
			json = json.get(0);
		name = json.name();
		NodeType type = null;
		if (NodeTemplate.templatesContainNodeName(editor.composite_nodes, name))
			type = NodeType.COMPOSITE;
		else if (NodeTemplate.templatesContainNodeName(editor.supplement_nodes, name))
			type = NodeType.SUPPLEMENT;
		else if (NodeTemplate.templatesContainNodeName(editor.leaf_nodes, name)) {
			type = NodeType.LEAF;
		} else {
			throw new IllegalArgumentException("Unable to create Node:[" + name + "] ");
		}

		n = new BehaviorNode(editor, type, name);
		JsonValue node_properties = json.get("properties");
		if (node_properties != null)
			n.properties.fromJson(node_properties);
		n.createProperties();
		n.showProperties();
		JsonValue children = json.get("children");

		for (int i = 0; i < children.size; i++) {
			JsonValue v = children.get(i);
			BehaviorNode child = BehaviorNode.fromJson(editor, v);
			n.addNode(child);

		}

		return n;
	}

	private Texture getTexture() {
		switch (type) {
		case COMPOSITE:
			return Assets.Textures.BLUE;
		case SUPPLEMENT:
			return Assets.Textures.YELLOW;
		case LEAF:
			return Assets.Textures.GREEN;
		default:
			return Assets.Textures.RED;
		}
	}

}
