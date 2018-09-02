package com.kyper.btedit;

import com.badlogic.gdx.utils.ObjectMap;

public class NodeProperties {
	
	public static enum PropertyType{
		Int,
		Float,
		Bool,
		String
	}
	
	public static class NodeProperty{
		public String name;
		public PropertyType type;
		
		public NodeProperty(String name,PropertyType type){
			this.name = name;
			this.type = type;
		}
		
		public String getJson() {
			return name;
		}
	}

}
