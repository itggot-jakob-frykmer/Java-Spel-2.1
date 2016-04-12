package client.objects;

import java.awt.Image;
import java.net.URL;
import java.util.Random;

import client.Main;
import client.handlers.Images;
import client.handlers.MapHandler;
import client.handlers.Sound;
import client.objects.lootableWorldObjects.Mineral;

public class Chest extends InteractableWorldObject {

	// bestämmer hur mycket kistan ska innehåller
	private int minResContains = 1;
	private int resVary = 5;

	private URL openSound;

	public Chest(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType) {
		super(x, y, width, height, paralax, imagePath, objectId, versionType);

		interactionTime = 1000;

		setImageHighlight(Images.readImageFromPath("chests/chestHighlight" + versionType + ".png"));

		setOpenSound(Sound.readSoundFile("sounds/objects/chestOpen.wav"));

		setInfoText("Hold 'E' to open this chest");


	}

	@Override
	public void onRemove() {
		Sound.play(openSound, getX(), getY(), 1f);
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

	public URL getOpenSound() {
		return openSound;
	}

	public void setOpenSound(URL openSound) {
		this.openSound = openSound;
	}

	@Override
	public void uniqueCompleteInteraction() {
		Random ra = new Random();

		int numMinerals = (minResContains + ra.nextInt(resVary)) * Mineral.mineralPerStack;
		int numHealthPotions = (minResContains + ra.nextInt(resVary));
		int numEnergyPotions = (minResContains + ra.nextInt(resVary));

		Main.clientPlayer.pickUpMinerals(numMinerals);
		Main.clientPlayer.pickUpHealthPotion(numHealthPotions);
		Main.clientPlayer.pickUpEnergyPotion(numEnergyPotions);

		MapHandler.sendSpawnOpenChest(getObjectId()); // säger till servern att en ny öppen kista ska spawna

		// tar bort kistan och säger till alla andra klienter att ta bort kistan
		MapHandler.sendRemoveObject(this);
		MapHandler.removeObject(this);
	}

	@Override
	public void uniqueOnStartInteraction() {
		// TODO Auto-generated method stub

	}

}
