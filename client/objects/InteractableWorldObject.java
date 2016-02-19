package client.objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import client.Main;
import client.Screen;
import client.handlers.InputHandler;

public abstract class InteractableWorldObject extends WorldObject {

	private boolean hovered = false;
	private Image imageHighlight;
	private boolean clickActivated = false; // k�r s� att click metoden bara k�rs en g�ng n�r man klickat p� ett object

	private int interactionTime = 2000;

	public InteractableWorldObject(int x, int y, int width, int height, double paralax, String imagePath, int objectId) {
		super(x, y, width, height, paralax, imagePath, objectId);
	}

	@Override
	public void update() {
		Rectangle objectRect = getCollisionBox();
		Rectangle mouseRect = new Rectangle(InputHandler.getWorldMouseX(), InputHandler.getWorldMouseY(), 1, 1);

		if (objectRect.intersects(mouseRect)) {
			hovered = true;

			if (InputHandler.clicking && !clickActivated) {
				clickObject();
				clickActivated = true;
			}
		} else {
			hovered = false;
		}

		// kollar s� man inte redan interactar med n�got
		if (!Main.clientPlayer.isInteracting()) {
			// om man f�rs�ker interacta med n�got
			if (Main.clientPlayer.isHoldingInteract()) {

				// om man �r tillr�ckligt n�ra f�r att interacta med detta object
				if (objectRect.intersects(Main.clientPlayer.getCollisionBox())) {
					startInteraction();
				}
			}

			if (!InputHandler.clicking) {
				clickActivated = false;
			}
		}

	}

	public abstract void clickObject();

	public void startInteraction() {
		Main.clientPlayer.startInteraction(this);
	}

	public abstract void completeInteraction();

	@Override
	public Rectangle createCollisionBox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getImage() {
		Image img = super.getImage();

		if (isHovered()) {
			img = imageHighlight;
		}

		return img;
	}

	public int getInteractionTime() {
		return interactionTime;
	}

	public boolean isHovered() {
		return hovered;
	}

	// initaliserar highlight bilden f�r objecktet. Om det inte finns n�gon highlight bild blir bara hela bilden vitare
	public void setImageHighlight(Image imgHighlight) {

		Image objectImage = super.getImage();

		BufferedImage bimage = new BufferedImage(objectImage.getWidth(null), objectImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(objectImage, 0, 0, null);

		if (imgHighlight != null) {
			bGr.drawImage(imgHighlight, 0, 0, objectImage.getWidth(null), objectImage.getHeight(null), null);
		} else {
			bGr.setColor(Color.white);
			Screen.setScreenAlpha(bGr, 0.3f);
		}

		this.imageHighlight = bimage;

	}
}
