package client.objects;

import client.Main;

public class Ladder extends EffectingWorldObject {



	public Ladder(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType) {
		super(x, y, width, height, paralax, imagePath, objectId, versionType);
	}
	

	@Override
	public void onRemove() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void giveEffect() {
		 Main.clientPlayer.setCanFly(true);
	}


	@Override
	public void removeEffect() {
		Main.clientPlayer.setCanFly(false);
	}

}
