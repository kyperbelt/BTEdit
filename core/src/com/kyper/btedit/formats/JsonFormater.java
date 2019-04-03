package com.kyper.btedit.formats;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import com.kyper.btedit.Utils;
import com.kyper.btedit.data.Node;
import com.kyper.btedit.data.NodeBank;
import com.kyper.btedit.data.NodeTemplate;
import com.kyper.btedit.data.NodeTree;
import com.kyper.btedit.data.NodeType;
import com.kyper.btedit.data.properties.NodeProperties;
import com.kyper.btedit.data.properties.NodeProperty;
import com.kyper.btedit.data.properties.PropertyType;

public class JsonFormater implements FileFormater{

	JsonReader reader;
	private NodeBank bank;//bank of node templates
	
	
	public JsonFormater(NodeBank bank) {
		reader = new JsonReader();
		this.bank = bank;
	}
	
	@Override
	public FileFormat getFormat() {
		return FileFormat.Json;
	}

	@Override
	public boolean isFormat(String data) {
		try {
			reader.parse(data);
		}catch(SerializationException e) {
			System.err.println(e);
			return false;
		}
		return true;
	}

	@Override
	public String toFormat(NodeTree tree) {
		return nodeToJson(tree.getRoot(), 0);
	}

	@Override
	public NodeTree toTree(String data) {
		JsonValue json = reader.parse(data);
		NodeTree tree = new NodeTree();
		tree.setRoot(getNode(json));
		return tree;
	}
	
	private Node getNode(JsonValue json) {
		Node n = null;
		
		String name = json.name();
		if(name == null || name.isEmpty())
			json = json.get(0);
		name = json.name();
		NodeType type = null;
		
		if (NodeTemplate.templatesContainNodeName(bank.getComposite(), name))
			type = NodeType.COMPOSITE;
		else if (NodeTemplate.templatesContainNodeName(bank.getSupplement(), name))
			type = NodeType.SUPPLEMENT;
		else if (NodeTemplate.templatesContainNodeName(bank.getLeaf(), name)) {
			type = NodeType.LEAF;
		} else {
			throw new IllegalArgumentException("Unable to create Node:[" + name + "] ");
		}
		
		n = new Node();
		n.setName(name);
		n.setNodeType(type);
		
		JsonValue nodeProperties = json.get("properties");
		if(nodeProperties != null)
			propertiesFromJson(n.getNodeProperties(), nodeProperties);
		
		JsonValue children = json.get("children");
		
		for (int i = 0; i < children.size; i++) {
			JsonValue v = children.get(i);
			Node child = getNode(v);
			n.addNode(child);
		}
		
		return n;
	}
	
	public static void propertiesFromJson(NodeProperties properties,JsonValue json) {
		for (int j = 0; j < json.size; j++) {
			JsonValue pv = json.get(j);
			NodeProperty property = new NodeProperty(pv.name,
					PropertyType.getTypeByName(pv.get("type") != null ? pv.getString("type") : ""),
					pv.get("value") != null ? pv.get("value").asString() : null);
			properties.addPropety(property);
		}
	}
	
	
	/**
	 * get the node in string json format 
	 * 
	 * @param node
	 * @param level - indent level
	 * @return
	 */
	private String nodeToJson(Node node,int level) {
		String json = Utils.tab(level) +"\""+node.getName()+ "\" :" + "{\n";
		json += propertiesToJson(node.getNodeProperties(),level + 1) + ",\n";
		json += Utils.tab(level + 1) + "\"children\" : [";
		for (int i = 0; i < node.getChildren().size; i++) {

			json += "\n{" + nodeToJson(node.getChildren().get(i),level + 2);
			if (i + 1 < node.getChildren().size)
				json += "},\n";
			else
				json += "}\n";
		}
		json += node.getChildren().size == 0 ? "]\n" : Utils.tab(level + 1) + "]\n";
		json += Utils.tab(level) + "}";
		return json;
	}
	
	/**
	 * get the properties in json format
	 * 
	 * @param properties
	 * @param level - indent level
	 * @return
	 */
	public static String propertiesToJson(NodeProperties properties,int level) {
		String propertiesJson = Utils.tab(level) + "\"properties\"" + " : {" + (properties.getProperties().size == 0 ? "" : "\n");
		for (int i = 0; i < properties.getProperties().size; i++) {
			NodeProperty p = properties.getProperties().get(i);
			propertiesJson += p.getJson(level + 1) + (i == properties.getProperties().size - 1 ? "" : ",") + "\n";
		}

		propertiesJson += (properties.getProperties().size == 0 ? "" : Utils.tab(level)) + "}";

		return propertiesJson;
	}

}
