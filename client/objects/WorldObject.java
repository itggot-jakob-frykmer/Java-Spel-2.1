package client.objects;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import client.Screen;
import client.handlers.Images;

// Detta är moderklassen till alla objekt i världen som skickas får servern när man ansluter.
public abstract class WorldObject {

	private int x;
	private int y;
	private int width;
	private int height;
	private double paralax;
	private Image image;
	private Rectangle collisionBox;
	private int objectId;
	private int versionType;

	public WorldObject(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.paralax = paralax;
		this.image = Images.readImageFromPath(imagePath);
		this.objectId = objectId;
		this.versionType = versionType;

		setCollisionBox(createCollisionBox());
	}

	public abstract void update();

	public Rectangle createCollisionBox() {
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}

	public abstract void onRemove();

	public void paint(Graphics2D g2d) {
		g2d.drawImage(getImage(), Screen.fixX(getX(), getParalax()), Screen.fixY(getY(), getParalax()), getWidth(), getHeight(), null);
	}

	public int getVersionType() {
		return versionType;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setImage(Image img) {
		this.image = img;
	}

	public Image getImage() {
		return image;
	}

	public Rectangle getCollisionBox() {
		return collisionBox;
	}

	public void setCollisionBox(Rectangle rect) {
		this.collisionBox = rect;
	}

	public double getParalax() {
		return paralax;
	}

	public int getObjectId() {
		return objectId;
	}

}
