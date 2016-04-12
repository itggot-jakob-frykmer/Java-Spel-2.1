package client.objects;

import java.awt.Image;

import client.Main;
import client.handlers.MapHandler;

// ett ställe där man kan låsa upp abilites
public class UnlockSkill extends InteractableWorldObject {

	public UnlockSkill(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType) {
		super(x, y, width, height, paralax, imagePath, objectId, versionType);

		interactionTime = 500;

		setInfoText("Hold 'E' to unlock this ability");

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

		// beroende på vilken typ av ställe detta är så ska olika skills låsas upp
		if (getVersionType() == 0) {
			Main.clientPlayer.getDashUnlocked().set(true);
		} else if (getVersionType() == 1) {
			Main.clientPlayer.getDoubleJumpUnlocked().set(true);
		}
		
		MapHandler.removeObject(this);
	}

	@Override
	public void uniqueOnStartInteraction() {

	}

}
