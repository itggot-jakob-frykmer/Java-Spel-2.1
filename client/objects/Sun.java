package client.objects;

import java.awt.Graphics2D;

import client.Screen;
import client.handlers.MapHandler;

public class Sun extends BackgroundObject {

	public Sun(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType) {
		super(x, y, width, height, paralax, imagePath, objectId, versionType);

	}

	@Override
	public void paint(Graphics2D g2d) {
		int x = Screen.screenWidth / 2 - getWidth() / 2;
		int y = Screen.fixY(MapHandler.groundLevel - 4000, 0.2);

		g2d.drawImage(getImage(), x, y, getWidth(), getHeight(), null);
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemove() {
		// TODO Auto-generated method stub

	}

}
