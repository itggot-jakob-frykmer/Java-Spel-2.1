package client.players;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.net.URL;
import java.util.ArrayList;

import client.Main;
import client.effects.Cloud;
import client.effects.Effect;
import client.handlers.Images;
import client.handlers.Sound;
import client.players.spells.CastSpell;
import client.players.spells.FireShield;
import client.players.spells.Spell;

public abstract class Player {

	private int x;
	private int y;

	public static double sizeScale = 0.17;
	private final int width = (int) (411 * sizeScale);
	private final int height = (int) (588 * sizeScale);

	private int playerNumber;
	private int characterType;

	private boolean movingLeft = false;
	private boolean movingRight = false;
	private boolean movingUp = false;
	private boolean movingDown = false;

	private boolean facingLeft = true;

	private boolean falling = true;
	private boolean jumping = false;
	private boolean moving = false;

	private Image currentImage;
	private int imageChangeDelay = 1;
	private int imageChangeTick = 0;
	int currentImageIndex = 0;

	private URL jumpSound;

	private Team team;

	private Weapon weapon;

	private FireShield fireShield;

	/*
	 * private int changedRunningImageTick = 0; // ökar varje gång man byter bild private int runningSoundDelayTick = 8;
	 */
	boolean dashing = false;

	private int imageChangeStandingDelay = 70;

	private HealthBar healthBar;

	private ArrayList<Image> movingImages = new ArrayList<Image>();
	private ArrayList<Image> standingImages = new ArrayList<Image>();
	private Image fallingImage;

	private URL runningSound;

	public ArrayList<Spell> activeSpells = new ArrayList<Spell>();

	public Player(int playerNumber, int characterType) {
		this.playerNumber = playerNumber;
		this.characterType = characterType;
		initImages(characterType);
		initSounds();

		fireShield = new FireShield(0, this);
		healthBar = new HealthBar(this);
		

		jumpSound = Sound.readSoundFile("sounds/players/Jump.wav");
	}

	public void initSounds() {
		runningSound = Sound.readSoundFile("sounds/players/FootstepGrass.wav");
	}

	public void initImages(int characterType) {
		movingImages.clear();
		standingImages.clear();

		for (int i = 0; i < 14; i++) {
			Image img = Images.readImageFromPath("characters/" + characterType + "/moving/" + i + ".png");
			movingImages.add(img);
		}

		for (int i = 0; i < 2; i++) {
			Image img = Images.readImageFromPath("characters/" + characterType + "/standing/" + i + ".png");
			standingImages.add(img);
		}

		fallingImage = movingImages.get(13);
		currentImage = standingImages.get(0);
	}

	public void changeCharacter(int characterType) {
		this.characterType = characterType;
		initImages(characterType);
	}

	public void playJumpSound() {
		Sound.play(jumpSound, getX(), getY(), 1f);
	}

	// funktion för att uppdatera för både sin egen och andra spelare
	public void commonUpdate() {
		fireShield.update();
		updateCurrentImage();
		updateSpells();
	}

	public void updateSpells() {
		for (int i = 0; i < activeSpells.size(); i++) {
			activeSpells.get(i).update();
		}
	}

	// uppdaterar vilken bild denna spelaren ska ha just nu
	public void updateCurrentImage() {

		imageChangeTick++;

		if (isDashing()) {
			currentImage = fallingImage;
		} else if (isFalling()) {
			currentImage = fallingImage;
		} else {

			// Byter bild för när man rör sig
			if (isMoving()) {

				// imageChangeDelay = 100;
				// gör så spring-bilden bara byts ibland
				if (imageChangeTick % imageChangeDelay == 0) {

					// gör så att bild indexet loopar runt hela arrayen
					currentImageIndex++;
					if (currentImageIndex == movingImages.size()) {
						currentImageIndex = 0;
					}

					currentImage = movingImages.get(currentImageIndex); // byter bild

					// Gör så att spring-ljudet bara spelas upp vissa gånger bilden byts
					if (currentImageIndex == 1 || currentImageIndex == 8) {
						Sound.play(runningSound, getX(), getY(), 0.7f);
					}

				}

			} else { // byter bild för när man står stilla

				if (imageChangeTick % imageChangeStandingDelay == 0) {

					// gör så att bild indexet loopar runt hela arrayen
					currentImageIndex++;
					if (currentImageIndex >= standingImages.size()) {
						currentImageIndex = 0;
					}

					if (standingImages.size() > 0) {
						currentImage = standingImages.get(currentImageIndex);
					}

				}
			}
		}

		if (weapon != null) {
			weapon.updatePosition();
		}

	}

	public void setTeam(Team team) {
		this.team = team;
		changeCharacter(team.getTeamNumber());
	}

	public Team getTeam() {
		return team;
	}

	public ArrayList<Image> getMovingImages() {
		return movingImages;
	}

	public void setImageChangeDelay(int imageChangeDelay) {
		this.imageChangeDelay = imageChangeDelay;
	}

	public int getImageChangeDelay() {
		return imageChangeDelay;
	}

	public boolean isMovingLeft() {
		return movingLeft;
	}

	public void setMovingLeft(boolean movingLeft) {
		this.movingLeft = movingLeft;
	}

	public boolean isMovingRight() {
		return movingRight;
	}

	public void setMovingRight(boolean movingRight) {
		this.movingRight = movingRight;
	}

	public boolean isMovingUp() {
		return movingUp;
	}

	public void setMovingUp(boolean movingUp) {
		this.movingUp = movingUp;
	}

	public boolean isMovingDown() {
		return movingDown;
	}

	public void setMovingDown(boolean movingDown) {
		this.movingDown = movingDown;
	}

	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	}

	public abstract void update();

	public abstract void paintCharacter(Graphics2D g2d);

	public void paint(Graphics2D g2d) {

		paintSpells(g2d);

		if (isAlive()) {

			// om man har ett vapen målas det ut. Och om man kollar åt vänster för då ska vapnet målas först så det hamnar bakom gubben
			if (isFacingLeft() && weapon != null) {
				weapon.paint(g2d);
			}

			getHealthBar().paint(g2d);
			paintCharacter(g2d);
			fireShield.paint(g2d);

			// om man har ett vapen målas det ut
			if (!isFacingLeft() && weapon != null) {
				weapon.paint(g2d);
			}
		}
	}

	// målar alla spells som tillhör denna spelare
	public void paintSpells(Graphics2D g2d) {
		for (int i = 0; i < activeSpells.size(); i++) {
			activeSpells.get(i).paint(g2d);
		}
	}

	public void removeSpellById(int id) {
		// loopar igenom alla spells och tar bort den spellen som har ID:t
		for (int i = 0; i < activeSpells.size(); i++) {
			Spell spell = activeSpells.get(i);

			if (spell.getId() == id) {
				activeSpells.remove(spell);
			}
		}
	}

	public void setWeapon(int versionType, int level) {
		Weapon weapon = new Weapon(versionType, level, this);
		setWeapon(weapon);
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public boolean isAlive() {
		return (getHealth() > 0);
	}

	public int getPlayerNumber() {
		return playerNumber;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Image getImage() {
		return currentImage;
	}

	public Rectangle getFeetCollisionBox() {
		int height = (int) (this.height * 0.09);
		int width = (int) ((this.width) * 0.3);

		int x = this.x + this.width / 2 - width / 2; // sätter den i mitten
		int y = this.y - height + this.height;

		return new Rectangle(x, y, width, height);
	}

	public Rectangle getCollisionBox() {
		return new Rectangle(x, y, width, height);
	}

	public boolean isFalling() {
		return falling;
	}

	public void setFalling(boolean falling) {
		this.falling = falling;
	}

	public boolean isJumping() {
		return jumping;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	public boolean isMoving() {
		return moving;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public boolean isDashing() {
		return dashing;
	}

	public boolean isFacingLeft() {
		return facingLeft;
	}

	public void setFacingLeft(boolean facingLeft) {
		this.facingLeft = facingLeft;
	}

	public HealthBar getHealthBar() {
		return healthBar;
	}

	public int getEnergy() {
		return healthBar.getEnergy();
	}

	public void setEnergy(int energy) {
		healthBar.setEnergy(energy);
	}

	public int getHealth() {
		return healthBar.getHealth();
	}

	public void setHealth(int health) {
		this.healthBar.setHealth(health);
	}

	public void setDashing(boolean dashing) {
		this.dashing = dashing;
	}

	public int getCurrentImageIndex() {
		return currentImageIndex;
	}

	public FireShield getFireShield() {
		return fireShield;
	}

	public void createDoubleJumpEffect() {

		int numClouds = 10;
		int skip = 360 / numClouds;

		int width = 30;
		int height = 30;

		int x = getX() + getWidth() / 2 - width / 2;
		int y = getY() + getHeight() - height;

		for (int i = 0; i < numClouds; i++) {
			Effect cloud = new Cloud(x, y, width, height, skip * i);
			Main.effects.add(cloud);
		}

	}

}
