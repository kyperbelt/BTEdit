package com.kyper.btedit.properties;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kyper.btedit.Assets;
import com.kyper.btedit.BTreeEditor;
import com.kyper.btedit.BehaviorNode;
import com.kyper.btedit.Utils;

public class NodeProperty {
	public String name;
	public PropertyType type;
	public String value;
	
	private VisLabel value_label;
	
	private PropertyEditTable edit_table;

	public NodeProperty(String name, PropertyType type) {
		this(name, type, type.getPropertyValue());
	}

	public NodeProperty(String name, PropertyType type, String value) {
		this.name = name;
		this.type = type;
		if (value == null)
			this.value = type.getPropertyValue();
		else
			this.value = value;
		edit_table = new PropertyEditTable(this);
	}
	
	public void updateValueLabel() {
		value_label.setText(value);
	}
	

	/**
	 * gets the json for this property in the format \{ "name" : "value" \}
	 * 
	 * @return
	 */
	public String getJson(int indent) {
		return Utils.tab(indent) + "\"" + name + "\":{\n" + Utils.tab(indent + 1) + "\"value\":\"" + value + "\",\n"
				+ Utils.tab(indent + 1) + "\"type\":\"" + type.name().toLowerCase() + "\"\n" + Utils.tab(indent)
				+ "}";
	}
	
	public PropertyEditTable getEditTale() {
		return edit_table;
		
	}
	
	public NodeProperty getCopy() {
		NodeProperty copy = new NodeProperty(name, type,value);
		return copy;
	}
	
	public Table getPropertyTable(final BTreeEditor editor) {
		

		Table pt = new Table();
		pt.setHeight(24);

		VisLabel namel = new VisLabel(name);
		namel.setColor(BehaviorNode.H_COLOR);
		namel.setFontScale(.9f);
		namel.setWidth(70);
		namel.setAlignment(Align.left);
		namel.setEllipsis(true);
		namel.setEllipsis("...");
		
		value_label = new VisLabel(value);
		value_label.setColor(BehaviorNode.H_COLOR);
		value_label.setFontScale(.9f);
		value_label.setAlignment(Align.right);
		value_label.setEllipsis(true);

		pt.add(namel).width(70).align(Align.left).padRight(3);
		pt.add(value_label).width(90).growX().align(Align.bottomRight);
		ImageButton gear = new ImageButton(Assets.Styles.editButton);
		gear.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				editor.busy = true;
				editor.property_display.showPropertyDisplay(NodeProperty.this);
			}	
		});
		pt.add(gear).size(16);

		return pt;
		
//		if (property_table!=null)
//			return property_table;
//		
//		property_table = new Table();
//		property_table.align(Align.topLeft);
//		
//		boolean is_boolean = false;
//		
//		VisLabel namelabel = new VisLabel(name+" : ");
//		
//		final VisTextField value_field = getValueField();
//		
//		final VisCheckBox checkbox = new VisCheckBox("");
//		if(value_field == null) {
//			checkbox.setChecked(Boolean.parseBoolean(value));
//			
//			checkbox.addListener(new ChangeListener() {
//				@Override
//				public void changed(ChangeEvent event, Actor actor) {
//					String new_value = Boolean.toString(checkbox.isChecked());
//					editor.addAndExecuteCommand(new ChangePropertyCommand(editor, NodeProperty.this, value, new_value,checkbox));
//				}
//			});
//			is_boolean = true;
//		}else {
//			
//			value_field.addListener(new ChangeListener() {
//				@Override
//				public void changed(ChangeEvent event, Actor actor) {
//					String new_value = value_field.getText();
//					editor.addAndExecuteCommand(new ChangePropertyCommand(editor, NodeProperty.this, value, new_value,value_field));
//				}
//			});
//		}
//		
//		property_table.add(namelabel);
//
//		if(!is_boolean) {
//			property_table.add(value_field);
//		}else {
//			property_table.add(checkbox);
//		}
//		
//		return property_table;
	}

}
