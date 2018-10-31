package com.kyper.btedit.command;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kyper.btedit.BTreeEditor;
import com.kyper.btedit.properties.NodeProperty;
import com.kyper.btedit.properties.PropertyType;

public class ChangePropertyCommand implements ICommand{

	BTreeEditor editor;
	NodeProperty property;
	String old_value;
	String new_value;
	
	public ChangePropertyCommand(BTreeEditor editor,NodeProperty property,String old_value,String new_value) {
		this.editor = editor;
		this.property = property;
		this.old_value = old_value;
		this.new_value = new_value;
	}
	
	@Override
	public void execute() {
		property.value = new_value;
		property.updateValueLabel();
		editor.setDirty();
	}

	@Override
	public void undo() {
		property.value = old_value;
		property.updateValueLabel();
		editor.setDirty();
	}

	@Override
	public String desc() {
		return String.format("Changed Property [%s] : old_value=%s | new_value=%s", property.name,old_value,new_value);
	}

}
