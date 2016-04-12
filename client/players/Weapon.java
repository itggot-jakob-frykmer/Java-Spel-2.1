package client.players;

import java.awt.Graphics2D;
import java.awt.Image;

import client.Screen;
import client.handlers.Images;
import client.objects.interactableWorldObjects.WeaponShrine;

public class Weapon {

	private Image image;
	private int versionType;
	private int level;

	// vapnet kan antingen hållas av ett shrine eller av en spelare
	private Player holder;
	private WeaponShrine holderShrine;

	// storleken på vapnen
	private double scale = 0.3;
	private int width = (int) (100 * scale);
	private int height = (int) (559 * scale);

	private int xOffset;
	private int yOffset;
	private double rotation;

	// konstruktor för när en spelare ska ha vapnet
	public Weapon(int versionType, int level, Player player) {
		init(versionType, level);
		this.holder = player;
	}

	// konstruktor för när et shrine ska ha vapnet
	public Weapon(int versionType, int level, WeaponShrine shrine) {
		init(versionType, level);
		this.holderShrine = shrine;

		// placerar vapnet rätt
		xOffset = shrine.getWidth() / 2;
		yOffset = 0;

		if (versionType == 0) {
			//d rotation = Math.PI;
			// yOffset = -20;
		}

	}

	public void init(int versionType, int level) {
		this.image = Images.readImageFromPath("weapons/weapon" + versionType + "_" + level + ".png");
		this.versionType = versionType;
		this.level = level;
	}

	public int getVersionType() {
		return versionType;
	}

	public int getLevel() {
		return level;
	}

	public Image getImage() {
		return image;
	}

	public void paint(Graphics2D g2d) {
		Graphics2D g2 = (Graphics2D) g2d.create();

		int x = getHolderX();
		int y = getHolderY();

		g2.rotate(rotation, Screen.fixX(x, 1), Screen.fixY(y, 1));

		int modWidth = 1;
		int extraX = 0;

		// om vapnet hålls av en spelare som kollar åt vänster
		if (holder != null && holder.isFacingLeft()) {
			modWidth = -1;
			extraX = width;
		}

		g2.drawImage(image, Screen.fixX(x - width / 2, 1) + extraX, Screen.fixY(y - height / 2, 1), width * modWidth, height, null); // målar ut vapnet

	}

	public int getHolderX() {
		int x;
		if (holder == null) {
			x = holderShrine.getX() + xOffset;
		} else {
			x = holder.getX() + xOffset;
		}
		return x;

	}

	public int getHolderY() {
		int y;
		if (holder == null) {
			y = holderShrine.getY() + yOffset;
		} else {
			y = holder.getY() + yOffset;
		}
		return y;
	}

	public void updatePosition() {

		int currentImage = holder.getCurrentImageIndex();

		// om man faller är det alltid bild 13
		if (holder.isFalling()) {
			currentImage = 13;
		}

		double rotPercent = 0;

		boolean movingOrFalling = holder.isMoving() || holder.isFalling();

		// om den nuvarande bildens index är över 1 betyder det att man enligt gubbens bild inte står stilla än. Detta behövs för att ibland blir moving och falling false innan den byter till en bild för när man står stilla på grund av att den inte uppdateras hela tiden
		if (currentImage > 1) {
			if (!holder.isMoving() && !holder.isFalling()) {
				movingOrFalling = true;
			}
		}

		if (movingOrFalling) {

			if (currentImage == 0) {
				xOffset = 291;
				yOffset = 355;
				rotPercent = 2;
			} else if (currentImage == 1) {
				xOffset = 245;
				yOffset = 414;
				rotPercent = 5;
			} else if (currentImage == 2) {
				xOffset = 193;
				yOffset = 446;
				rotPercent = 10;
			} else if (currentImage == 3) {
				xOffset = 179;
				yOffset = 449;
				rotPercent = 15;
			} else if (currentImage == 4) {
				xOffset = 154;
				yOffset = 421;
				rotPercent = 20;
			} else if (currentImage == 5) {
				xOffset = 130;
				yOffset = 365;
				rotPercent = 25;
			} else if (currentImage == 6) {
				xOffset = 130;
				yOffset = 365;
				rotPercent = 25;
			} else if (currentImage == 7) {
				xOffset = 130;
				yOffset = 365;
				rotPercent = 25;
			} else if (currentImage == 8) {
				xOffset = 154;
				yOffset = 421;
				rotPercent = 20;
			} else if (currentImage == 9) {
				xOffset = 179;
				yOffset = 449;
				rotPercent = 15;
			} else if (currentImage == 10) {
				xOffset = 193;
				yOffset = 446;
				rotPercent = 10;
			} else if (currentImage == 11) {
				xOffset = 245;
				yOffset = 414;
				rotPercent = 5;
			} else if (currentImage == 12) {
				xOffset = 291;
				yOffset = 355;
				rotPercent = 2;
			} else if (currentImage == 13) {
				xOffset = 291;
				yOffset = 355;
				rotPercent = 2;
			}
			// byter håll på vinkeln på staven
			if (holder.isFacingLeft()) {
				// rotation += Math.PI * -0.5;
				// rotPercent -= 25;
				rotPercent = rotPercent - 25;
			}

		} else { // om man står stilla
			if (currentImage == 0) {
				xOffset = 193;
				yOffset = 422;
				rotPercent = 20;
			} else if (currentImage == 1) {
				xOffset = 219;
				yOffset = 447;
				rotPercent = 21;
			}

			// byter håll på vinkeln på staven
			if (holder.isFacingLeft()) {
				rotPercent = -rotPercent;
			}

		}
		// skalar efter skalan på karaktären
		xOffset *= Player.sizeScale;
		yOffset *= Player.sizeScale;

		rotation = Math.PI * 2.0 * rotPercent * 0.01;
		/*
		 * System.out.println("----"); System.out.println("rotpercent: " + rotPercent); System.out.println("rotation: " + rotation);
		 */

	}

}
