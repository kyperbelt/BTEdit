package com.kyper.btedit;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter;
import com.kotcrab.vis.ui.util.IntDigitsOnlyFilter;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextField;

public class NodeProperties {

	private Array<NodeProperty> properties;

	public NodeProperties() {
		properties = new Array<NodeProperties.NodeProperty>();
	}

	public Array<NodeProperty> getProperties() {
		return properties;
	}

	/**
	 * set the node property value of the named node
	 * 
	 * @param name
	 * @param value
	 */
	public void setPropertyValue(String name, String value) {
		NodeProperty property = getProperty(name);
		if (property != null) {
			property.value = value;
		}
	}

	public void addPropety(NodeProperty property) {

		NodeProperty p = getProperty(property.name);
		if (p != null) {
			System.out.println(String.format("failed to add [%s] to propetties because similar named already exists.",
					property.name));
			return;
		}

		properties.add(property);
	}

	/**
	 * search for a Node property by name
	 * 
	 * @param name
	 * @return
	 */
	public NodeProperty getProperty(String name) {
		for (int i = 0; i < properties.size; i++) {
			if (properties.get(i).name.equals(name))
				return properties.get(i);
		}
		return null;
	}

	/**
	 * get the properties in json format proper
	 * 
	 * @param indent
	 * @return
	 */
	public String toJson(int indent) {
		String json = Utils.tab(indent) + "\"properties\"" + " : {" + (properties.size == 0 ? "" : "\n");
		for (int i = 0; i < properties.size; i++) {
			NodeProperty p = properties.get(i);
			json += p.getJson(indent + 1) + (i == properties.size - 1 ? "" : ",") + "\n";
		}

		json += (properties.size == 0 ? "" : Utils.tab(indent)) + "}";

		return json;
	}

	public NodeProperties fromJson(JsonValue json) {
		for (int j = 0; j < json.size; j++) {
			JsonValue pv = json.get(j);
			NodeProperty property = new NodeProperty(pv.name,
					PropertyType.getTypeByName(pv.get("type") != null ? pv.getString("type") : ""),
					pv.get("value") != null ? pv.get("value").asString() : null);
			addPropety(property);
		}
		return this;
	}

	/**
	 * these are the types of properties we can assign to nodes. They are enums with
	 * default string values that will be used when no property is set.
	 * 
	 * @author john
	 *
	 */
	public static enum PropertyType {
		Int("0"), Float("0.0"), Bool("false"), String("null");

		final String value;

		PropertyType(String value) {
			this.value = value;
		}

		public String getPropertyValue() {
			return value;
		}

		public static PropertyType getTypeByName(String name) {
			if (name.toLowerCase().equals("int"))
				return Int;
			if (name.toLowerCase().equals("float"))
				return Float;
			if (name.toLowerCase().equals("bool"))
				return Bool;
			return String;
		}
	}

	public static class NodeProperty {
		public String name;
		public PropertyType type;
		public String value;
		
		private Table property_table;

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
		
		public Table getPropertyTable(final BTreeEditor editor) {
			

			Table pt = new Table();
			pt.setHeight(24);

			VisLabel namel = new VisLabel(name);
			namel.setFontScale(.9f);
			namel.setWidth(70);
			namel.setAlignment(Align.left);
			int char_lim = (int) (70 / namel.getStyle().font.getSpaceWidth());
			System.out.println(char_lim);
			if( char_lim*.8f < name.length()) {
				namel.setText(name.substring(0, 8)+"...");
			}
			VisLabel valuel = new VisLabel(value);
			valuel.setFontScale(.9f);
			valuel.setAlignment(Align.right);

			pt.add(namel).width(70).align(Align.left).padRight(3);
			pt.add(valuel).growX().align(Align.bottomRight);
			ImageButton gear = new ImageButton(Assets.Styles.editButton);
			pt.add(gear).size(16);

			return pt;
			
//			if (property_table!=null)
//				return property_table;
//			
//			property_table = new Table();
//			property_table.align(Align.topLeft);
//			
//			boolean is_boolean = false;
//			
//			VisLabel namelabel = new VisLabel(name+" : ");
//			
//			final VisTextField value_field = getValueField();
//			
//			final VisCheckBox checkbox = new VisCheckBox("");
//			if(value_field == null) {
//				checkbox.setChecked(Boolean.parseBoolean(value));
//				
//				checkbox.addListener(new ChangeListener() {
//					@Override
//					public void changed(ChangeEvent event, Actor actor) {
//						String new_value = Boolean.toString(checkbox.isChecked());
//						editor.addAndExecuteCommand(new ChangePropertyCommand(editor, NodeProperty.this, value, new_value,checkbox));
//					}
//				});
//				is_boolean = true;
//			}else {
//				
//				value_field.addListener(new ChangeListener() {
//					@Override
//					public void changed(ChangeEvent event, Actor actor) {
//						String new_value = value_field.getText();
//						editor.addAndExecuteCommand(new ChangePropertyCommand(editor, NodeProperty.this, value, new_value,value_field));
//					}
//				});
//			}
//			
//			property_table.add(namelabel);
//
//			if(!is_boolean) {
//				property_table.add(value_field);
//			}else {
//				property_table.add(checkbox);
//			}
//			
//			return property_table;
		}
		
		private VisTextField getValueField() {
			
			VisTextField text_field = new VisTextField();
			text_field.setWidth(280);
			
			switch(type) {
			case Int:
				text_field.setTextFieldFilter(new IntDigitsOnlyFilter(true));
				
				break;
			case Float:
				text_field.setTextFieldFilter(new FloatDigitsOnlyFilter(true));
				break;
			case Bool:
				return null;
			case String:
				break;
			}
			text_field.setText(value);
			return text_field;
		}
	}

}
