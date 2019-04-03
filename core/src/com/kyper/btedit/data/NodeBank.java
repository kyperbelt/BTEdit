package com.kyper.btedit.data;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.kyper.btedit.BTConfig;
import com.kyper.btedit.Utils;
import com.kyper.btedit.formats.JsonFormater;

/**
 * a bank of stored node types for use in behavior trees
 * 
 * @author john
 *
 */
public class NodeBank {

	private Array<NodeTemplate> compositeNodes;
	private Array<NodeTemplate> supplementNodes; // DECORATORSs
	private Array<NodeTemplate> leafNodes;

	public NodeBank() {
		compositeNodes = new Array<NodeTemplate>();
		supplementNodes = new Array<NodeTemplate>();
		leafNodes = new Array<NodeTemplate>();
	}

	public Array<NodeTemplate> getComposite() {
		return compositeNodes;
	}

	public Array<NodeTemplate> getSupplement() {
		return supplementNodes;
	}

	public Array<NodeTemplate> getLeaf() {
		return leafNodes;
	}

	public String templatesToJson(NodeType type, int indent, Array<NodeTemplate> templates) {
		String json = "\n" + Utils.tab(indent) + "\"" + type.name().toLowerCase() + "\" : {"
				+ (templates.size == 0 ? "" : "\n");
		for (int i = 0; i < templates.size; i++) {
			NodeTemplate t = templates.get(i);
			json += t.getJson(indent + 1) + (i == templates.size - 1 ? "" : ",") + "\n";
		}
		json += Utils.tab(indent) + "}";
		return json;
	}

	/**
	 * save the node templates to the file
	 *
	 * @param nodes
	 */
	public void saveNodeTemplates(FileHandle nodes) {
		String json = "{ \n";
		json += templatesToJson(NodeType.COMPOSITE, 1, compositeNodes) + ",";
		json += templatesToJson(NodeType.SUPPLEMENT, 1, supplementNodes) + ",";
		json += templatesToJson(NodeType.LEAF, 1, leafNodes);
		json += "\n}";
	}

	public void templateFromJson(Array<NodeTemplate> templates, JsonValue json, NodeType type) {

		for (int i = 0; i < json.size; i++) {
			JsonValue node = json.get(i);
			System.out.println("templated added to " + type.name() + " :" + node.name);
			NodeTemplate template = new NodeTemplate(node.name, type);
			JsonValue properties = node.get("properties");
			if (properties != null) {
				JsonFormater.propertiesFromJson(template.getProperties(), properties);
			}

			templates.add(template);

			// JsonValue isDefault = node.get("root");
			// if (isDefault != null) {
			// // right now if there always assumes true!!!
			// m_defaultRootTemplate = template;
			// m_defaultRootTemplate.setAsRoot();
			// templates.add(template);
			// } else {
			// // only add to chooser if it's not default
			// // this is useful for defining a root node always
			// // just set node type to "root"
			// templates.add(template);
			// }

		}
	}

	public NodeTemplate getTemplate(String name, NodeType type) {
		switch (type) {
		case COMPOSITE:
			return NodeTemplate.getTemplateByName(compositeNodes, name);
		case SUPPLEMENT:
			return NodeTemplate.getTemplateByName(supplementNodes, name);
		case LEAF:
			return NodeTemplate.getTemplateByName(leafNodes, name);
		}
		return null;
	}

	public void loadNodeTemplates(FileHandle nodes) {
		if(!nodes.file().exists()) {
			nodes.writeString(Utils.DEFAULT_NODES, false);
		}
//		m_defaultRootTemplate = null;

		JsonValue root = new JsonReader().parse(nodes);

		JsonValue composite = root.get("composite");
		System.out.println("json.size : " + composite.size);
		templateFromJson(compositeNodes, composite, NodeType.COMPOSITE);

		JsonValue supplement = root.get("supplement");
		templateFromJson(supplementNodes, supplement, NodeType.SUPPLEMENT);

		JsonValue leaf = root.get("leaf");
		templateFromJson(leafNodes, leaf, NodeType.LEAF);
	}
}
