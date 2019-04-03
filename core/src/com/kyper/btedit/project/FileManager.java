package com.kyper.btedit.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kyper.btedit.data.NodeBank;
import com.kyper.btedit.formats.FileFormat;
import com.kyper.btedit.formats.FileFormater;
import com.kyper.btedit.formats.JsonFormater;

/**
 * class that will be used to save and load files from the system
 * 
 * @author john
 *
 */
public class FileManager {

	FileChooser chooser;
	FileChooser saver;

	FileFormater formatter;

	NodeBank bank;

	public FileManager(NodeBank bank, FileFormat format) {
		this.bank = bank;
		setFileFormat(format);
	}

	public void setFileFormat(FileFormat format) {
		switch (format) {
		case Json:
			formatter = new JsonFormater(bank);
			break;
		default:
			throw new UnsupportedOperationException("Invalid FileFormat :" + format.name());
		}
	}

	public Project open(String path, String filename) {

		FileHandle file = Gdx.files.absolute(path + "\\" + filename);
		String data = file.readString();

		if (!formatter.isFormat(data)) {
			// TODO: handle unexpected/wrong format
			System.out.println(String.format("file %s is not the correct format!", filename));
			return null;
		}

		return new Project(formatter.toTree(data), filename, path);
	}

	public boolean save(String path, Project project) {

		return false;
	}

}
