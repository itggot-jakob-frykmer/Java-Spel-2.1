package client.players.spells;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.net.URL;
import java.util.ArrayList;

import client.Client;
import client.Main;
import client.Screen;
import client.handlers.Images;
import client.handlers.Sound;
import client.players.Player;

public abstract class Spell {

	// IDs för alla spells
	public static int fireBallId = 0;
	public static int knifeThrowId = 1;
	public static int fireShieldId = 4;
	public static int fanOfKnivesId = 3;
	public static int azureShieldId = 5;
	public static int circleId = 2;

	// lista över vilka spells ID som är castSpells
	public static ArrayList<Integer> castSpells = new ArrayList<Integer>() {
		{
			add(fireBallId);
			add(knifeThrowId);
		}
	};

	// lista över vilka spells ID som är auraSpells
	public static ArrayList<Integer> auraSpells = new ArrayList<Integer>() {
		{
			add(circleId);
			add(azureShieldId);
		}
	};

	private double x;
	private double y;
	private int width;
	private int height;

	private int type;
	private int id;

	private int distMoved = 0;
	private int maxXMoved = 3000;
	private int damage;

	private int startX = 0;
	private int startY = 0;

	private int timeAlive = 0;

	private double rotationSpeed = 0;
	private double currentAngle = 0;

	private double movingDirection = 0;

	private String spellName = "";

	private ArrayList<Image> images = new ArrayList<Image>();
	private int currentImageIndex = 0;
	private int imageChangeDelay = 1;
	private int numImages = 1;

	private int tick = 0;

	private Player caster; // spelaren som kastade spellen

	public Spell(int startX, int startY, int type, int id, double direction, Player caster) {
		this.x = startX;
		this.y = startY;
		this.startX = startX;
		this.startY = startY;
		this.type = type;
		this.id = id;
		this.caster = caster;
		this.movingDirection = direction;

		initSpellFromType(type);

		playStartSound();
		initImages(numImages);

	}

	public void initImages(int numImages) {
		// läser in alla bilder som hör till denna spell
		for (int i = 0; i < numImages; i++) {
			Image img = Images.readImageFromPath("spells/spell" + type + "_" + i + ".png");
			images.add(img);
		}
	}

	public void playStartSound() {
		// ljuder ska spelas upp när man kastar en spell
		URL soundURL = Sound.readSoundFile("sounds/abilities/ability" + type + ".wav");

		if (soundURL != null) {
			Sound.play(soundURL, getX(), getY(), 1f);
		}
	}

	public abstract void initSpellFromType(int type);

	public abstract void uniqueUpdate();

	public void update() {
		tick++;
		uniqueUpdate();

		currentAngle += rotationSpeed;

		timeAlive += Screen.sleep;

		if (tick % imageChangeDelay == 0) {

			currentImageIndex++;

			// loopar igenom alla bilder
			if (currentImageIndex == numImages) {
				currentImageIndex = 0;
			}

		}

		if (Main.clientPlayer.isAlive()) {
			// kollar om man kolliderar med spellen
			if (getCollisionBox().intersects(Main.clientPlayer.getCollisionBox())) {
				causeEffect();
			}

		}

	}

	public abstract void causeEffect();

	public void paint(Graphics2D g2d) {

		Graphics2D g2dCopy = (Graphics2D) g2d.create();

		int x = Screen.fixX(getX(), 1);
		int y = Screen.fixY(getY(), 1);

		g2dCopy.rotate(currentAngle + getMovingDirection(), x, y);
		g2dCopy.drawImage(images.get(currentImageIndex), x - width / 2, y - height / 2, width, height, null);
	}

	public void sendRemoveSpell() {
		Client.sendData("#SENDREMOVESPELL", id + "@" + caster.getPlayerNumber());
	}

	public Rectangle getCollisionBox() {
		return new Rectangle(getX() - width / 2, getY() - height / 2, width, height);
	}

	public void removeSpell() {
		caster.activeSpells.remove(this);
	}

	public int getStartY() {
		return startY;
	}

	public int getStartX() {
		return startX;
	}

	public int getX() {
		return (int) x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return (int) y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setRotationSpeed(double speed) {
		this.rotationSpeed = speed;
	}

	public void setSpellName(String name) {
		this.spellName = name;
	}

	public void move(double xSpeed, double ySpeed) {
		x += xSpeed;
		distMoved += xSpeed;
		y += ySpeed;
		distMoved += ySpeed;
	}

	public void moveAngle(int speed, double angle) {

		double xSpeed = speed * Math.cos(angle);
		double ySpeed = speed * Math.sin(angle);

		move(xSpeed, ySpeed);

	}

	public double getMovingDirection() {
		return movingDirection;
	}

	public int getDamage() {
		return damage;
	}

	public void setNumImages(int num) {
		this.numImages = num;
	}

	public Player getCaster() {
		return caster;
	}

	public int getDistMoved() {
		return distMoved;
	}

	public int getTimeAlive() {
		return timeAlive;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
}
