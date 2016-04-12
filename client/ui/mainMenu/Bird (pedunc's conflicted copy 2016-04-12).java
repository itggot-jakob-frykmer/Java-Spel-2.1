package client.ui.mainMenu;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Random;

import client.Main;
import client.Screen;
import client.handlers.Images;


// fåglar som flyger över mainmeny skärmen 
public class Bird {

	private double x;
	private int y;

	private int width;
	private int height;

	private double xSpeed = 2;

	private ArrayList<Image> images = new ArrayList<Image>();
	private int currentImage = 0;
	private int imageChangeDelay = 20;
	private int tick = 0;

	public Bird(int x, int y) {
		this.x = x;
		this.y = y;

		initSize();
		initImages();
	}

	public void initImages() {
		for (int i = 0; i < 10; i++) {
			images.add(Images.readImageFromPath("npc/bird/bird_" + i + ".png"));
		}
		Random ra = new Random();
		currentImage = ra.nextInt(images.size() - 1);
	}

	public void initSize() {
		Random ra = new Random();

		double scale = ra.nextDouble() * 0.1;
		width = (int) (269 * scale);
		height = (int) (501 * scale);

		xSpeed = (xSpeed * scale);
	}

	public void paint(Graphics2D g2d) {
		g2d.drawImage(images.get(currentImage), (int) x, y, width, height, null);
	}

	public void update() {
		tick++;

		x += xSpeed;

		// byter bild ibland
		if (tick % imageChangeDelay == 0) {
			currentImage++;
			if (currentImage >= images.size()) {
				currentImage = 0;
			}
		}

		// tar bort fågeln när den rört sig för lånt
		if(x > Screen.screenWidth){
			Main.mainMenu.birds.remove(this);
		}
	}
}
