package client.objects;

import java.awt.Image;

import client.Main;
import client.handlers.Sound;

// Sawmill är en byggnad som man kan gå till för att byta wood till mineraler
public class Sawmill extends InteractableWorldObject {

	private int cost = 2;
	private int gain = 1; // vad man får

	public Sawmill(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType) {
		super(x, y, width, height, paralax, imagePath, objectId, versionType);

		interactionTime = 500;

		setCompleteSound(Sound.readSoundFile("sounds/objects/WoodHandle.wav"));

		setInfoText("Hold 'E' to swap " + cost + " wood for " + gain + " mineral");

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

	}

	@Override
	public void uniqueCompleteInteraction() {

		// ger mineraler och tar bort wood
		Main.clientPlayer.pickUpMinerals(gain);
		Main.clientPlayer.pickUpWood(-cost);

	}

	@Override
	public void uniqueOnStartInteraction() {
		// det kostar wood att interacta med detta objekt
		if (Main.clientPlayer.getNumWood() < cost) {
			Main.clientPlayer.stopInteraction();
		}
	}

}
