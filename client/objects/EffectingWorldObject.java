package client.objects;

import client.Main;

public abstract class EffectingWorldObject extends WorldObject {

	public EffectingWorldObject(int x, int y, int width, int height, double paralax, String imagePath, int objectId, int versionType) {
		super(x, y, width, height, paralax, imagePath, objectId, versionType);
	}

	public abstract void giveEffect();

	public abstract void removeEffect();

	private boolean effectActive = false; // gör så att effekten inte tas bort hela tiden utan bara när den redan finns

	@Override
	public void update() {
		if (getCollisionBox().intersects(Main.clientPlayer.getCollisionBox())) {
			giveEffect();
			effectActive = true;
		} else {
			if (effectActive) {
				removeEffect();
				effectActive = false;
			}
		}
	}

	@Override
	public void onRemove() {

	}

}
