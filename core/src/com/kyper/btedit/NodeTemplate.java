package com.kyper.btedit;

import com.badlogic.gdx.utils.Array;
import com.kyper.btedit.data.Node;
import com.kyper.btedit.data.NodeType;
import com.kyper.btedit.properties.NodeProperties;
import com.kyper.btedit.properties.NodeProperty;

public class NodeTemplate {
	
	private String name;
	private NodeType type;
	private NodeProperties properties;
	private boolean isDefault = false;

	public NodeTemplate(String name,NodeType type, boolean isDefault) {
		this(name, type);
		this.isDefault= isDefault;
	}
	
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

	public void setAsRoot()
	{
		isDefault = true;
	}

	public boolean isRoot()
	{
		return isDefault;
	}
	
	public void properitize(Node node) {
		Array<NodeProperty> ps = this.properties.getProperties();
		for (int i = 0; i < ps.size; i++) {
			NodeProperty p = ps.get(i);
			NodeProperty np = new NodeProperty(p.name, p.type,p.value);
			node.getNodeProperties().addPropety(np);
			System.out.println(String.format("added property [%s] to Node->%s", np.name,node.getName()));
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
			if (template.isRoot() == false)
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
