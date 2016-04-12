package client.objects;

import java.awt.Image;
import java.util.ArrayList;

import client.Screen;
import client.handlers.Images;
import client.handlers.MapHandler;

public class OpenChest extends WorldObject {

	private int timeExisted = 0;
	private int maxLifeTime = 5000;

	// variabler för animationen på kistan
	private int animationCounter = 0;
	private int changeImageDelay = 75;
	private boolean animationComplete = false;
	private int currentImage = 0;

	// gemensamma för alla öppna kistor
	public static ArrayList<Image> images = new ArrayList<Image>(); // static för att spara på ramminnet
	public static int numImages = 3;

	public OpenChest(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType) {
		super(x, y, width, height, paralax, imagePath, objectId, versionType);
	}

	// läser in alla bilder för animationen. Körs från Images.java när spelet startas
	public static void initImages() {
		for (int i = 0; i < numImages; i++) {
			Image image = Images.readImageFromPath("chests/openChest" + (i + 1) + ".png");
			images.add(image);
		}
	}

	@Override
	public void update() {
		animationCounter += Screen.sleep;
		timeExisted += Screen.sleep;

		// när det gått en viss tid byter kistan bild till en helt öppen kista för att animera den
		if (animationCounter >= changeImageDelay && !animationComplete) {
			// byter bild
			setImage(images.get(currentImage));
			currentImage++;
			animationCounter = 0;

			// om animationen är på sista bilden
			if (currentImage == images.size()) {
				animationComplete = true;
			}

		}

		// tar bort den tomma kistan efter en stund
		if (timeExisted >= maxLifeTime) {
			MapHandler.removeObject(this);
		}

	}

	@Override
	public void onRemove() {
		// TODO Auto-generated method stub

	}

}
