package com.kyper.btedit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Assets {
	
	public static class Textures{
		public static Texture BLUE;
		public static Texture RED;
		public static Texture YELLOW;
		public static Texture GREEN;
		public static Texture Gear;
		public static Texture ArrowLeft;
		public static Texture ArrowRight;
		public static Texture Up;
		public static Texture Down;
		public static Texture Cross;
		public static Texture Plus;
		
	}
	
	public static class Styles{
		public static ImageButtonStyle leftButton;
		public static ImageButtonStyle rightButton;
		public static ImageButtonStyle addButton;
		public static ImageButtonStyle removeButton;
		public static ImageButtonStyle upButton;
		public static ImageButtonStyle downButton;
		public static ImageButtonStyle editButton;
	}
	
	public static void createStyles() {
		Styles.leftButton = new ImageButtonStyle();
		Styles.leftButton.imageUp = new TextureRegionDrawable(new TextureRegion(Textures.ArrowLeft));
		Styles.leftButton.pressedOffsetX = -3;
		Styles.leftButton.pressedOffsetY = -3;
		
		Styles.rightButton = new ImageButtonStyle();
		Styles.rightButton.imageUp = new TextureRegionDrawable(new TextureRegion(Textures.ArrowRight));
		Styles.rightButton.pressedOffsetX = -3;
		Styles.rightButton.pressedOffsetY = -3;
		
		Styles.addButton = new ImageButtonStyle();
		Styles.addButton.imageUp = new TextureRegionDrawable(new TextureRegion(Textures.Plus));
		Styles.addButton.pressedOffsetX = -3;
		Styles.addButton.pressedOffsetY = -3;
		
		Styles.removeButton = new ImageButtonStyle();
		Styles.removeButton.imageUp = new TextureRegionDrawable(new TextureRegion(Textures.Cross));
		Styles.removeButton.pressedOffsetX = -3;
		Styles.removeButton.pressedOffsetY = -3;
		
		Styles.upButton = new ImageButtonStyle();
		Styles.upButton.imageUp = new TextureRegionDrawable(new TextureRegion(Textures.Up));
		Styles.upButton.pressedOffsetX = -3;
		Styles.upButton.pressedOffsetY = -3;
		
		Styles.downButton = new ImageButtonStyle();
		Styles.downButton.imageUp = new TextureRegionDrawable(new TextureRegion(Textures.Down));
		Styles.downButton.pressedOffsetX = -3;
		Styles.downButton.pressedOffsetY = -3;
		
		Styles.editButton = new ImageButtonStyle();
		Styles.editButton.imageUp = new TextureRegionDrawable(new TextureRegion(Textures.Gear));
		Styles.editButton.pressedOffsetX = -3;
		Styles.editButton.pressedOffsetY = -3;
		
	}
	
	public static void loadTextures() {
		System.out.println("textures loaded");
		Textures.BLUE = new Texture(Gdx.files.internal("blue.png"));
		Textures.RED = new Texture(Gdx.files.internal("red.png"));
		Textures.YELLOW = new Texture(Gdx.files.internal("yellow.png"));
		Textures.GREEN = new Texture(Gdx.files.internal("green.png"));
		Textures.Gear = new Texture(Gdx.files.internal("gear.png"));
		Textures.ArrowLeft = new Texture(Gdx.files.internal("arrowLeft.png"));
		Textures.ArrowRight = new Texture(Gdx.files.internal("arrowRight.png"));
		Textures.Up = new Texture(Gdx.files.internal("up.png"));
		Textures.Down = new Texture(Gdx.files.internal("down.png"));
		Textures.Cross = new Texture(Gdx.files.internal("cross.png"));
		Textures.Plus = new Texture(Gdx.files.internal("plus.png"));
		
		
	}
	
	public static void disposeTextures() {
		System.out.println("textures disposed");
		Textures.BLUE.dispose();
		Textures.RED.dispose();
		Textures.YELLOW.dispose();
		Textures.GREEN.dispose();
		Textures.Gear.dispose();
		Textures.ArrowLeft.dispose();
		Textures.ArrowRight.dispose();
		Textures.Up.dispose();
		Textures.Down.dispose();
		Textures.Cross.dispose();
		Textures.Plus.dispose();
	}
	

}
