package com.kyper.btedit.gui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * camera used to pan around a group
 * 
 * @author john
 *
 */
public class GroupCamera {

	private float width;
	private float height;
	private float zoom;
	private Group group;
	private Vector2 position;

	public GroupCamera(int width, int height, Group group) {
		this.width = width;
		this.height = height;
		this.group = group;
		this.zoom = 1f;
		this.position = new Vector2(0, 0);
	}

	public void setPosition(float x, float y) {
		position.set(x, y);
	}

	public void translate(float x, float y) {
		setPosition(position.x + x, position.y + y);
	}

	public void setZoom(float zoom) {
		this.zoom = Math.max(.5f, zoom);
	}

	public Vector2 getPosition() {
		return position;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public float getZoom() {
		return zoom;
	}

	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * transform the group attached to this camera
	 */
	public void update() {
		float xoffset = 0;// getWidth()/2;
		float yoffset = 0;// getHeight()/2;

		group.setTransform(true);
		group.setScale(zoom);
		group.setPosition(-position.x + (width - (width * zoom)) * (width / width * zoom), position.y + (height - (height * zoom)) * (height / height * zoom));

	}

	public Group getCameraGroup() {
		return group;
	}

}
