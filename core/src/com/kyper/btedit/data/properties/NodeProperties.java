package com.kyper.btedit.data.properties;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.kyper.btedit.Utils;

public class NodeProperties {

	private Array<NodeProperty> properties;

	public NodeProperties() {
		properties = new Array<NodeProperty>();
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
	
	public void makeCopyOf(NodeProperties properties) {
		getProperties().clear();
		for (int i = 0; i < properties.getProperties().size; i++) {
			addPropety(properties.getProperties().get(i).getCopy());
		}
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


}
