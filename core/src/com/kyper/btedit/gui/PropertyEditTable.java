package com.kyper.btedit.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter;
import com.kotcrab.vis.ui.util.IntDigitsOnlyFilter;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kyper.btedit.data.properties.NodeProperty;

public class PropertyEditTable extends Table{
	
	private NodeProperty property;
	private VisTextField input_field;
	private VisCheckBox checkbox;
	private VisLabel name_label;
	private boolean is_bool = false;
	
	public PropertyEditTable(NodeProperty property) {
		this.property = property;
		align(Align.topLeft);

		name_label = new VisLabel(property.name + ":");
		name_label.setAlignment(Align.left);

		input_field = getValueField();

		add(name_label).width(100);

		if (!isBoolean()) {
			add(input_field).growX();
		} else {
			add(checkbox).align(Align.left);
		}

	}
	
	/**
	 * update all the values in the edit table to correspond with that of its property
	 */
	public void update() {
		name_label.setText(property.name+":");
		if(isBoolean())
			checkbox.setChecked(Boolean.parseBoolean(property.value));
		else
			input_field.setText(property.value);
	}
	
	/**
	 * check if this uses a boolean(checkbox) or input field value
	 * @return
	 */
	public boolean isBoolean() {
		return is_bool;
	}
	
	public NodeProperty getProperty() {
		return this.property;
	}
	
	public VisTextField getInputField() {
		return input_field;
	}
	
	public VisCheckBox getCheckbox() {
		return checkbox;
	}
	
	public VisLabel getNameLabel() {
		return name_label;
	}
	
	
	private VisTextField getValueField() {
		
		VisTextField text_field = new VisTextField();
		text_field.setWidth(280);
		is_bool = false;
		switch(property.type) {
		case Int:
			text_field.setTextFieldFilter(new IntDigitsOnlyFilter(true));
			break;
		case Float:
			text_field.setTextFieldFilter(new FloatDigitsOnlyFilter(true));
			break;
		case Bool:
			is_bool = true;
			checkbox = new VisCheckBox("");
			checkbox.setChecked(Boolean.parseBoolean(property.value));
			return null;
		case String:
			break;
		}
		text_field.setText(property.value);
		return text_field;
	}

}
