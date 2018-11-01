package com.kyper.btedit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kyper.btedit.NodeProperties.NodeProperty;
import com.kyper.btedit.command.MoveNodeCommand;
import com.kyper.btedit.command.RemoveNodeCommand;

public class BehaviorNode extends VisWindow {

	public static int NWIDTH = 200;
	public static int NHEIGHT = 100;

	public static enum NodeType {
		SUPPLEMENT, COMPOSITE, LEAF
	}

	String nodename;
	public NodeType type;

	Table button_table;
	Table property_table,property_container;
	Table child_table;

	public NodeProperties properties;

	BTreeEditor editor;

	VisTextButton add, del, left, right;

	protected BehaviorNode parent;
	protected Array<BehaviorNode> children;

	boolean p_check = false;

	public BehaviorNode(final BTreeEditor editor, NodeType type, String name) {
		super(name);
		properties = new NodeProperties();
		this.nodename = name;
		this.editor = editor;
		this.type = type;
		setSize(NWIDTH, NHEIGHT);
		setResizable(false);
		setKeepWithinParent(false);
		setKeepWithinStage(false);
		// setMovable(false);
		children = new Array<BehaviorNode>();

		ClickListener listener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor a = event.getListenerActor();

				if (a == add) {
					editor.createNewNode(BehaviorNode.this);
				}

				if (a == del) {
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

		button_table = new Table();
		button_table.setHeight(20);
		add = new VisTextButton("add");
		add.addListener(listener);
		del = new VisTextButton("del");
		del.addListener(listener);
		left = new VisTextButton("<<");
		left.addListener(listener);
		right = new VisTextButton(">>");
		right.addListener(listener);
		button_table.add(left).padLeft(5).padRight(5);
		button_table.add(right);
		button_table.add(add).padLeft(5).padRight(5);
		button_table.add(del);
		button_table.add().growX();
		
		property_container = new Table();
		property_container.align(Align.topLeft);
		
		property_table = new Table();
		property_table.align(Align.topLeft);
		

		child_table = new Table();
		child_table.align(Align.top);

		add(button_table).growX().row();
		add(property_container).grow().row();
		add(child_table).grow();

		switch (type) {
		case COMPOSITE:
			setColor(Color.GREEN);
			break;
		case SUPPLEMENT:
			setColor(Color.YELLOW);
			break;
		case LEAF:
			setColor(Color.RED);
			add.setVisible(false);
			break;
		default:
			break;
		}

	}
	
	public void createProperties() {
		Array<NodeProperty> nps = properties.getProperties();
		for (int i = 0; i < nps.size; i++) {
			Table p = nps.get(i).getPropertyTable(editor);
			property_table.add(p).growX().align(Align.left).padTop(5).row();
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

	public int getChildrenCount()
	{
		return children.size;
	}

	public BehaviorNode getFirstChild()
	{
		return children.get(0);
	}

	public void addNode(BehaviorNode node) {
		node.parent = this;
		children.add(node);
		node.setMovable(false);
		child_table.add(node).pad(10).align(Align.top);
		child_table.invalidate();
		reLayout();
		if (type == NodeType.SUPPLEMENT)
			add.setVisible(false);

		// As a node was added to this parent, update all children's arrows
		// to reflect new possible moves.
		updateArrows();
		updateArrowsOnChildren();

		editor.setDirty();
	}

	public void addNode(BehaviorNode node, int index) {
		if (index != -1) {
			node.parent = this;
			children.insert(index, node);
			node.setMovable(false);
			child_table.add(node).pad(10).align(Align.top);
			child_table.getCells().swap(index, child_table.getCells().size - 1);
			child_table.invalidate();
			reLayout();
			if (type == NodeType.SUPPLEMENT)
				add.setVisible(false);

			// As a node was added to this parent, update all children's arrows
			// to reflect new possible moves.
			updateArrows();
			updateArrowsOnChildren();

			editor.setDirty();
		} else {
			addNode(node);
		}
	}

	public void rebuildChildTable() {
		child_table.reset();

		for (int i = 0; i < children.size; i++) {
			BehaviorNode n = children.get(i);
			child_table.add(n).pad(10).align(Align.top);
		}

		child_table.invalidate();
		reLayout();

	}

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
		Cell<BehaviorNode> cell = child_table.getCell(node);
		cell.pad(0);
		node.remove();
		reLayout();
		if (type == NodeType.SUPPLEMENT)
			add.setVisible(true);
		editor.setDirty();

		// As a node was removed from this parent, update all children's arrows
		// to reflect new possible moves.
		updateArrowsOnChildren();

	}

	public void removeNodeLeaveChild(BehaviorNode node)
	{
		//children.
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
		rebuildChildTable();
		editor.setDirty();
		updateArrowsOnChildren();
	}

	public void moveRight(BehaviorNode node) {
		int pos = arrayNumber(node);
		if (pos < 0)
			return;

		int newPos = pos + 1;
		children.swap(newPos, pos);
		rebuildChildTable();
		editor.setDirty();
		updateArrowsOnChildren();
	}

	protected void reLayout() {
		this.pack();
		if (parent != null)
			parent.reLayout();
	}

	@Override
	public void act(float delta) {
		if (!p_check) {
			if (parent == null) {
				del.setVisible(false);
			}
		}
		super.act(delta);
	}

	public String getJson(int indent) {
		String json = Utils.tab(indent) + "\"" + nodename + "\" :" + "{\n";
		json += properties.toJson(indent + 1) + ",\n";
		json += Utils.tab(indent + 1) + "\"children\" : [";
		for (int i = 0; i < children.size; i++) {

			json += "\n{" + children.get(i).getJson(indent + 2);
			if (i + 1 < children.size)
				json += "},\n";
			else
				json += "}\n";
		}
		json += children.size == 0 ? "]\n" : Utils.tab(indent + 1) + "]\n";
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

}
