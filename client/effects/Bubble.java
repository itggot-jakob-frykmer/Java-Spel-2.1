package client.effects;

import java.awt.Image;
import java.util.Random;

import client.Main;
import client.Screen;
import client.handlers.Images;

public class Bubble extends Effect {

	double xSpeed = 0;
	double ySpeed = -0.07;

	double xAcc = 0.00008;
	double xAccVar = 0.00006;

	double yAcc = 0.000001;

	int direction = 1; // 1 = right -1 = left

	double sizeDec = 0.03;

	private Image image;

	private int delay; // hur länge den ska vänta innan den ska synas
	private boolean animationStarted = false;
	private int delayCounter = 0;

	public Bubble(int x, int y, int width, int height, int versionType, int delay) {
		super(x, y, width, height);

		this.delay = delay;

		image = Images.readImageFromPath("effects/bubble" + versionType + ".png");

		Random ra = new Random();

		xAcc += xAccVar * ra.nextDouble(); // get xAccelerationen lite slump

		// slumpar håll
		if (ra.nextInt(2) == 0) {
			direction = -1;
			xAcc *= -1;
		}
		
		setVisible(false); // ska inte synas från början, börjar synas efter en viss tid

	}

	@Override
	public void update() {

		delayCounter += Screen.sleep;
		if (animationStarted) {

			ySpeed += yAcc;
			xSpeed += xAcc;

			move(xSpeed, ySpeed);

			changeSize(sizeDec);

			if (getWidth() <= 0) {
				Main.effects.remove(this);
			}
		} else {
			if (delayCounter >= delay) {
				animationStarted = true;
				setVisible(true);
			}
		}
	}

	@Override
	public Image getImage() {
		return image;
	}
}
