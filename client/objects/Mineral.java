package client.objects;

import java.awt.Image;
import java.net.URL;

import client.Main;
import client.handlers.Images;
import client.handlers.Sound;

public class Mineral extends LootableWorldObject {

	public static int mineralPerStack = 1;

	public Mineral(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType) {
		super(x, y, width, height, paralax, imagePath, objectId, versionType);

		Image img = Images.readImageFromPath("objects/mineralHighlight.png");
		setImageHighlight(img);

		setNumItems(mineralPerStack);

		URL pickupSound = Sound.readSoundFile("sounds/objects/WoodHandle.wav");
		setPickupSound(pickupSound);

	}

	@Override
	public void pickUpItem() {
		Main.clientPlayer.pickUpMinerals(getNumItems());
	}

	@Override
	public void onRemove() {
		playPickupSound();
	}

	@Override
	public void uniqueUpdate() {
		// TODO Auto-generated method stub
		
	}

}
