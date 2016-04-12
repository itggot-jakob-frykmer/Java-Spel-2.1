package client.objects.interactableWorldObjects;

import java.awt.Image;
import java.util.Random;

import client.Client;
import client.Main;
import client.Screen;
import client.handlers.MapHandler;
import client.handlers.Sound;

public class PotionShrine extends InteractableWorldObject {

	private int cost = 3;

	// variabler för att spela upp effect ibland
	private int effectInterval = 1000;
	private int timeCounter = 0;
	private int chanceForEffect = 10; // hur hög procent chans det är att effekten spelas vid ett intervall

	public PotionShrine(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType) {
		super(x, y, width, height, paralax, imagePath, objectId, versionType);

		interactionTime = 500;

		setInfoText("Hold 'E' to swap " + cost + " minerals for a potion");

		
		setCompleteSound(Sound.readSoundFile("sounds/objects/PotionHandle.wav"));
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

	// skapar en bubbel effekt över objektet. SendToOther bestämmer om denna klienten ansvarar för att skicka informationen om bubblorna till alla andra klienter
	public void causeEffect(int num, boolean sendToOther) {

		int delay = 700;
		int bubbleWidth = 10;
		int bubbleHeight = 10;
		int startX = getX() + getWidth() / 2 - bubbleWidth / 2;
		int startY = getY() - bubbleHeight;

		MapHandler.causeBubbleEffect(startX, startY, bubbleWidth, bubbleHeight, num, getVersionType(), delay);

		if (sendToOther) {
			Client.sendData("#SENDBUBBLESEFFECT", startX + "@" + startY + "@" + bubbleWidth + "@" + bubbleHeight + "@" + num + "@" + getVersionType() + "@" + delay);
		}

	}

	@Override
	public void uniqueUpdate() {
		timeCounter += Screen.sleep;

		// gör så att det kommer en bubbel-effekt ibland
		if (timeCounter >= effectInterval) {
			// random mellan 1 och 100
			Random ra = new Random();
			int randInt = ra.nextInt(100);

			// gör så att effekten inte spelas upp varje intervall genom slump. För att öka variationen
			if (randInt <= chanceForEffect) {
				causeEffect(1, false);
			}

			timeCounter = 0; // återställer timern
		}
	}

	@Override
	public void uniqueCompleteInteraction() {
		// kollar vilket typ av torn detta är och ger den potionen som man ska få från detta torn
		if (getVersionType() == 0) {
			Main.clientPlayer.pickUpHealthPotion(1);
		} else if (getVersionType() == 1) {
			Main.clientPlayer.pickUpEnergyPotion(1);
		}

		Main.clientPlayer.pickUpMinerals(-cost); // tar bort så mycket det kostade att göra detta

		causeEffect(3, true); // orskar en bubbel-effekt och skickar till alla andra klienter

	}

	@Override
	public void uniqueOnStartInteraction() {
		// det kostar wood att interacta med detta objekt
		if (Main.clientPlayer.getNumMinerals() < cost) {
			Main.clientPlayer.stopInteraction();
		}
	}

}
