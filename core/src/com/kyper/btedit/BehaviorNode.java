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
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class BehaviorNode extends VisWindow {

	public static int NWIDTH = 200;
	public static int NHEIGHT = 100;

	public static enum NodeType {
		SUPPLEMENT, COMPOSITE, LEAF
	}

	String nodename;
	NodeType type;

	Table button_table;
	Table child_table;
	
	NodeProperties properties;

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
		//setMovable(false);
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
						parent.removeNode(BehaviorNode.this);
					}
				}

				if (a == left) {

				}

				if (a == right) {

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

		child_table = new Table();
		child_table.align(Align.top);

		add(button_table).growX().row();
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

	public String getNodeName() {
		return nodename;
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
		editor.setDirty();
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
		json+=Utils.tab(indent+1)+"\"children\" : {";
		for (int i = 0; i < children.size; i++) {
			
			json += "\n" + children.get(i).getJson(indent + 2);
			if (i + 1 < children.size)
				json += ",\n";
			else 
				json+="\n";
		}
		json+=children.size == 0 ? "}\n":Utils.tab(indent+1)+"}\n";
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
		if (editor.Composites.contains(name, false))
			type = NodeType.COMPOSITE;
		else if (editor.Supplements.contains(name, false))
			type = NodeType.SUPPLEMENT;
		else if (editor.Leafs.contains(name, false)) {
			type = NodeType.LEAF;
		} else {
			throw new IllegalArgumentException("Unable to create Node:[" + name + "] ");
		}

		n = new BehaviorNode(editor, type, name);
		JsonValue children = json.get("children");
		
		for (int i = 0; i < children.size; i++) {
			JsonValue v = children.get(i);
			BehaviorNode child = BehaviorNode.fromJson(editor, v);
			n.addNode(child);

		}

		return n;
	}

}
