package client.ui.mainMenu;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract class Component {

	int x;
	int y;
	int width;
	int height;

	Image image;
	Image hoverImage;

	String description = "";

	boolean hovered = false;

	public Component(int x, int y, int width, int height, String description) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.description = description;

	}

	public abstract void paintDescription(Graphics2D g2d);

	public void paint(Graphics2D g2d) {
		g2d.drawImage(image, x, y, width, height, null);

		if (hovered && hoverImage != null) {
			g2d.drawImage(hoverImage, x, y, width, height, null);
		}

		paintDescription(g2d);
	}

	public void setImage(Image img) {
		this.image = img;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getDescription() {
		return description;
	}

	public void setHoverImage(Image img) {
		this.hoverImage = img;
	}

	public Rectangle getCollisionBox() {
		return new Rectangle(x, y, width, height);
	}

	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}

	public boolean getHovered() {
		return hovered;
	}

	

	public abstract void handleKeyEvent(KeyEvent e);

	public abstract void handleClick(MouseEvent e);

	public abstract void handleNotClick(MouseEvent e); // körs när man klickar någon annanstans än på komponenten för att ta bort focus

}
