package client.players.actionbar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.concurrent.atomic.AtomicInteger;

import client.Screen;
import client.handlers.Images;

public class CounterIcon extends Icon {

	Image image;
	AtomicInteger counter;

	public CounterIcon(int x, int y, int width, int height, String imagePath, AtomicInteger counter) {
		super(x, y, width, height);
		image = Images.readImageFromPath(imagePath);
		this.counter = counter;
	}

	public void paint(Graphics2D g2) {
		g2.drawImage(getImage(), getX(), getY(), getWidth(), getHeight(), null);

		Font font = new Font("Calibri", Font.BOLD, 25);
		g2.setFont(font);
		g2.setColor(Color.WHITE);
		
		String text = counter.get() + "";
		int textHeight = Screen.getTextHeight(g2, text, g2.getFont());
		int textY = getY() + getHeight() - textHeight / 4;
		int textX = getX() + getWidth() + 5;

		g2.drawString(text, textX, textY);
	}

	public Image getImage() {
		return image;
	}

}