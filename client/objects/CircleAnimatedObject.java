package client.objects;

import java.awt.Graphics2D;

import client.Screen;

public class CircleAnimatedObject extends WorldObject {

	double angle = 0;
	double speed;
	int radius;

	public CircleAnimatedObject(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType, int radius, double speed) {
		super(x, y, width, height, paralax, imagePath, objectId, versionType);
		
		this.speed = speed;
		this.radius = radius;
	}

	@Override
	public void paint(Graphics2D g2d) {
		Graphics2D g2dCopy = (Graphics2D) g2d.create();

		g2dCopy.rotate(angle, Screen.fixX(getX() + getWidth() / 2, 1), Screen.fixY(getY() + getHeight() / 2, 1));
		g2dCopy.drawImage(getImage(), Screen.fixX(getX() - radius, 1), Screen.fixY(getY() - radius, 1), getWidth(), getHeight(), null);

		g2dCopy.dispose();
	}

	@Override
	public void update() {
		if (!Screen.devMode) {
			angle += speed;
		}

	}

	@Override
	public void onRemove() {
		// TODO Auto-generated method stub

	}

}
