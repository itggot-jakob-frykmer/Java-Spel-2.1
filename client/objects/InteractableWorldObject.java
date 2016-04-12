package client.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.URL;

import client.Client;
import client.Main;
import client.Screen;
import client.handlers.InputHandler;
import client.handlers.Sound;

public abstract class InteractableWorldObject extends WorldObject {

	private boolean hovered = false;
	private Image imageHighlight;
	private boolean clickActivated = false; // kör så att click metoden bara körs en gång när man klickat på ett object

	protected int interactionTime = 0;

	private URL completeSound;

	// interactionsljud som spelas upp flera gånger med ett mellanrum
	private URL interactionSound;
	private int interactionSoundInterval = 0;;
	private int interactionTimeCounter = 0;

	public boolean playerInReach = false;

	int infoBoxWidth = 250;
	int infoBoxHeight = 200;

	int lineWrapWidth = (int) (infoBoxWidth * 0.6);



	int centerTextYOffset = 0;

	String infoText = "";

	int infoTextSize = 20;

	public InteractableWorldObject(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType) {
		super(x, y, width, height, paralax, imagePath, objectId, versionType);

		setImageHighlight(getHighlightImage());

		infoBoxHeight = infoTextSize * 2;

	}

	public abstract Image getHighlightImage();

	public abstract void uniqueOnStartInteraction();

	public void onStartInteraction() {
		uniqueOnStartInteraction();
		interactionTimeCounter = 0;
		playInteractionSound(); // spelar upp interactionsljudet i början
		sendInteractionSound();
	}

	@Override
	public void paint(Graphics2D g2d) {
		super.paint(g2d); // målar ut objektet som vanligt

		if (hovered) {
			paintInfoBox(g2d);
		}
	}

	public void paintInfoBox(Graphics2D g2d) {

		Screen.frontImageGraphics = Screen.setAlpha(Screen.frontImageGraphics, 0.6f);
		Screen.frontImageGraphics.setColor(new Color(255, 255, 255));
		int infoBoxX = InputHandler.onScreenMouseX;
		int infoBoxY = InputHandler.onScreenMouseY;

		Screen.frontImageGraphics.fillRect(infoBoxX, infoBoxY, infoBoxWidth, infoBoxHeight); // målar ut boxen

		Screen.frontImageGraphics = Screen.setAlpha(Screen.frontImageGraphics, 1f); // återsätller opaciteten

		// målar ut info texten på fronImageGraphics för att den ska komma över allt annat
		Screen.paintText(infoBoxX + infoBoxWidth / 2, infoBoxY + centerTextYOffset, Screen.secondFont, Font.PLAIN, infoTextSize, getInfoText(), Color.black, Screen.frontImageGraphics, lineWrapWidth, true);

	}

	public abstract void uniqueUpdate();

	public void updateInteraction() {
		interactionTimeCounter += Screen.sleep;

		// betyder att det inte ska spelas något interaktions-ljud på detta objekt
		if (interactionSoundInterval != 0) {
			// gör så att det blir ett intervall i vilket interaktions-ljudet spelas upp
			if (interactionTimeCounter >= interactionSoundInterval) {
				playInteractionSound();
				sendInteractionSound();
				interactionTimeCounter = 0;
			}
		}

	}

	@Override
	public void update() {
		uniqueUpdate();

		Rectangle objectRect = getCollisionBox();
		Rectangle mouseRect = new Rectangle(InputHandler.getWorldMouseX(), InputHandler.getWorldMouseY(), 1, 1);

		// om man håller på objektet
		if (objectRect.intersects(mouseRect)) {
			hovered = true;
			// om man klickar på objektet
			if (InputHandler.clicking && !clickActivated) {
				clickObject();
				clickActivated = true;
			}
		} else {
			hovered = false;
		}

		// om man är tillräckligt nära för att interacta med detta object
		if (objectRect.intersects(Main.clientPlayer.getCollisionBox())) {
			playerInReach = true;

			// om man försöker interacta med något
			if (Main.clientPlayer.isHoldingInteract()) {

				// kollar så man inte redan interactar med något
				if (!Main.clientPlayer.isInteracting()) {

					// kollar så man inte faller eller rör sig nu
					if (!Main.clientPlayer.isMoving() && !Main.clientPlayer.isFalling()) {
						startInteraction();
						onStartInteraction();

					}
				}
			}

			if (!InputHandler.clicking) {
				clickActivated = false;
			}

		} else { // när spelaren inte når objektet
			playerInReach = false;
		}

	}

	// skickar till alla andra klienter att interaktionsljudet ska spelas upp
	public void sendInteractionSound() {
		Client.sendData("#SENDINTERACTIONSOUND", getX() + "@" + getY() + "@" + getWidth() + "@" + getHeight());
	}

	public void playInteractionSound() {
		if (interactionSound != null) {
			Sound.play(interactionSound, getX(), getY(), 1f);
		}
	}

	public abstract void clickObject();

	public void startInteraction() {
		Main.clientPlayer.startInteraction(this);
	}

	public abstract void uniqueCompleteInteraction();

	public void completeInteraction() {
		uniqueCompleteInteraction();
		playCompleteSound();
	}

	public void playCompleteSound() {
		if (completeSound != null) {
			Sound.play(completeSound, getX(), getY(), 1f);
		}
	}

	@Override
	public Image getImage() {
		Image img = super.getImage();

		if (isHovered()) {
			img = imageHighlight;
		}

		if (img == null) {
			img = super.getImage();
		}

		return img;
	}

	public boolean isHovered() {
		return hovered;
	}

	// initaliserar highlight bilden för objecktet. Om det inte finns någon highlight bild blir bara hela bilden vitare
	public void setImageHighlight(Image imgHighlight) {

		Image objectImage = super.getImage();

		BufferedImage bimage = new BufferedImage(objectImage.getWidth(null), objectImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(objectImage, 0, 0, null);

		if (imgHighlight != null) {
			bGr.drawImage(imgHighlight, 0, 0, objectImage.getWidth(null), objectImage.getHeight(null), null);
		} else {
			// bGr.setColor(Color.white);
			// Screen.setScreenAlpha(bGr, 0.3f);
		}

		this.imageHighlight = bimage;

	}

	public void setInteractionSoundInterval(int interval) {
		this.interactionSoundInterval = interval;
	}

	public void setInteractionSound(URL url) {
		this.interactionSound = url;
	}

	public void setCompleteSound(URL url) {
		this.completeSound = url;
	}

	public int getInteractionTime() {
		return interactionTime;
	}

	public void setInfoText(String info) {
		infoText = info;
		calculateBoxHeight();
	}

	public void calculateBoxHeight() {

		// kollar hur många rader texten kommer bli
		String[] lines = Screen.wrapLines(getInfoText(), Screen.secondFont, lineWrapWidth);
		int numLines = lines.length;
		int textHeight = numLines * infoTextSize;

		infoBoxHeight = textHeight + infoTextSize * 2; // hur hög boxen ska vara

		centerTextYOffset = infoBoxHeight / 2 - textHeight / 2;

	}

	public String getInfoText() {
		return infoText;
	}

	

}
