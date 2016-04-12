package client.objects.backgroundObjects;

import java.awt.Rectangle;

import client.objects.WorldObject;

public class BackgroundObject extends WorldObject {

	public BackgroundObject(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType) {
		super(x, y, width, height, paralax, imagePath, objectId, versionType);
	}



	@Override
	public Rectangle createCollisionBox() {
		// TODO Auto-generated method stub
		return new Rectangle(0, 0, 0, 0);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemove() {
		// TODO Auto-generated method stub

	}
}