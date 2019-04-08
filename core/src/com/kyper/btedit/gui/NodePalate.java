package com.kyper.btedit.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kyper.btedit.Assets;
import com.kyper.btedit.data.NodeBank;
import com.kyper.btedit.data.NodeTemplate;

/**
 * in editor view of all available nodes allowing for easy drag and drop of nodes into trees
 * @author john
 *
 */
public class NodePalate extends VisWindow{
	
	private static final int WIDTH = 300;
	private static final int HEIGHT = 500;
	
	private static final String LESS = "-";
	private static final String MORE = "+";
	
	private VisTextButton minMaxButton;
	
	private Table nodesTable;
	
	private NodesList composite;
	private NodesList supplement;
	private NodesList leaf;
	
	private VisScrollPane scroll;

	public NodePalate() {
		super("Nodes");
		// TODO Auto-generated constructor stub
		minMaxButton = new VisTextButton(LESS);
		minMaxButton.setWidth(15);
		getTitleTable().add(minMaxButton);
		setWidth(WIDTH);
		setResizable(false);
		nodesTable = new Table();
		//nodesTable.setWidth(WIDTH);
		nodesTable.align(Align.topLeft);
		
		composite = new NodesList("Composite");
		composite.setBG(Assets.Textures.BLUE);
		supplement = new NodesList("Supplement");
		supplement.setBG(Assets.Textures.YELLOW);
		leaf = new NodesList("Leaf");
		leaf.setBG(Assets.Textures.GREEN);
		
		nodesTable.add(composite).growX().row();
		nodesTable.add(supplement).growX().row();
		nodesTable.add(leaf).growX().row();
		
		scroll = new VisScrollPane(nodesTable);
		scroll.setScrollingDisabled(true, false);
		scroll.setSize(WIDTH, HEIGHT);
		
		add(scroll).grow();
		
		setMinimized(false);
	}
	
	public void refreshPalate(NodeBank bank) {
		composite.refreshList(bank.getComposite());
		supplement.refreshList(bank.getSupplement());
		leaf.refreshList(bank.getLeaf());
		pack();
	}
	
	@Override
	public float getPrefWidth() {
		return WIDTH;
	}
	
	@Override
	public float getMaxHeight() {
		return Gdx.graphics.getWidth() * .8f;
	}
	
	public void setMinimized(boolean minimized) {
		if(minimized) {
			minMaxButton.setText(MORE);
		}else {
			minMaxButton.setText(LESS);
		}
		
		pack();
	}
	
	
	static class NodesList extends Table{
		
		private Table header;
		private VisLabel listName;
		private VisTextButton minMaxButton;
		private VisTextButton addButton;
		private Table nodeItems;
		
		public NodesList(String name) {
			//setWidth(WIDTH);
			
			header = new Table();
			header.align(Align.left);
			
			listName = new VisLabel(name);
			addButton = new VisTextButton("new");
			minMaxButton = new VisTextButton(LESS);
			

			header.add(minMaxButton).width(15);
			header.add(listName).padLeft(15);
			header.add().growX();
			header.add(addButton).padRight(10);
			
			add(header).growX().padBottom(10).padTop(10).row();
			
			nodeItems = new Table();
			
			add(nodeItems).grow();
			
		}
		
		public void refreshList(Array<NodeTemplate> nodes) {
			nodeItems.clear();
			for (int i = 0; i < nodes.size; i++) {
				NodeTemplate nt = nodes.get(i);
				nodeItems.add(new NodeItem(nt.getNodeName())).align(Align.left).padBottom(5).growX().row();
				
			}
		}
		
		public void setMinimized(boolean minimized) {
			if(minimized) {
				minMaxButton.setText(MORE);
				nodeItems.clear();
				nodeItems.pack();
			}else {
				minMaxButton.setText(LESS);
			}
		}
		
		public void setBG(Texture texture) {
			NinePatch bg_patch = new NinePatch(texture, 14, 14, 18, 14);
			NinePatchDrawable bg = new NinePatchDrawable(bg_patch);
			bg.setTopHeight(80);

			setBackground(bg);
			padTop(0);
		}
		
		
	}
	
	static class NodeItem extends Table{
		
		VisLabel nameLabel;
		VisTextButton edit;
		
		public NodeItem(String name) {
			
			align(Align.left);
			
			nameLabel = new VisLabel(name);
			edit = new VisTextButton("edit");
			
			add(nameLabel);
			add().growX();
			add(edit);
		}
		
	}
}
