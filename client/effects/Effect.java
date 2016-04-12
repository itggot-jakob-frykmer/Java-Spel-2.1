package client.effects;

import java.awt.Graphics2D;
import java.awt.Image;

import client.Screen;

public abstract class Effect {

	private double x;
	private double y;
	private double width;
	private double height;

	private boolean visible = true;;

	public Effect(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void paint(Graphics2D g2d) {
		if (visible) {
			g2d.drawImage(getImage(), Screen.fixX((int) x, 1), Screen.fixY((int) y, 1), (int) width, (int) height, null);
		}
	}

	public abstract void update();

	public abstract Image getImage();

	public void changeSize(double change) {
		width -= change;
		height -= change;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void move(double x, double y) {
		this.x += x;
		this.y += y;
	}

	public int getWidth() {
		return (int) width;
	}

	public int getHeight() {
		return (int) height;
	}
}
