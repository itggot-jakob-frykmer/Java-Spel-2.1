package client.objects;

import java.awt.Image;
import java.net.URL;

import client.Main;
import client.handlers.Images;
import client.handlers.Sound;

public class Potion extends LootableWorldObject {

	public Potion(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType) {
		super(x, y, width, height, paralax, imagePath, objectId, versionType);

		// sätter en highlight image
		Image img = Images.readImageFromPath("potions/potionHighlight.png");
		setImageHighlight(img);

		setNumItems(1);

		URL pickupSound = Sound.readSoundFile("sounds/objects/PotionHandle.wav");
		setPickupSound(pickupSound);
	}

	@Override
	public void pickUpItem() {

		// beroende på vilken typ av potion det är plockas olika upp
		if (getVersionType() == 0) {
			Main.clientPlayer.pickUpHealthPotion(getNumItems());

		} else if (getVersionType() == 1) {
			Main.clientPlayer.pickUpEnergyPotion(getNumItems());
		}
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
