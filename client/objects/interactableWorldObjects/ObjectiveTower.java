package client.objects.interactableWorldObjects;

import java.awt.Image;

import client.Main;
import client.handlers.Images;

public class ObjectiveTower extends InteractableWorldObject {

	ObjectiveTowerSpawn spawn;

	public ObjectiveTower(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType, ObjectiveTowerSpawn spawn) {
		super(x, y, width, height, paralax, imagePath, objectId, versionType);

		this.spawn = spawn;

		interactionTime = 1000;

		setImageHighlight(Images.readImageFromPath("towers/objectiveTowerHighlight.png"));
		
		setInfoText("Hold 'E' to capture this tower");
		
	}

	@Override
	public void onRemove() {

	}

	@Override
	public Image getHighlightImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clickObject() {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniqueUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void uniqueCompleteInteraction() {
		spawn.changeOwner(Main.clientPlayer.getTeam());
		spawn.sendOwner();
	}

	@Override
	public void uniqueOnStartInteraction() {
		if (spawn.getOwner() == Main.clientPlayer.getTeam()) {
			Main.clientPlayer.stopInteraction();
		}
	}

}
