package server.objects;

import server.ServerMap;

public class WorldObject {

	private int x;
	private int y;
	private int width;
	private int height;
	private double paralax;
	private String itemType;
	private int versionType;
	private int objectId;

	public WorldObject(int x, int y, int width, int height, double paralax, String itemType, int versionType) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.paralax = paralax;
		this.itemType = itemType;
		this.versionType = versionType;
		this.objectId = ServerMap.idCounter;
		ServerMap.idCounter++;
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

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public int getVersionType() {
		return versionType;
	}

	public double getParalax() {
		return paralax;
	}

	public int getObjectId() {
		return objectId;
	}

}