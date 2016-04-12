package client.ui.mainMenu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

import client.Client;
import client.Main;
import client.Screen;
import client.handlers.Images;

// huvudmenyn som kommer upp när man startar spelet
public class MainMenu {

	boolean open = true;

	Image background;
	Image trees;


	Image logo;

	// storlek för loggan
	double scale = 1.3;
	int logoWidth = (int) (444 * scale);
	int logoHeight = (int) (173 * scale);

	// placering för loggan
	int logoX = Screen.screenWidth / 2 - logoWidth / 2; // centerar
	int logoY = 80;

	ArrayList<Bird> birds = new ArrayList<Bird>();

	ArrayList<Component> components = new ArrayList<Component>();

	private int spawnBirdsInterval = 30000;
	private int birdsIntervalCounter = 0;

	private int loadingDotsInterval = 500;
	private int loadingDotsCounter = 0;

	private String dots = "";

	public MainMenu() {
		loadImages();
		initComponents();
		spawnBirds();
	}

	public void update() {
		birdsIntervalCounter += Screen.sleep;
		loadingDotsCounter += Screen.sleep;

		if (birdsIntervalCounter >= spawnBirdsInterval) {
			birdsIntervalCounter = 0;
			spawnBirds();
		}

		for (int i = 0; i < birds.size(); i++) {
			birds.get(i).update();
		}

		// uppdaterar "..." på loading skärmen
		if (loadingDotsCounter >= loadingDotsInterval) {
			loadingDotsCounter = 0;
			if (dots.equals("...")) {
				dots = "";
			} else {
				dots += ".";
			}
		}
	}

	// spawnar in fåglar
	public void spawnBirds() {
		int num = 5;

		Random ra = new Random();
		int varyY = 100;
		int varyX = 300;

		for (int i = 0; i < num; i++) {
			// slumpar posiitonerna lite
			int y = (int) (350 + varyY * ra.nextDouble());
			int x = (int) (varyX * ra.nextDouble() - varyX);
			Bird bird = new Bird(x, y);
			birds.add(bird);
		}

	}

	public void initComponents() {

		Component component = null;

		double scale = 0.86;
		int buttonWidth = (int) (377 * scale);
		int buttonHeight = (int) (100 * scale);

		int buttonX = Screen.screenWidth / 2 - buttonWidth / 2;
		int buttonY = 500;

		int buttonDist = 140;

		scale = 0.86;
		int inputHeight = (int) (100 * scale * 0.6);
		int inputY = 400;

		// de tre huvudknapparna
		Method method;
		try {

			// IP-textfield
			component = new InputText(buttonX, inputY, buttonWidth, inputHeight, "IP: ", "IP");
			components.add(component);

			// playknappen
			method = this.getClass().getMethod("startGame", null);// metoden som ska köras
			component = new Button(buttonX, buttonY + buttonDist * 0, buttonWidth, buttonHeight, 0, "Play", method);
			components.add(component);

			// credits-knappen
			method = this.getClass().getMethod("openCredits", null); // metoden som ska köras
			component = new Button(buttonX, buttonY + buttonDist * 1, buttonWidth, buttonHeight, 1, "Credits", method);
			components.add(component);

			// options-knappen
			method = this.getClass().getMethod("openOptions", null); // metoden som ska köras
			component = new Button(buttonX, buttonY + buttonDist * 2, buttonWidth, buttonHeight, 2, "Options", method);
			components.add(component);

			// storlek för quit button
			scale = 0.7;
			buttonWidth *= scale;
			buttonHeight *= scale;

			int quitButtonX = (int) (Screen.screenWidth - buttonWidth * 1.05);
			int quitButtonY = (int) (Screen.screenHeight - buttonHeight * 1.5);

			method = this.getClass().getMethod("quitGame", null); // metoden som ska köras
			component = new Button(quitButtonX, quitButtonY, buttonWidth, buttonHeight, 3, "Quit", method);
			components.add(component);

		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void quitGame() {
		System.exit(0);
	}

	public void openOptions() {

	}

	public void openCredits() {

	}

	public void startGame() {
		Client.connect(getComponentValueByName("IP"));
	}

	// hantera musklick
	public void handleMouseClicked(MouseEvent e) {

		for (int i = 0; i < components.size(); i++) {
			Component component = components.get(i);

			// om man klickar på denna komponent
			if (component.getHovered()) {
				component.handleClick(e);
			} else {
				component.handleNotClick(e);
			}

		}

	}

	public void handleKeyEvent(KeyEvent e) {
		for (int i = 0; i < components.size(); i++) {
			Component component = components.get(i);

			component.handleKeyEvent(e);
		}
	}

	public void handleMouseMoved(MouseEvent e) {
		Rectangle mouseRect = new Rectangle(e.getX(), e.getY(), 1, 1);

		for (int i = 0; i < components.size(); i++) {
			Component component = components.get(i);

			if (component.getCollisionBox().intersects(mouseRect)) {
				component.setHovered(true);
			} else {
				component.setHovered(false);
			}
		}
	}

	public void loadImages() {
		// laddar in bakgrunden
		background = Images.readImageFromPath("ui/mainMenu/startScreen0.png");

		// laddar in träden
		trees = Images.readImageFromPath("ui/mainMenu/startScreen1.png");

		// loggan
		logo = Images.readImageFromPath("ui/mainMenu/logo.png");

	}

	public void paint(Graphics2D g2d) {
		g2d.drawImage(background, 0, 0, Screen.screenWidth, Screen.screenHeight, null);

		for (int i = 0; i < birds.size(); i++) {
			birds.get(i).paint(g2d);
		}
		g2d.drawImage(trees, 0, 0, Screen.screenWidth, Screen.screenHeight, null);
	

		g2d.drawImage(logo, logoX, logoY, logoWidth, logoHeight, null);

		// om man är på huvud skärmen
		if (Main.States.inMainMenu == Main.state) {
			for (int i = 0; i < components.size(); i++) {
				components.get(i).paint(g2d);
			}
		} else if (Main.States.inLoadingScreen == Main.state) {

			Screen.paintText(Screen.screenWidth / 2, 500, Screen.standardFont, Font.BOLD, 80, "Loading" + dots, Color.BLACK, g2d, 100000, true);
		}

	}

	public String getComponentValueByName(String name) {
		String value = "";

		for (int i = 0; i < components.size(); i++) {
			Component comp = components.get(i);

			if (comp instanceof InputText) {
				if (((InputText) comp).getName().equals(name)) {
					value = ((InputText) comp).getValue();
				}
			}
		}

		return value;

	}

	public boolean isOpen() {
		return open;
	}
}
