package client.players.spells;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import client.Main;
import client.Screen;
import client.handlers.MapHandler;
import client.objects.Platform;
import client.players.Player;

public class AuraSpell extends Spell {

	private int lifeTime;

	// positiva effekter av att stå i sin egen aura
	private int energyReg;
	private int healthReg;

	private int effectInterval; // hur ofta man får effecten när man står auran
	private int effectCounter = 0;

	public AuraSpell(int startX, int startY, int type, int id, int direction, Player caster) {
		super(startX, startY, type, id, direction, caster);
	}

	@Override
	public void initSpellFromType(int type) {
		if (type == 2) {
			setSpellName("circle");

			double scale = 0.8;
			setWidth((int) (300 * scale));
			setHeight((int) (175 * scale));

			setDamage(10);

			lifeTime = 10000;

			healthReg = 1;
			effectInterval = 100;

		} else if (type == 5) {
			setSpellName("AzureBarrier");

			double scale = 0.8;
			setWidth((int) (542 * scale));
			setHeight((int) (455 * scale));

			setDamage(10);

			lifeTime = 10000;

			energyReg = 5;
			effectInterval = 100;
		}

		setY(getCaster().getY() - getHeight() / 2); // sätter så att nedre delen av auran är vid castarens huvud

		placeToClosestGround(0);
	}

	// sätter auran till närmsta marken under där den placerades ut
	public void placeToClosestGround(int tries) {
		int currentY = getY();
		Rectangle bottomBox = getBottomBox();

		boolean collides = false;

		ArrayList<Platform> platforms = MapHandler.getAllPlatforms();

		for (int i = 0; i < platforms.size(); i++) {
			Platform platform = platforms.get(i);
			// System.out.println("kör");
			if (bottomBox.intersects(platform.getCollisionBox())) {
				collides = true;
			}
		}

		// om den fortfarande inte kollidar med en platform
		if (!collides) {
			setY(currentY += 5); // sätter ner den lite
			// gör så att den inte försöker för alltid om det inte går
			if (tries < 100) {
				placeToClosestGround(tries + 1);
			}

		}

	}

	// hämtar "fötterna" på auran. Delen längst ner som ska stå mot marken
	public Rectangle getBottomBox() {
		int boxHeight = 10;

		int x = getX() - getWidth() / 2;
		int y = getY() + getHeight() / 2 - boxHeight;
		int width = getWidth();

		return new Rectangle(x, y, width, boxHeight);
	}

	@Override
	public void paint(Graphics2D g2d) {
		super.paint(g2d);

		g2d.fill(getBottomBox());

	}

	@Override
	public void uniqueUpdate() {

		if (getTimeAlive() >= lifeTime) {
			removeSpell();
		}

	}

	@Override
	public void causeEffect() {
		effectCounter += Screen.sleep;

		if (effectCounter >= effectInterval) {
			effectCounter = 0;
			// om det är din egen aura
			if (getCaster() == Main.clientPlayer) {
				causePositiveEffect();
			} else {
				causeNegativeEffect();
			}
		}

	}

	public void causeNegativeEffect() {
		Main.clientPlayer.takeDamage(getDamage());
	}

	// positiva effekterna av att stå i spelln. Ges om det är du som har lagt ut den
	public void causePositiveEffect() {
		Main.clientPlayer.addEnergy(energyReg);
		Main.clientPlayer.heal(healthReg);
	}

}
