package com.kyper.btedit.data.properties;

/**
 * these are the types of properties we can assign to nodes. They are enums with
 * default string values that will be used when no property is set.
 * 
 * @author john
 *
 */
public enum PropertyType {
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
