package client.players.spells;

import client.Main;
import client.players.Player;

public class CastSpell extends Spell {

	private int speed;

	private int maxMoved = 3000;

	public CastSpell(int startX, int startY, int type, int id, double direction, Player caster) {
		super(startX, startY, type, id, direction, caster);

	}

	public void initSpellFromType(int type) {

		int offsetX = 0;
		int offsetY = 0;
		Player caster = getCaster();

		if (type == 0) {
			setSpellName("fire");
			setNumImages(3);

			double scale = 0.8;
			setWidth((int) (100 * scale));
			setHeight((int) (35 * scale));

			speed = 5;
			setDamage(20);
			offsetY = caster.getHeight() / 2;
		} else if (type == 1 || type == 3) {
			setSpellName("knifeThrow");

			double scale = 0.6;
			setWidth((int) (32 * scale));
			setHeight((int) (92 * scale));

			speed = 5;
			setDamage(20);
			setRotationSpeed(0.1);
			offsetY = caster.getHeight() / 2;
		}

		// om spelaren inte är vänd åt vänster när hen kastar spellen ska spellen komma från hens högra sida
		if (!caster.isFacingLeft()) {
			offsetX = caster.getWidth();
		}

		move(offsetX, offsetY); // placerar spellen rätt

	}

	@Override
	public void uniqueUpdate() {
		moveAngle(speed, getMovingDirection());

		if (Math.abs(getDistMoved()) >= maxMoved) {
			removeSpell();
		}

	}

	@Override
	public void causeEffect() {
		// om det inte är din egen spell
		if (getCaster() != Main.clientPlayer) {
			sendRemoveSpell();
			removeSpell();
			Main.clientPlayer.takeDamage(getDamage());
		}
	}

}
