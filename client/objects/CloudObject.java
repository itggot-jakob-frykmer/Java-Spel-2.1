package client.objects;

import java.util.Random;

import client.handlers.MapHandler;
import client.objects.backgroundObjects.BackgroundObject;

public class CloudObject extends BackgroundObject {

	private double xSpeed = 0.2;
	private double speedVary = 0.3;

	private double x = 0;
	private double y = 0;

	private int maxX = MapHandler.worldWidth / 4;

	public CloudObject(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType) {
		super(x, y, width, height, paralax, imagePath, objectId, versionType);

		Random ra = new Random();

		this.x = x;
		this.y = y;
		xSpeed += speedVary * ra.nextDouble();
	}

	@Override
	public void update() {

		x += xSpeed;

		// om molnet gått för långt
		if (x >= maxX) {
			x = -2000;
		}
	}

	@Override
	public void onRemove() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getX() {
		return (int) x;
	}

	@Override
	public int getY() {
		return (int) y;
	}

}
