package client.objects.lootableWorldObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.URL;

import client.Main;
import client.Screen;
import client.handlers.InputHandler;
import client.handlers.MapHandler;
import client.handlers.Sound;
import client.objects.Platform;
import client.objects.WorldObject;

public abstract class LootableWorldObject extends WorldObject {

	private boolean hovered = false;
	private Image imageHighlight;
	private boolean falling = false;
	private int fallingSpeed = 1;
	private int numItems;

	URL pickupSound = null;

	// Gör så att inte massa ljud från lootableworldobjects spelar samtidigt när man springer på massa saker på marken
	static long lastSoundStamp = 0;
	static long soundDelay = 50;

	public LootableWorldObject(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType) {
		super(x, y, width, height, paralax, imagePath, objectId, versionType);
	}

	public abstract void uniqueUpdate();

	@Override
	public void update() {
		uniqueUpdate();
		
		Rectangle lootableRect = getCollisionBox();
		Rectangle mouseRect = new Rectangle(InputHandler.getWorldMouseX(), InputHandler.getWorldMouseY(), 1, 1);

		// om man hovrar på den
		if (lootableRect.intersects(mouseRect)) {
			hovered = true;
		} else {
			hovered = false;
		}

		Rectangle playerRect = Main.clientPlayer.getCollisionBox();

		if (lootableRect.intersects(playerRect)) {
			if (Main.clientPlayer.isAlive()) {
				pickUpItem();
				MapHandler.sendRemoveObject(this);
				MapHandler.removeObject(this);
			}
		}

		falling = true;

		// kollar om den kollidar men något platform
		for (int i = 0; i < MapHandler.worldObjects.size(); i++) {
			WorldObject obj = MapHandler.worldObjects.get(i);

			if (obj instanceof Platform) {
				Rectangle rectObj = obj.getCollisionBox();
				if (rectObj.intersects(lootableRect)) {
					falling = false;
				}
			}

		}

		// när objektet faller
		if (falling) {
			int y = getY() + fallingSpeed;
			setY(y);
			setCollisionBox(createCollisionBox()); // uppdaterar colllision box
		}

	}

	public void playPickupSound() {

		long timeNow = System.currentTimeMillis();
		long timeSinceLast = timeNow - lastSoundStamp; // hur länge sedan det senaste ljudet spelades från ett lootable world object

		// kollar så att det gått en viss tid innan nästa ljud spelas upp så inte massor av ljud spelas samtidigt när man lootar många saker på marken
		if (timeSinceLast > soundDelay) {
			if (pickupSound != null) {
				Sound.play(pickupSound, getX(), getY(), 1f);

				lastSoundStamp = System.currentTimeMillis(); // sparar vad tiden är nu i en genensam variabel för alla lootableworldobjekts
			}
		}
	}

	@Override
	public Image getImage() {
		Image img = super.getImage();

		if (isHovered()) {
			img = imageHighlight;
		}

		return img;
	}

	public boolean isHovered() {
		return hovered;
	}

	public abstract void pickUpItem();

	@Override
	public Rectangle createCollisionBox() {
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
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
			bGr.setColor(Color.white);
			Screen.setScreenAlpha(bGr, 0.3f);
		}

		this.imageHighlight = bimage;

	}

	public void setPickupSound(URL url) {
		this.pickupSound = url;
	}

	public int getNumItems() {
		return numItems;
	}

	public void setNumItems(int numItems) {
		this.numItems = numItems;
	}

}
