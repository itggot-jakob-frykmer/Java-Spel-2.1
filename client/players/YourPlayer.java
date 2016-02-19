package client.players;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import client.Client;
import client.MapHandler;
import client.Screen;
import client.objects.InteractableWorldObject;
import client.objects.Platform;
import client.objects.WorldObject;
import client.players.actionbar.DisplayAbility;

public class YourPlayer extends Player {

	// private int movingSpeed = 3;
	private double currentMovingSpeed = 0;
	private double movingAcceleration = 0.09;
	private double movingDeacceleration = 0.025;
	private double maxMovingSpeed = 3;

	private double startJumpingSpeed = 5;
	private double currentJumpingSpeed = startJumpingSpeed;
	private double minJumpingSpeed = 0;

	private double fallingAcceleration = 0.09;
	private double startFallingSpeed = 0;
	private double currentFallingSpeed = startFallingSpeed;
	private double maxFallingSpeed = 10;

	private double lengthFallen = 0;
	private double damagePerPixel = 0.02;
	private int safeFallingDistance = 1200;

	private AtomicBoolean doubleJumpReady; // denna �r av typen atomic boolean f�r att kunna skapa en referens till variablen
	private AtomicInteger doubleJumpCooldown = new AtomicInteger(5000);
	private AtomicInteger doubleJumpCooldownCounter = new AtomicInteger(0);
	private double startDoubleJumpingSpeed = 6.5;

	private int onScreenX = 550;
	private int onScreenY = 650;

	private double dashAcceleration = 0.2;
	private int startDashSpeed = 20;
	private double currentDashSpeed = 0;

	private int regHealthDelay = 1000;
	private int regHealthAmount = 1;

	private AtomicInteger dashCooldown = new AtomicInteger(20000);
	private AtomicInteger dashCooldownCounter = new AtomicInteger(0);
	private AtomicBoolean dashReady; // denna �r av typen atomic boolean f�r att kunna skapa en referens till variablen

	private boolean godMode = false;

	private boolean holdingInteract = false; // om man f�rs�ker interacta med n�got
	private InteractableWorldObject interactionObject; // det object man interactar med
	private boolean interacting = false; // n�r man h�ller p� att interacta med n�got
	private int timeInteracted = 0;

	private double cameraDelayX = 0;
	private double cameraDelayY = 0;

	private int cameraDelayMax = 20;
	private double cameraDelaySpeed = 0.2;

	private ActionBar actionBar;

	private Color interactionColor;

	private int delayCounter = 0;

	private AtomicInteger numWood = new AtomicInteger(0); // hur mycket wood man har p� sig

	public YourPlayer(int playerNumber, int characterType) throws NoSuchMethodException, SecurityException {
		super(playerNumber, characterType);

		doubleJumpReady = new AtomicBoolean(true);
		dashReady = new AtomicBoolean(true);

		actionBar = new ActionBar(0, 1, 2, this);
	}

	public void move(double xMove, double yMove) {
		int speedModifier = 1;

		if (godMode) {
			speedModifier = 5;
		}

		int oldX = getX();
		int oldY = getY();

		oldX += xMove * speedModifier;
		oldY += yMove * speedModifier;

		setX(oldX);
		setY(oldY);

		sendCoords();

	}

	public void sendAllInfo() {
		sendCoords();
		sendIsFalling();
		sendIsFacingLeft();
		sendIsMoving();
		sendIsDashing();
		sendImageChangeSpeed();
		sendHealth();
	}

	public void sendCoords() {
		String message = getX() + "&" + getY();
		// sending players coordinates
		Client.sendData("#SPC", message);
	}

	public void calculateCurrentImageChange() {
		int imageChangeDelay;
		// om man r�r sig
		if (currentMovingSpeed != 0) {
			double pixelsPerTick = Math.abs(currentMovingSpeed); // hur m�nga pixlar man r�r sig p� en tick
			double numPicsPerStep = getMovingImages().size() / 2.0; // hur m�nga bilder det tar f�r karakt�ren att g�ra ett steg
			double pixelsPerPic = (getWidth() * 2) / (numPicsPerStep); // Hur m�nga pixlar man uppskattas g�ra per bild

			imageChangeDelay = (int) Math.ceil(pixelsPerPic / pixelsPerTick); // Hur m�nga g�nger man ska vara p� samma bild f�r att man ska flytta sig s� m�nga pixlar man ska g�ra per bild
			if (imageChangeDelay > 20) {
				imageChangeDelay = 20;
			}
		} else {
			imageChangeDelay = 1;
		}

		setImageChangeDelay(imageChangeDelay);
	}

	public void updateDash() {
		if (currentDashSpeed > 0) {
			currentDashSpeed -= dashAcceleration;
		}

		if (currentDashSpeed < 0) {
			currentDashSpeed += dashAcceleration;
		}

		if (Math.abs(currentDashSpeed) < dashAcceleration) {
			currentDashSpeed = 0;
		}
	}

	public void updateCameraDelay() {

		if (isFalling()) {

			int mod = 1;

			if (isJumping()) {
				mod = -1;
			}

			if (Math.abs(cameraDelayY) < cameraDelayMax) {
				cameraDelayY += cameraDelaySpeed * mod;
			}

		}

		else {
			int mod = 1;

			if (cameraDelayY < 0) {
				mod = -1;
			}

			if (Math.abs(cameraDelayY) > 0) {
				cameraDelayY -= cameraDelaySpeed * mod;
			}

			if (Math.abs(cameraDelayY) < cameraDelaySpeed) {
				cameraDelayY = 0;
			}
		}

		// f�r horizontellt
		if (isMovingRight() || isMovingLeft() || isDashing()) {

			int speedMod = 1;

			if (isDashing()) {
				speedMod = 1;
			}

			int mod = 1;
			if (isMovingLeft()) {
				mod = -1;
			}

			if (Math.abs(cameraDelayX) < cameraDelayMax * speedMod) {
				cameraDelayX += cameraDelaySpeed * mod * speedMod;
			}

		}

		else {

			int mod = 1;

			if (cameraDelayX < 0) {
				mod = -1;
			}

			if (Math.abs(cameraDelayX) > 0) {
				cameraDelayX -= cameraDelaySpeed * mod;
			}

			if (Math.abs(cameraDelayX) < cameraDelaySpeed) {
				cameraDelayX = 0;
			}

			/*
			 * double percent = currentMovingSpeed / maxMovingSpeed;
			 * 
			 * cameraDelayX = cameraDelayMax * percent;
			 */

		}
	}

	public void update() {

		delayCounter += Screen.sleep;

		checkCollision();

		calculateCurrentImageChange();
		commonUpdate();
		updateInteraction();
		updateCooldowns();
		updateDash();
		updateCameraDelay();

		setMoving(false);
		// s� l�nge man r�r sig �t n�got h�ll
		if (isMovingLeft() || isMovingRight()) {

			int direction = 1; // 1 = right, -1 = left

			if (isMovingLeft()) {
				direction = -1;
			}

			currentMovingSpeed += movingAcceleration * direction;

			// om man har g�tt v�er max hastigheten
			if (Math.abs(currentMovingSpeed) > maxMovingSpeed) {
				currentMovingSpeed = maxMovingSpeed * direction;
			}
		} else { // om man ska deaccellerara
			if (currentMovingSpeed > 0) {
				currentMovingSpeed -= movingDeacceleration;
			}
			if (currentMovingSpeed < 0) {
				currentMovingSpeed += movingDeacceleration;
			}
			if (Math.abs(currentMovingSpeed) < movingDeacceleration) {
				currentMovingSpeed = 0;
			}
		}

		if (isMovingLeft()) {
			setMoving(true);
			setFacingLeft(true);
		}

		else if (isMovingRight()) {
			setMoving(true);
			setFacingLeft(false);
		}

		double totalMovingSpeed = currentMovingSpeed + currentDashSpeed;

		move(totalMovingSpeed, 0);

		if (godMode) {
			if (isMovingUp()) {
				move(0, -3);
			}
			if (isMovingDown()) {
				move(0, 3);
			}
		}

		// om man faller och inte hoppar och inte �r i godmode ska man falla ner�t
		if (isFalling() && !isJumping() && !godMode) {
			lengthFallen += currentFallingSpeed;
			if (currentFallingSpeed < maxFallingSpeed) {
				currentFallingSpeed += fallingAcceleration;

			}
			move(0, currentFallingSpeed);
		}

		if (isJumping()) {
			if (currentJumpingSpeed > minJumpingSpeed) { // om man ska sluta hoppa
				currentJumpingSpeed -= fallingAcceleration;
				move(0, -currentJumpingSpeed);
			} else {
				stopJumping();
			}
		}

		// g�r s� man f�r nytt liv �ver tid
		if (delayCounter % regHealthDelay == 0) {
			heal(regHealthAmount);

		}

		sendIsFalling();
		sendIsMoving();
		sendIsFacingLeft();
		sendImageChangeSpeed();
		sendHealth();
		sendIsDashing();

		actionBar.update();
	}

	public void checkCollision() {
		setFalling(true);
		// loopar igenom alla world objects och kollar om man kolliderar med dem
		for (int i = 0; i < MapHandler.worldObjects.size(); i++) {
			WorldObject object = MapHandler.worldObjects.get(i);
			if (object instanceof Platform) { // kollar om det �r en platform
				if (getFeetCollisionBox().intersects(object.getCollisionBox())) {
					stopFalling();
				}
			}
		}

	}

	public void toggleGodMode() {
		godMode = !godMode;
	}

	public ActionBar getActionBar() {
		return actionBar;
	}

	public void setActionBar(ActionBar actionBar) {
		this.actionBar = actionBar;
	}

	public void stopFalling() {
		if (isFalling()) {
			setFalling(false);
			currentFallingSpeed = startFallingSpeed;

			if (lengthFallen > safeFallingDistance) {
				int damage = (int) (lengthFallen * damagePerPixel);
				takeDamage(damage);
			}
			lengthFallen = 0;

		}
	}

	public void startJump() {
		// om man inte faller eller hoppar eller om doubleJump �r redo
		if ((!isJumping() && !isFalling()) || doubleJumpReady.get()) {
			// om man redan hoppar eller faller anv�nds doubleJump
			if (isJumping() || isFalling()) { // n�r man anv�nder double jump
				doubleJumpReady.set(false);
				currentJumpingSpeed = startDoubleJumpingSpeed;
			} else { // n�r man hoppar vanligt
				currentJumpingSpeed = startJumpingSpeed;
			}
			setJumping(true);
			stopFalling();
		}

	}

	public void stopJumping() {
		setJumping(false);
	}

	public void heal(int amount) {
		int health = getHealth() + amount;
		setHealth(health);
	}

	public void takeDamage(int damage) {
		int health = getHealth() - damage;
		setHealth(health);
	}

	public void updateCooldowns() {
		if (!doubleJumpReady.get()) {
			doubleJumpCooldownCounter.addAndGet(Screen.sleep);

			if (doubleJumpCooldownCounter.get() >= doubleJumpCooldown.get()) {
				doubleJumpReady.set(true);
				doubleJumpCooldownCounter.set(0);
			}

		}

		if (!dashReady.get()) {
			dashCooldownCounter.addAndGet(Screen.sleep);

			if (dashCooldownCounter.get() >= dashCooldown.get()) {
				dashReady.set(true);
				dashCooldownCounter.set(0);
			}
		}

	}

	public void updateInteraction() {
		// om man har sl�ppt interaction knappen slutar man interacta med objectet
		if (!holdingInteract || isMoving() || isFalling()) {
			stopInteraction();
		}

		if (interacting) {

			timeInteracted += Screen.sleep;

			// om man har interactat s� l�nge som kr�vs f�r detta objekt
			if (timeInteracted >= interactionObject.getInteractionTime()) {
				interactionObject.completeInteraction();
				stopInteraction();
			}

		}
	}

	public int getOnScreenX() {
		return (int) (onScreenX /* + cameraDelayX */);
	}

	public void setOnScreenX(int onScreenX) {
		this.onScreenX = onScreenX;
	}

	public int getOnScreenY() {
		return (int) (onScreenY /* + cameraDelayY */);
	}

	public void setOnScreenY(int onScreenY) {
		this.onScreenY = onScreenY;
	}

	// tar emot information fr�n servern om var man ska spawna
	public void setStartPos(String message) {
		String[] pos = message.split("&");

		int x = Integer.parseInt(pos[0]);
		int y = Integer.parseInt(pos[1]);
		setX(x);
		setY(y);
	}

	public void sendIsDashing() {
		int dashing = (isDashing()) ? 1 : 0;
		Client.sendData("#SNDISDASHING", dashing + "");

	}

	public void sendIsFalling() {
		int falling = (isFalling()) ? 1 : 0;
		Client.sendData("#ISFALLING", falling + "");
	}

	public void sendIsMoving() {
		int moving = (isMoving()) ? 1 : 0;
		Client.sendData("#ISMOVING", moving + "");
	}

	public void sendIsFacingLeft() {
		int facingLeft = (isFacingLeft()) ? 1 : 0;
		Client.sendData("#ISFACINGLEFT", facingLeft + "");
	}

	public void sendImageChangeSpeed() {
		Client.sendData("#SNDIMAGECHANGEDELAY", getImageChangeDelay() + "");
	}

	public void sendHealth() {
		Client.sendData("#SNDHEALTH", getHealth() + "");
	}

	public double getFallingSpeed() {
		return currentFallingSpeed;
	}

	public boolean isMoving() {
		return (Math.abs(currentMovingSpeed) > 0);
	}

	public boolean isDashing() {
		return (Math.abs(currentDashSpeed) > 0);
	}

	public boolean isHoldingInteract() {
		return holdingInteract;
	}

	public boolean isInteracting() {
		return interacting;
	}

	public void setHoldingInteract(boolean holdingInteract) {
		this.holdingInteract = holdingInteract;
	}

	public void startInteraction(InteractableWorldObject object) {
		this.interactionObject = object;
		interacting = true;
		timeInteracted = 0;
		interactionColor = new Color(255, 0, 0);
	}

	public void stopInteraction() {
		this.interactionObject = null;
		interacting = false;
	}

	@Override
	public void paint(Graphics2D g2d) {
		g2d = (Graphics2D) g2d.create();
		getHealthBar().paint(g2d);

		// om karakt�ren g�r �t h�ger ska bilden spelgas, detta g�rs genom att bredden g�rs negativ och x v�rdet flyttas lika l�ngt som karakt�rens bredd
		int modWidth = 1;
		int modX = 0;

		// om man g�r �t v�nster
		if (isFacingLeft()) {
			modWidth = -1;
			modX = -getWidth();
		}

		// om man �r utzoomad ska din karakt�r m�las p� ett annorlunda s�tt
		if (Screen.zoomOutDone) {
			g2d.drawImage(getImage(), Screen.fixX(getX(), 1) - modX, Screen.fixY(getY(), 1), getWidth() * modWidth, getHeight(), null);
		} else {
			g2d.drawImage(getImage(), getOnScreenX() - modX, getOnScreenY(), getWidth() * modWidth, getHeight(), null);

			// m�lar progress cirklen f�r en interaction
			if (interacting) {

				double completePercent = ((timeInteracted * 1.0) / interactionObject.getInteractionTime());

				// interactionColor.getR
				int red = interactionColor.getRed();
				int green = interactionColor.getGreen();
				int blue = interactionColor.getBlue();

				// om cirkeln �r i den f�rsta halvan. I s� fall ska det gr�na �ka och g� mot 255 s� att cirkeln blir gul
				if (completePercent < 0.5) {
					green = (int) (255 * completePercent * 2);
				}

				// om man �r p� den andra halvan av cirkeln. I s� fall ska det r�da minska s� den g�r mot gr�n
				if (completePercent > 0.5 && completePercent < 1) {
					double tmpCompletePercent = (completePercent - 0.5) * 2; // r�knar ut hur l�ngt man har kommit p� andra halvan
					red = (int) (255 * (1 - tmpCompletePercent));
				}

				interactionColor = new Color(red, green, blue);

				g2d.setColor(interactionColor);

				g2d.setStroke(new BasicStroke(6)); // g�r str�ck tjockare

				int deg = (int) (completePercent * 360);

				g2d.drawArc(getOnScreenX() + 80, getOnScreenY(), 45, 45, 90, -deg);
			}

		}
	}

	public void castSpell(int type) {
		Spell spell = new Spell(getX(), getY(), type);
	}

	public void pickUpPotion(int numPotions) {
		DisplayAbility ability = actionBar.getPotionAbility();
		int amount = ability.getAmount() + numPotions;
		ability.setAmount(amount);
	}

	public void pickUpWood(int numWood) {
		this.numWood.addAndGet(numWood);
	}

	public void startDash() {
		if (dashReady.get()) {
			dashReady.set(false);

			int mod = 1;
			if (isFacingLeft()) {
				mod = -1;
			}
			currentDashSpeed = startDashSpeed * mod;

		}
	}

	public AtomicBoolean getDoubleJumpReady() {
		return doubleJumpReady;
	}

	public AtomicBoolean getDashReady() {
		return dashReady;
	}

	public AtomicInteger getDoubleJumpCooldown() {
		return doubleJumpCooldown;
	}

	public AtomicInteger getDoubleJumpCooldownCounter() {
		return doubleJumpCooldownCounter;
	}

	public AtomicInteger getDashCooldown() {
		return dashCooldown;
	}

	public AtomicInteger getDashCooldownCounter() {
		return dashCooldownCounter;
	}

	public double getLengthFallen() {
		return lengthFallen;
	}

	public void usePotion() {
		int health = getHealth() + 50;
		setHealth(health);

	}

	public AtomicInteger getNumWood() {
		return numWood;
	}

	public double getMovingSpeed() {
		return currentMovingSpeed;
	}

}
