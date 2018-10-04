package com.kyper.btedit;

import com.badlogic.gdx.utils.Array;
import com.kyper.btedit.BehaviorNode.NodeType;
import com.kyper.btedit.NodeProperties.NodeProperty;

public class NodeTemplate {
	
	private String name;
	private NodeType type;
	private NodeProperties properties;
	
	public NodeTemplate(String name,NodeType type) {
		this.name = name;
		this.type = type;
		properties = new NodeProperties();
	}
	
	public NodeProperties getProperties() {
		return properties;
	}
	
	public NodeType getNodeType() {
		return type;
	}
	
	public String getNodeName() {
		return name;
	}
	
	public void properitize(BehaviorNode node) {
		Array<NodeProperty> ps = this.properties.getProperties();
		for (int i = 0; i < ps.size; i++) {
			NodeProperty p = ps.get(i);
			NodeProperty np = new NodeProperty(p.name, p.type,p.value);
			node.properties.addPropety(np);
			System.out.println("added property:"+np.name);
		}
	}
	
	public String getJson(int indent) {
		String json = String.format(Utils.tab(indent)+"\"%s\":{\n",name);
		json+=properties.toJson(indent+1);
		json+=Utils.tab(indent)+"}\n";
		return json;
	}
	
	public static NodeTemplate getTemplateByName(Array<NodeTemplate> templates,String name) {
		for (int i = 0; i < templates.size; i++) {
			NodeTemplate template = templates.get(i);
			if(template.name.equals(name))
				return template;
		}
		return null;
	}
	
	public static void templatesToStringArray(Array<NodeTemplate> templates,Array<String> out){
		out.clear();
		for (int i = 0; i < templates.size; i++) {
			NodeTemplate template = templates.get(i);
			out.add(template.name);
		}
	}
	
	public static boolean templatesContainNodeName(Array<NodeTemplate> templates,String name) {
		for (int i = 0; i < templates.size; i++) {
			NodeTemplate t = templates.get(i);
			if(t.name.equals(name))
				return true;
		}
		return false;
	}

}
