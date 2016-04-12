package client.players;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import client.Main;
import client.Screen;
import client.handlers.Images;

public class HealthBar {

	private int health;
	private int maxHealth = 100;
	
	private int energy;
	private int maxEnergy = 100;

	private Image gradImage;
	private int displayWidth = 100;
	private int displayHeight = 9;

	private int xOffset = -10;
	private int yOffset = -20;

	private Player holder;

	public HealthBar(Player holder) {
		this.holder = holder;
		this.health = maxHealth;
		this.energy = maxEnergy;
		this.gradImage = Images.readImageFromPath("ui/health_grad.png");
	}

	public void paint(Graphics2D g2d) {
		g2d = (Graphics2D) g2d.create();

		int barWidth = (int) (((getHealth() * 1.0) / (maxHealth)) * displayWidth); // räknar ut hur bred healthbaren ska vara

		int x = Screen.fixX(holder.getX() + xOffset, 1); // räknar ut var den ska sitta
		int y = Screen.fixY(holder.getY() + yOffset, 1);

		// målar ut HP-baren
		g2d.setColor(new Color(90, 0, 0));
		g2d.fillRect(x, y, displayWidth, displayHeight);
		g2d.setColor(new Color(150, 18, 18));
		g2d.fillRect(x, y, barWidth, displayHeight);
		g2d.drawImage(gradImage, x, y, displayWidth, displayHeight, null);

		barWidth = (int) (((getEnergy() * 1.0) / (maxEnergy)) * displayWidth); // räknar ut hur bred healthbaren ska vara

		y += displayHeight; // sätter energy-baren längre ner
		int energyHeight = displayHeight / 2;

		// Målar ut energy-baren
		g2d.setColor(new Color(50, 35, 0));
		g2d.fillRect(x, y, displayWidth, energyHeight);
		g2d.setColor(new Color(177, 126, 0));
		g2d.fillRect(x, y, barWidth, energyHeight);
		g2d.drawImage(gradImage, x, y, displayWidth, energyHeight, null);

		// g2d.Arc(200, 200, 400, 400, 90, -90);
	}

	
	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		if (energy > maxEnergy) {
			energy = maxEnergy;
		}
		
		if (energy < 0) {
			energy = 0;
		}
		
		this.energy = energy;
		
		
	}
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		if (health > maxHealth) {
			health = maxHealth;
		}
		if (health < 0) {
			health = 0;
		}
		this.health = health;
		
		
	}

}