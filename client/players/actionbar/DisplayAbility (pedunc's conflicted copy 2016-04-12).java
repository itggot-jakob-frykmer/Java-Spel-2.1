package client.players.actionbar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import client.Main;
import client.Screen;
import client.handlers.Images;

public class DisplayAbility {
	private int id;
	private int x;
	private int y;
	private int size;
	private boolean round;
	private boolean showCounter;
	private int amount;
	private int keyBind;

	private Image image;
	private Image highlightImage;
	private Image shadowImage;
	private boolean hovered = false;

	private Method method;
	private Object methodHolder;
	private Object[] parameters;

	private int cooldown;
	private int cooldownLeft = 0;

	private int energyCost;

	private int levelReq = 0; // vilket level på vapnet man behöver för att använda denna ability

	public DisplayAbility(int id, int x, int y, int size, boolean round, boolean showCounter, int keyBind, Method method, Object methodHolder, Object parameter, int cooldown, int energyCost, int levelReq) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.size = size;
		this.image = Images.readImageFromPath("ui/abilities/ability" + id + ".png");

		this.levelReq = levelReq;
		this.round = round;
		this.showCounter = showCounter;
		this.keyBind = keyBind;
		this.cooldown = cooldown;
		this.energyCost = energyCost;

		this.method = method; // vilken metod denna ability ska köra
		this.methodHolder = methodHolder; // vilket object som håller metoden denna ability ska köra

		if (round) {
			this.highlightImage = ActionBar.powerHighlight;
			this.shadowImage = ActionBar.powerShadow;
		} else {
			this.highlightImage = ActionBar.abilityHighlight;
			this.shadowImage = ActionBar.abilityShadow;
		}

		// sparar parameterns som ska skickas med methoden som denna ability ska köra
		if (parameter != null) { // Om ingen parameter ska skickas med methoden är 'parameter' null. Om den inte är null sparas parametern i en lista över parametrar, annars blir listan null
			parameters = new Object[1];
			parameters[0] = parameter;
		} else {
			parameters = null;
		}

		// -1 betyder att man har oändligt med charges
		if (!showCounter) {
			amount = -1;
		} else {
			amount = 5;
		}

	}

	public void paint(Graphics2D g2d) {

		Graphics2D g2 = Screen.reverseZoom(g2d); // reversar zoomen

		g2.drawImage(getImage(), getX(), getY(), getSize(), getSize(), null); // målar ikonen
		g2.drawImage(getOverlay(), getX(), getY(), getSize(), getSize(), null); // målar skugga eller ljus över ikonen beroende på om man hovrar över ikonen eller inte

		paintCooldownShadow(g2); // målar cooldownskuggan

		// om man inte har råd med abilityn målas en skugga över den eller om man inte har tillräckligt hög nivå på vapnet för att göra denna ability
		if (Main.clientPlayer.getEnergy() < energyCost || Main.clientPlayer.getWeapon().getLevel() < levelReq) {
			paintFullShadow(g2d);
		}

		// på vissa abilities ska en counter visas för att visa hur många charges man har kvar
		if (getShowCounter()) {
			String text = getAmount() + "";
			Font font = new Font("Calibri", Font.BOLD, 40);

			int textWidth = Screen.getTextWidth(font, text);

			int fontX = getX() + getSize() - textWidth - 5;
			int fontY = getY() + getSize() - 5;

			g2.setFont(font);
			g2.setColor(Color.white);
			g2.drawString(getAmount() + "", fontX, fontY);
		}
	}

	public void paintFullShadow(Graphics2D g2d) {
		g2d.setColor(new Color(0, 0, 0, 180));
		if (!round) {
			g2d.fillRect(getX(), getY(), getSize(), getSize());
		} else {
			g2d.fillOval(getX(), getY(), getSize(), getSize());
		}
	}

	// Målar cooldownskuggan. Skuggan målas som en cirkel över ikonen som fylls på allt eftersom cooldownen tickar ner. Först målas cirkeln på en bufferedimage som har samma
	// storlek som ikonen och sedan målas bufferedimage på panelen. Detta är för att cirkeln ska kunnas målas större än vad ikonen egentligen är utan att synas utanför för att det
	// ska se ut som att det är en kvadrat som skuggar ikonen.
	public void paintCooldownShadow(Graphics2D g2) {

		BufferedImage bimage = new BufferedImage(getSize(), getSize(), BufferedImage.TYPE_INT_ARGB);

		Graphics2D bGr = bimage.createGraphics();

		bGr.setColor(new Color(0, 0, 0, 180));
		// int deg = (int) (completePercent * 360);

		int width = getSize();
		int height = getSize();

		// om ikonen inte är rund måste storleken vara större
		if (!round) {
			width *= 2;
			height *= 2;
		}

		int x = getSize() / 2 - width / 2;
		int y = getSize() / 2 - height / 2;

		double percentComplete = (cooldownLeft * 1D) / cooldown; // hur många procent av cooldownen som har gått

		int deg = (int) (360 * percentComplete);

		bGr.fillArc(x, y, width, height, 90, deg);

		g2.drawImage(bimage, getX(), getY(), null);

	}

	public void update() {
		// tickar ner på cooldownen
		if (cooldownLeft > 0) {
			cooldownLeft -= Screen.sleep;
		}

		if (cooldownLeft < 0) {
			cooldownLeft = 0;
		}
	}

	public void use() {

		// Om spelaren har det vapnet som krävs för att göra denna ability
		if (Main.clientPlayer.getWeapon().getLevel() >= levelReq) {
			// om den inte är på coolden
			if (cooldownLeft == 0) {
				// om man har oändligt eller har mer än 0
				if (amount == -1 || amount > 0) {
					// kollar så man har tillräckligt med energy
					if (Main.clientPlayer.getEnergy() >= energyCost) {
						try {
							method.invoke(methodHolder, parameters);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}

						Main.clientPlayer.addEnergy(-energyCost);

						if (amount != -1) {
							amount--;
						}
						cooldownLeft = cooldown;
					}
				}
			}
		}

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}
	
	public boolean getHovered(){
		return hovered;
	}

	public Image getImage() {
		return image;
	}

	public Image getOverlay() {
		Image img = null;

		if (hovered) {
			img = highlightImage;
		} else {
			img = shadowImage;
		}

		return img;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public boolean getShowCounter() {
		return showCounter;
	}

	public int getKeyBind() {
		return keyBind;
	}

}
