package client.players.actionbar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import client.handlers.Images;

public class ReadyIcon extends Icon {

	Image readyImage;
	Image notReadyImage;

	AtomicBoolean ready;
	AtomicInteger cooldown;
	AtomicInteger cooldownCounter;
	AtomicBoolean unlocked;

	public ReadyIcon(int x, int y, int width, int height, AtomicBoolean ready, AtomicInteger cooldown, AtomicInteger cooldownCounter, String readyImagePath, String notReadyImagePath, AtomicBoolean unlocked) {
		super(x, y, width, height);
		this.ready = ready;
		this.readyImage = Images.readImageFromPath(readyImagePath);
		this.notReadyImage = Images.readImageFromPath(notReadyImagePath);
		this.cooldown = cooldown;
		this.cooldownCounter = cooldownCounter;
		this.unlocked = unlocked;
	}

	public void paint(Graphics2D g2) {
		g2.drawImage(getImage(), getX(), getY(), getWidth(), getHeight(), null);
	}

	public Image getImage() {
		Image img;
		if (ready.get() && unlocked.get()) { // om den är redo kommer båda bilderna målas på varandra och den kommer se grön ut

			int imgWidth = readyImage.getWidth(null);
			int imgHeight = readyImage.getHeight(null);

			BufferedImage bimage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);

			Graphics2D bGr = bimage.createGraphics();

			bGr.drawImage(readyImage, 0, 0, null);
			bGr.drawImage(notReadyImage, 0, 0, null);

			img = bimage;
		} else {

			double completePercent = (cooldownCounter.doubleValue() / cooldown.doubleValue());

			int imgWidth = readyImage.getWidth(null);
			int imgHeight = readyImage.getHeight(null);

			BufferedImage bimage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);

			// Draw the image on to the buffered image
			Graphics2D bGr = bimage.createGraphics();

			bGr.drawImage(readyImage, 0, 0, null);

			bGr.setBackground(new Color(255, 255, 255, 0)); // sätter 0 opastitet
			// skär bort en del av det gröna
			int clearHeight = (int) (imgHeight * completePercent);
			bGr.clearRect(0, 0, imgWidth, imgHeight - clearHeight);

			bGr.drawImage(notReadyImage, 0, 0, null);

			img = bimage;
		}

		return img;
	}

}