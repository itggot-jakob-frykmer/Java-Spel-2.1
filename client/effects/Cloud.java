package client.effects;

import java.awt.Image;

import client.Main;
import client.handlers.Images;

public class Cloud extends Effect {

	private double movingSpeedX;
	private double movingSpeedY;

	static double movingSpeed = 1;
	static double sizeChangeSpeed = 0.5;
	static Image image;

	public Cloud(int x, int y, int width, int height, int deg) {
		super(x, y, width, height);

		double rad = Math.toRadians(deg);

		movingSpeedX = Math.cos(rad) * movingSpeed;
		movingSpeedY = Math.sin(rad) * movingSpeed;

		image = Images.readImageFromPath("effects/cloud.png");

		setVisible(true);
	}

	@Override
	public void update() {
		move(movingSpeedX, movingSpeedY);
		changeSize(sizeChangeSpeed);

		// tar bort effekten när den blivit liten
		if (getWidth() < 0 || getHeight() < 0) {
			Main.effects.remove(this);
		}
	}

	@Override
	public Image getImage() {
		return image;
	}
}
