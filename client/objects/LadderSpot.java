package client.objects;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import client.Main;
import client.handlers.Images;
import client.handlers.MapHandler;

public class LadderSpot extends InteractableWorldObject {

	private int cost = 5;

	public LadderSpot(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType) {
		super(x, y, width, height, paralax, imagePath, objectId, versionType);

		interactionTime = 5000;

		setInfoText("Hold 'E' to build a ladder here# Cost: " + cost + " wood");


		setImageHighlight(Images.readImageFromPath("objects/ladderHighlight.png"));

		initImage();

	}

	// gör om bilden så den får opacitet
	public void initImage() {

		BufferedImage bimage = new BufferedImage(getImage().getWidth(null), getImage().getHeight(null), BufferedImage.TYPE_INT_ARGB);

		Graphics2D bGr = bimage.createGraphics();

		bGr.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		bGr.drawImage(getImage(), 0, 0, null);

		setImage(bimage);
	}

	@Override
	public Rectangle createCollisionBox() {
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}

	@Override
	public void onRemove() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clickObject() {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getHighlightImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void uniqueUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniqueCompleteInteraction() {

		Main.clientPlayer.removeWood(cost); // tar bort vad det kostade att göra detta

		MapHandler.sendSpawnLadder(getObjectId());

		MapHandler.sendRemoveObject(this);
		MapHandler.removeObject(this);
	}

	@Override
	public void uniqueOnStartInteraction() {

		// det kostar wood att interacta med detta objekt
		if (Main.clientPlayer.getNumWood() < cost) {
			Main.clientPlayer.stopInteraction();
		}

	}

}
