package client.players.spells;

import java.awt.Graphics2D;
import java.awt.Image;

import client.Main;
import client.Screen;
import client.handlers.Images;
import client.players.Player;

// En av lagens power-ability är att göra en sköld med eldbollar som snurrar runt en. Detta objekt är för hela skölden
public class FireShield {

	private int numCharges;
	private Image image;
	private Player owner;

	private double angle = 0;
	private double rotationSpeed = 0.018;
	private int radius = 100;

	int ballWidth = 20;
	int ballHeight = 20;

	public FireShield(int numCharges, Player player) {
		this.numCharges = numCharges;
		this.image = Images.readImageFromPath("spells/shieldBall.png");
		this.owner = player;
	}

	public void paint(Graphics2D g2d) {

		double rotSplit = (Math.PI * 2) / numCharges; // extra vinkeln för varje boll så de sprider ut sig

		int x = Screen.fixX(owner.getX() + owner.getWidth() / 2, 1);
		int y = Screen.fixY(owner.getY() + owner.getHeight() / 2, 1);

		// målar ut alla bollar
		for (int i = 0; i < numCharges; i++) {
			Graphics2D g2dCopy = (Graphics2D) (g2d.create());
			// roterar så alla bollar hamnar rätt
			g2dCopy.rotate(rotSplit * i + angle, x, y);

			g2dCopy.drawImage(image, x + radius, y, ballWidth, ballHeight, null);
		}
	}

	public void update() {
		angle += rotationSpeed;
	}

	// konsumerar en charge och skickar info till andra spelare om hur många charges man har nu
	public void consumeCharge() {
		numCharges--;
		Main.clientPlayer.sendShieldInfo();
	}

	public void setCharges(int num){
		this.numCharges = num;
	}

	public void renewShield(){
		setCharges(5);
		Main.clientPlayer.sendShieldInfo();
	}
	
	public int getNumCharges() {
		return numCharges;
	}

}
