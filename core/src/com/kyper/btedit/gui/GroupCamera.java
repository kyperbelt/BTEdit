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
	private Vector2 translation;
	private Vector2 position;
	private Vector2 lerp;
	private Vector2 target;

	public GroupCamera(int width, int height, Group group) {
		this.width = width;
		this.height = height;
		this.group = group;
		this.zoom = 1f;
		this.position = new Vector2(0, 0);
		this.translation = new Vector2(0, 0);
		this.lerp = new Vector2();
		this.target = new Vector2();
	}

	/**
	 * set the position this camera rests at
	 * @param x
	 * @param y
	 */
	public void setAnchorPos(float x, float y) {
		position.set(x, y);
	}

	public void translate(float x, float y) {
		translation.add(x, y);
	}
	
	public void setPosition(float x,float y) {
		translation.set(x,y);
	}

	public void setZoom(float zoom) {
		this.zoom = Math.max(.5f, zoom);
	}

	/**
	 * the position this camera considers its anchor or resting place -- 0-0
	 * @return
	 */
	public Vector2 getAnchorPos() {
		return position;
	}

	public Vector2 getTranslation() {
		return translation;
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

	public void update() {
		this.update(false);
	}

	/**
	 * transform the group attached to this camera
	 */
	public void update(boolean interpolate) {

		if (interpolate) {

			lerp.set(group.getX(), group.getY());
			target.set(position.x + translation.x, position.y + translation.y);
			lerp.lerp(target, .1f);

			group.setPosition(lerp.x,lerp.y);
		}else {
			group.setPosition(position.x + translation.x, position.y + translation.y);
		}
		// float xoffset = 0;// getWidth()/2;
		// float yoffset = 0;// getHeight()/2;

		// group.setTransform(true);
		// group.setScale(zoom);
		// group.setPosition(-position.x + (width - (width * zoom)) * (width / width *
		// zoom), position.y + (height - (height * zoom)) * (height / height * zoom));

	}

	public Group getCameraGroup() {
		return group;
	}

}
