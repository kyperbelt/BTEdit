package com.kyper.btedit.displays;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kyper.btedit.BTreeEditor;
import com.kyper.btedit.command.ChangePropertyCommand;
import com.kyper.btedit.properties.NodeProperty;
import com.kyper.btedit.properties.PropertyType;
import com.kyper.btedit.properties.PropertyEditTable;

/**
 * this will be used to display the property name and an editable field.
 * 
 * @author jonathancamarena
 *
 */

public class PropertyDisplay extends VisWindow {

	private static final int WIDTH = 400;
	private static final int HEIGHT = 180;

	private BTreeEditor edit;

	private NodeProperty property;

	private Table property_table;
	private Table button_table;

	private VisTextButton save;

	private InputListener enter_listener = new InputListener() {
		public boolean keyDown(InputEvent event, int keycode) {
			
			if(keycode == Keys.ENTER) {
				saveProperty();
				return true;
			}
			return false;
		}; 
	};

	public PropertyDisplay(final BTreeEditor edit) {
		super("Edit Property");
		this.edit = edit;
		this.setSize(WIDTH, HEIGHT);
		this.setModal(true);
		this.addCloseButton();
		getTitleTable().getCells().get(1).getActor().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				edit.busy = false;
			}
		});

		property_table = new Table();
		property_table.align(Align.left);
		button_table = new Table();
		button_table.align(Align.right);

		this.add(property_table).growX().row();
		this.add(button_table).growX().padTop(5);

		save = new VisTextButton("save");
		save.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				saveProperty();
			}
		});
		button_table.add().growX();
		button_table.add(save);

	}

	public void saveProperty() {
		edit.busy = false;
		String new_value = property.value;
		PropertyEditTable pet = property.getEditTale();
		if (pet.isBoolean()) {
			new_value = Boolean.toString(pet.getCheckbox().isChecked());
		} else {
			new_value = property.getEditTale().getInputField().getText();
		}
		edit.addAndExecuteCommand(new ChangePropertyCommand(edit, property, property.value, new_value));
		
		if (!pet.isBoolean())
			property.getEditTale().getInputField().removeListener(enter_listener);

		close();
	}

	public void showPropertyDisplay(NodeProperty property) {
		this.property = property;
		property_table.reset();
		property.getEditTale().update();
		if (property.type != PropertyType.Bool)
		{
			property.getEditTale().getInputField().addListener(enter_listener);
		}

		property_table.add(property.getEditTale()).growX();
		edit.centerActor(this);
		edit.stage.addActor(this);

		if (property.type != PropertyType.Bool)
		{
			edit.stage.setKeyboardFocus(property.getEditTale().getInputField());
			property.getEditTale().getInputField().selectAll();
		}
	}

}
