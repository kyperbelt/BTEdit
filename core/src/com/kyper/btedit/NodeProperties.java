package com.kyper.btedit;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.kyper.btedit.NodeProperties.NodeProperty;
import com.kyper.btedit.NodeProperties.PropertyType;

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

		json += Utils.tab(indent) + "}";

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
	}

}
