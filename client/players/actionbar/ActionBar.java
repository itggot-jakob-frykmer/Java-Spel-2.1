package client.players.actionbar;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;

import client.Screen;
import client.handlers.Images;
import client.players.YourPlayer;

// första: 0,07678
// andra: 0,24713
// ability size 0,10767
// yoffset: ability: 0,4429
// articact power size: 0,1571
// artifact power yOffset: 0,0588

public class ActionBar {

	private Image actionBarImage;
	private int width;
	private int height;
	private int x;
	private int y;

	static ArrayList<DisplayAbility> abilities = new ArrayList<>();
	static ArrayList<Icon> icons = new ArrayList<>();

	public static Image abilityHighlight;
	public static Image abilityShadow;

	public static Image powerHighlight;
	public static Image powerShadow;

	private int healthPotionIndex;
	private int energyPotionIndex;

	public ActionBar() {
		actionBarImage = Images.readImageFromPath("ui/actionbar.png");

		// räknar ut hur stor actionbaren ska vara och var den ska sitta
		width = (int) (Screen.screenWidth * 0.3);
		height = Images.getPropHeightFromImage(width, actionBarImage);
		y = Screen.screenHeight- height;
		x = Screen.screenWidth / 2 - width / 2;
	}

	public void initAbilities(int team, YourPlayer player) {

		int abilityOneId = 0;
		int abilityTwoId = 0;
		int powerId = 0;

		// gör så att de olika lagen får olika abilities
		if (team == 1) {
			abilityOneId = 0;
			abilityTwoId = 2;
			powerId = 4;
		} else if (team == 0) {
			abilityOneId = 1;
			abilityTwoId = 3;
			powerId = 5;
		}

		int healthPotionAbility = 6;
		int energyPotionAbility = 7;

		abilityHighlight = Images.readImageFromPath("ui/ability_highlight.png");
		abilityShadow = Images.readImageFromPath("ui/ability_shadow.png");

		powerHighlight = Images.readImageFromPath("ui/power_highlight.png");
		powerShadow = Images.readImageFromPath("ui/power_shadow.png");

		int abilitySize = (int) (width * 0.10767);
		int powerSize = (int) (width * 0.1671);

		// räknar ut var första ability ska sitta
		int xOffset = (int) (x + width * 0.07678);
		int yOffset = (int) (y + height * 0.4429);

		Method method = null; // vilken metod en ability ska köra
		Class[] paramtersTypes = new Class[1]; // Säger vilken datatyp parametern på en method som en ability kör ska vara. Behövs endast om en method tar en parameter

		try {

			// lägger till första ability
			paramtersTypes[0] = int.class;
			method = YourPlayer.class.getMethod("castSpell", paramtersTypes); // metoden som ska köras
			DisplayAbility ability = new DisplayAbility(abilityOneId, xOffset, yOffset, abilitySize, false, false, KeyEvent.VK_1, method, player, abilityOneId, 150, 5, 0);
			abilities.add(ability);

			// lägger till andra ability
			xOffset = (int) (x + width * 0.24713); // räknar ut var andra ability ska sitta
			paramtersTypes[0] = int.class;
			method = YourPlayer.class.getMethod("castSpell", paramtersTypes);// metoden som ska köras
			ability = new DisplayAbility(abilityTwoId, xOffset, yOffset, abilitySize, false, false, KeyEvent.VK_2, method, player, abilityTwoId, 2000, 40, 1);
			abilities.add(ability);

			// lägger till potion ability
			xOffset = (int) (x + width - (width * 0.24713) - abilitySize); // räknar ut var tredje ability ska sitta
			method = YourPlayer.class.getMethod("useHealthPotion"); // metoden som ska köras
			ability = new DisplayAbility(healthPotionAbility, xOffset, yOffset, abilitySize, false, true, KeyEvent.VK_4, method, player, null, 1500, 0, 0);
			abilities.add(ability);

			healthPotionIndex = abilities.indexOf(ability);

			// räknar ut var potion ability ska sitta
			xOffset = (int) (x + width - (width * 0.07678) - abilitySize);
			method = YourPlayer.class.getMethod("useEnergyPotion"); // metoden som ska köras
			ability = new DisplayAbility(energyPotionAbility, xOffset, yOffset, abilitySize, false, true, KeyEvent.VK_5, method, player, null, 1500, 0, 0);
			abilities.add(ability);

			energyPotionIndex = abilities.indexOf(ability);

			// räknar ut var powerability ska sitta
			xOffset = (int) (x + width / 2 - powerSize / 2);
			yOffset = (int) (y + height * 0.0588);
			paramtersTypes[0] = int.class;
			method = YourPlayer.class.getMethod("castSpell", paramtersTypes); // metoden som ska köras
			ability = new DisplayAbility(powerId, xOffset, yOffset, powerSize, true, false, KeyEvent.VK_3, method, player, powerId, 5000, 20, 2);
			abilities.add(ability);

			int iconWidth = (int) (width * 0.09);
			int iconHeight = iconWidth;

			int iconY = y - 15;

			// cooldown iconen för double jump
			Icon icon = new ReadyIcon(x + width - iconWidth * 2, iconY, iconWidth, iconHeight, player.getDoubleJumpReady(), player.getDoubleJumpCooldown(), player.getDoubleJumpCooldownCounter(), "ui/icons/skill_1_ready.png", "ui/icons/skill_1_notReady.png", player.getDoubleJumpUnlocked());
			icons.add(icon);

			// cooldown iconen för dash
			icon = new ReadyIcon(x + width - iconWidth * 4, iconY, iconWidth, iconHeight, player.getDashReady(), player.getDashCooldown(), player.getDashCooldownCounter(), "ui/icons/skill_0_ready.png", "ui/icons/skill_0_notReady.png", player.getDashUnlocked());
			icons.add(icon);

			// ikonen för att räkna hur mycket wood man har
			icon = new CounterIcon(x + 20, iconY, iconWidth, iconHeight, "ui/icons/wood.png", player.getWoodCounter());
			icons.add(icon);

			// ikonen för att räkna hur mycket wood man har
			icon = new CounterIcon(x + 30 + iconWidth * 2, iconY, iconWidth, iconHeight, "ui/icons/mineral.png", player.getMineralCounter());
			icons.add(icon);

		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DisplayAbility getAbility(int index) {
		return abilities.get(index);
	}

	public DisplayAbility getHealthPotionAbility() {
		return abilities.get(healthPotionIndex);
	}

	public DisplayAbility getEnergyPotionAbility() {
		return abilities.get(energyPotionIndex);
	}

	public void update() {
		for (int i = 0; i < abilities.size(); i++) {
			abilities.get(i).update();
		}
	}

	public void paint(Graphics2D g2d) {

		Graphics2D g2 = Screen.reverseZoom(g2d); // reversar zoomen

		// målar alla ability ikoner
		for (int i = 0; i < abilities.size(); i++) {
			abilities.get(i).paint(g2d);
		}

		// målar alla ikoner
		for (int i = 0; i < icons.size(); i++) {
			Icon icon = icons.get(i);

			icon.paint(g2);

		}

		g2.drawImage(actionBarImage, x, y, width, height, null);
	}

	public Image getActionBarImage() {
		return actionBarImage;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	// Kollar om en knapp som trycktes ner ska köra någon ability
	public void handleKeyEvent(KeyEvent e) {
		int keyCode = e.getKeyCode();
		for (int i = 0; i < abilities.size(); i++) {
			DisplayAbility ability = abilities.get(i);

			if (ability.getKeyBind() == keyCode) {
				ability.use();
			}
		}
	}

	public void handleMouseClicked(MouseEvent e) {

		boolean abilityUsed = false;

		for (int i = 0; i < abilities.size(); i++) {
			DisplayAbility ability = abilities.get(i);

			if (ability.getHovered()) {
				ability.use();
				abilityUsed = true;
			}

		}

		// om ingen annan ability klickades ner betyder det att man klickade någonstans på skärmen och då ska man skjuta sin första ability
		if (!abilityUsed) {
			abilities.get(0).use();
		}

	}

	public void handleMouseMoved(MouseEvent e) {
		Rectangle mouseRect = new Rectangle(e.getX(), e.getY(), 1, 1);

		for (int i = 0; i < abilities.size(); i++) {
			DisplayAbility ability = abilities.get(i);

			Rectangle abilityRect = new Rectangle(ability.getX(), ability.getY(), ability.getSize(), ability.getSize());
			// om musen intersectar någon ikon ska den få en hover-effekt
			if (mouseRect.intersects(abilityRect)) {
				ability.setHovered(true);
			} else {
				ability.setHovered(false);
			}
		}
	}

}
