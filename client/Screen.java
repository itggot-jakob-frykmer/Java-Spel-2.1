/*
 H�r sker allt grafiskt

 */
package client;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import client.effects.Effect;
import client.handlers.Images;
import client.handlers.InputHandler;
import client.handlers.MapHandler;
import client.objects.WorldObject;
import client.objects.backgroundObjects.BackgroundObject;
import client.players.Player;
import client.players.Team;
import client.players.YourPlayer;
import server.ServerMap;

@SuppressWarnings("serial")
public class Screen extends JPanel implements Runnable {

	InputHandler input;

	static boolean GM = false; // GM = God mode, f�r att kunna testa spelat l�ttare

	public static int sleep = 5; // hur l�nga tr�den ska v�nta mellan varje g�ng den k�rs
	static int tick = 0; // tick tickar relativt hur l�nge tr�den v�ntar mellan varje g�ng den k�rs s� man kan anv�nda den till att r�kna tid
	int singleTick = 0; // singleTick tickar en g�ng varje g�ng main-loopen k�rs

	static int noMessageReceivedFor = 0;

	public static Font standardFont;
	public static Font secondFont = new Font("Arial", Font.PLAIN, 20);

	// skalor utifr�n sk�rmens uppl�sning
	public static double scaleWidth;
	public static double scaleHeight;

	public static double scaleWidthZoom;
	public static double scaleHeightZoom;

	public static String test = "-1";
	public static String test1 = "-1";
	public static String test2 = "-1";
	public static String test3 = "-1";

	/*
	 * static double currentScaleWidth = 1; static double currentScaleHeight = 1;
	 */
	// static double scalingSpeed = 0.005;

	static boolean fadeOut = false;

	static float currentAlpha = 0f;

	public static boolean zoomOutDone = false;

	public static int panelWidth = 1920; // panelens bredd och h�jd
	public static int panelHeight = 1080;

	public static int screenWidth;
	public static int screenHeight;

	static JFrame frame;
	public static boolean devMode = false;

	// F�r att vissa saker ska m�las framf�r allt annat m�ste de f�rst m�las p� denna bilden och sen ut p� sk�rmen. Detta �r p� grund av att det inte finns n�got annat s�tt att kontrollera vilket lager
	// saker hamner p� f�rutom att m�la ut dom i r�tt ordning men det g�r inte alltid.
	public static BufferedImage frontImage = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
	public static Graphics2D frontImageGraphics = frontImage.createGraphics();

	public Screen() {
		super();
		Main.state = Main.States.inMainMenu;

		// startar InputHandler och l�gger in alla eventListeners
		input = new InputHandler();
		addKeyListener(input);
		addMouseMotionListener(input);
		addMouseListener(input);
		addMouseWheelListener(input);

		setFocusable(true);
		setFocusTraversalKeysEnabled(false); // g�r s� bl.a. tab funkar

		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;

		setRenderSettings(g2d, true);

		if (zoomOutDone) { // Om allt ska vara utzoomat
			g2d.scale(scaleWidthZoom, scaleHeightZoom);

		} else {
			g2d.scale(scaleWidth, scaleHeight); // skalar allt enligt skalorna som r�knades ut n�r spelet startades s� att spelat funkar p� alla sk�rmuppl�sningar
		}

		if (Main.state == Main.States.inGame) {



			paintBackgrounds(g2d);

			if (devMode) {
				g2d.drawString(test, 300, 100);
				g2d.drawString(test1, 300, 120);
				g2d.drawString(test2, 300, 140);
				g2d.drawString(test3, 300, 160);

				g2d.drawString("Playernumber: " + Client.playerNumber, 100, 100);
				g2d.drawString("Fallingspeed: " + Main.clientPlayer.getFallingSpeed(), 100, 120);

				g2d.drawString("Average delay to clients: " + Client.averageDelayToClients, 100, 140);
				g2d.drawString("x: " + Main.clientPlayer.getX(), 100, 160);
				g2d.drawString("y: " + Main.clientPlayer.getY(), 100, 180);
				g2d.drawString("LengthFallen : " + Main.clientPlayer.getLengthFallen(), 100, 200);
				g2d.drawString("Moving speed : " + Main.clientPlayer.getMovingSpeed(), 100, 220);
				g2d.drawString("Canfly : " + Main.clientPlayer.getCanFly(), 100, 240);
				g2d.drawString("IsMovingUp : " + Main.clientPlayer.isMovingUp(), 100, 260);
				g2d.drawString("playNumberreal: " + Main.clientPlayer.getPlayerNumber(), 100, 280);
				g2d.drawString("currentimageindex: " + Main.clientPlayer.getCurrentImageIndex(), 100, 300);

				int x = 500;

				for (int i = 0; i < Main.teams.size(); i++) {
					x += 200;

					int y = 200;

					g2d.drawString("Team: " + Main.teams.get(i).getTeamNumber(), x, y);

					for (int n = 0; n < Main.teams.get(i).getMembers().size(); n++) {
						Player player = Main.teams.get(i).getMembers().get(n);

						y += 50;
						g2d.drawString("player: " + player.getPlayerNumber(), x, y);
					}
				}
			}

			paintWorldObjects(g2d);
			paintPlayers(g2d);
			paintEffects(g2d);

			paintScore(g2d);
			/*
			 * g2d.fillRect(fixX(0, 1), fixY(0, 1), MapHandler.worldWidth, 50); g2d.fillRect(fixX(0, 1), fixY(0, 1), 50, MapHandler.worldHeight); g2d.fillRect(fixX(MapHandler.worldWidth - 50, 1), fixY(0, 1), 50, MapHandler.worldHeight); g2d.fillRect(fixX(0, 1), fixY(MapHandler.worldHeight - 50, 1), MapHandler.worldWidth, 50);
			 */
			/*
			 * for (int i = 0; i < MapHandler.treeSpawns.size(); i++) { TreeSpawn TS = MapHandler.treeSpawns.get(i); g2d.fillRect(fixX(TS.getX(), 1), fixY(TS.getY(), 1), TS.getWidth(), TS.getHeight()); }
			 */

			setScreenAlpha(g2d, currentAlpha);

			paintFrontImage(g2d);
			paintIngameUI(g2d);

		} else if (Main.state == Main.States.inMainMenu || Main.state == Main.States.inLoadingScreen) {
			Main.mainMenu.paint(g2d);
		}

		// m�lar musen
		Graphics2D g2 = reverseZoom(g2d);
		g2.drawImage(Images.imgCursor, (int) (InputHandler.onScreenMouseX), (int) (InputHandler.onScreenMouseY), 25, 25, null);
	}

	// m�lar bilden med alla saker som ska vara framf�r allt annat
	public void paintFrontImage(Graphics2D g2d) {
		g2d.drawImage(frontImage, 0, 0, frontImage.getWidth(), frontImage.getHeight(), null);
		frontImageGraphics.setBackground(new Color(0, 0, 0, 0));
		frontImageGraphics.clearRect(0, 0, frontImage.getWidth(), frontImage.getHeight());
	}

	public void paintScore(Graphics2D g2d) {

		int size = 30;

		int x = panelWidth / 2;
		int y = 20;

		for (int i = 0; i < Main.teams.size(); i++) {
			Team team = Main.teams.get(i);

			Color color = team.getColor();

			paintText(x, y, standardFont, Font.BOLD, size, team.getName() + " team: " + team.getScore() + " / " + ServerMap.scoreWinReq, color, g2d, 100000, true);
			y += size * 2;
		}

	}

	static void setRenderSettings(Graphics2D g2d, boolean good) {
		if (good) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			// g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
			g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		}
	}

	void paintIngameUI(Graphics2D g2d) {
		// m�lar actionbaren
		YourPlayer clientPlayer = Main.clientPlayer;
		clientPlayer.getActionBar().paint(g2d);

		// m�lar ikonerna som visar vilket lag som �ger tornen
		for (int i = 0; i < MapHandler.objectiveTowerSpawns.size(); i++) {
			MapHandler.objectiveTowerSpawns.get(i).paint(g2d);
		}

		for (int i = 0; i < Main.teams.size(); i++) {
			Main.teams.get(i).paint(g2d);
		}

	}

	void paintBackgrounds(Graphics2D g2d) {
		Graphics2D g2 = reverseZoom(g2d); // reversrar zoomen

		for (int i = 0; i < MapHandler.worldObjects.size(); i++) {
			WorldObject obj = MapHandler.worldObjects.get(i);
			// mo det �r ett bakgrundsobjekt eller solen
			if (obj instanceof BackgroundObject) {
				obj.paint(g2d);
			}
		}

		// g2.drawImage(Images.imgBackgroundGradient, 0, 0, panelWidth, panelHeight, null);
		g2d.setColor(Color.black);
		g2d.fillRect(fixX(0, 1) - 2000, fixY(MapHandler.groundLevel, 1), MapHandler.worldWidth + 4000, MapHandler.worldHeight); // m�lar m�rk under marken

	}

	void paintWorldObjects(Graphics2D g2d) {

		for (int i = 0; i < MapHandler.worldObjects.size(); i++) {
			WorldObject object = MapHandler.worldObjects.get(i);
			if (!(object instanceof BackgroundObject)) {
				object.paint(g2d);
			}
		}
	}

	public static Graphics2D reverseZoom(Graphics2D g2d) {
		Graphics2D g2 = (Graphics2D) g2d.create(); // skapar en kopia av graphics objectet

		// reverear zoomen
		if (!zoomOutDone) {
			g2.scale(1 / scaleWidth, 1 / scaleHeight); // skalar allt enligt skalorna som r�knades ut n�r spelet startades s� att spelat funkar p� alla sk�rmuppl�sningar
		} else {
			g2.scale(1 / scaleWidthZoom, 1 / scaleHeightZoom);
		}

		return g2;
	}

	void paintEffects(Graphics2D g2d) {
		for (int i = 0; i < Main.effects.size(); i++) {
			Effect efc = Main.effects.get(i);
			efc.paint(g2d);
		}
	}

	void paintPlayers(Graphics2D g2d) {
		for (int i = 0; i < Main.players.size(); i++) {
			Player player = Main.players.get(i);
			player.paint(g2d);
		}
	}

	public static Graphics2D setAlpha(Graphics2D g2d, float alpha) {

		AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g2d.setComposite(alcom);

		return g2d;
	}

	public static void setScreenAlpha(Graphics2D g2d, float alpha) {
		Graphics2D g2 = (Graphics2D) g2d.create();

		AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g2.setComposite(alcom);
		g2.fillRect(0, 0, MapHandler.worldWidth, MapHandler.worldHeight);
	}

	public static void toggleZoom() {
		fadeOut = !fadeOut;
	}

	static void updateScreen() {
		if (fadeOut) {
			if (currentAlpha < 1) {
				currentAlpha += 0.03;
				if (currentAlpha >= 1) { // n�r den �r klar
					currentAlpha = 1;

					zoomOutDone = !zoomOutDone;
					fadeOut = false;

				}
			}
		} else {
			if (currentAlpha > 0) {
				currentAlpha -= 0.03;
				if (currentAlpha < 0) { // n�r den �r klar
					currentAlpha = 0;
				}
			}
		}
	}

	public static int fixX(int oldX, double paralax) {
		int newX = 0;
		if (!zoomOutDone) {
			newX = (int) (oldX - (Main.clientPlayer.getX() * paralax) + Main.clientPlayer.getOnScreenX());
		} else {
			newX = (int) (oldX - 0);
		}

		return newX;
	}

	public static int fixY(int oldY, double paralax) {
		int newY = 0;
		if (!zoomOutDone) {
			newY = (int) (oldY - (Main.clientPlayer.getY() * paralax) + Main.clientPlayer.getOnScreenY());
		} else {
			newY = (int) (oldY - 0);

		}
		return newY;
	}

	// h�mtar en texts bredd
	public static int getTextWidth(Font font, String text) {
		int width = 0;

		AffineTransform affinetransform = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
		width = (int) (font.getStringBounds(text, frc).getWidth());

		return width;
	}

	public static int getTextHeight(Graphics2D g2d, String text, Font font) {

		FontMetrics metrics = g2d.getFontMetrics(font);
		int height = metrics.getHeight();

		return height;
	}

	// anv�nds f�r att p� ett enklare s�tt skriva ut text p� sk�rmen. Denna metod anv�nds tillsammans med wrapLines()
	// som g�r att texten hamnar p� en ny rad om textens bredd �verstrider 'lineWrapWidth'
	public static void paintText(int x, int y, Font font, int style, int size, String text, Color color, Graphics2D g, int lineWrapWidth, boolean centerText) {

		font = font.deriveFont(style, size);

		g.setFont(font);
		g.setColor(color);

		// wrapLines() delar upp texten i olika rader
		String[] lines = wrapLines(text, font, lineWrapWidth);

		int widestLine = 0;

		// hittar den bredaste raden
		for (int i = 0; i < lines.length; i++) {
			int currentLineWidth = getTextWidth(font, lines[i]);

			if (currentLineWidth > widestLine) {
				widestLine = currentLineWidth;
			}

		}

		int widestLineXOffset = 0;

		if (centerText) {
			widestLineXOffset = widestLine / 2;
		}

		// m�lar ut varje rad f�r sig
		for (int i = 0; i < lines.length; i++) {
			int textWidth = getTextWidth(font, lines[i]);

			int tmpX = x;
			// centerar raden
			if (centerText) {
				// placerar texten r�tt s� att varje rad centeras
				tmpX = x + widestLineXOffset - textWidth / 2 - widestLine / 2;
			}
			g.drawString(lines[i], tmpX, y + size + size * i);
		}

	}

	// anv�nds f�r att dela in text i flera rader
	public static String[] wrapLines(String text, Font font, int lineWrapWidth) {
		String strReturn[] = null;

		String buildString = "";
		String nextLineString = "";

		String[] words = text.split(" ");

		for (int i = 0; i < words.length; i++) {
			// om ett ord b�rjar p� '#' ska raden brytas d�r
			if (words[i].startsWith("#")) {
				nextLineString = " ";
			}

			// r�knar ut bredden p� texten
			AffineTransform affinetransform = new AffineTransform();
			FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
			int textWidth = (int) (font.getStringBounds(nextLineString, frc).getWidth());

			// kollar om bredden �r �verstridigt radbredden
			if (textWidth > lineWrapWidth) {
				buildString += "#"; // '#' visar att det ska vara en ny rad d�r
				nextLineString = "";
			}

			buildString += words[i] + " ";
			nextLineString += words[i] + " ";

		}

		strReturn = buildString.split("#");

		return strReturn;
	}

	// skapar framen, detta k�rs f�rst av allt
	static void createFrame() {
		Images.initImages(); // laddar in bilderna

		// hittar skalan f�r sk�rmen s� det kan skalas senare i paint metoden
		scaleWidth = screenWidth / (Screen.panelWidth * 1.0);
		scaleHeight = screenHeight / (Screen.panelHeight * 1.0);

		// g�r s� att muspekaren blir en bild
		// Point hotspot = new Point(1, 1); // s�tter 'hotspot' f�r crosshairet, (pointen d�r mus-event ska ske), i detta fall ska det vara i mitten av muspekaren och bilden �r 34x34
		// Cursor myCursor = tk.createCustomCursor(Images.imgCursor, hotspot, "cursor");

		// skapar framen
		frame = new JFrame();
		frame.add(new Screen());
		frame.setTitle("Warden");
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(screenWidth, screenHeight);
		frame.setVisible(true);
		frame.setCursor(frame.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null"));

		// �ndrar programmets ikon
		ImageIcon imgIcon = new ImageIcon(Images.class.getClassLoader().getResource("images/icon.png"));
		Image img = imgIcon.getImage();
		frame.setIconImage(img);
		System.out.println("Frame created");

		// Sound.startMusic();
	}

	public void run() {

		while (true) {

			tick += sleep;
			singleTick++;

			// om man �r inne i spelet eller i inGame-menyn
			if (Main.state == Main.States.inGame) {
				noMessageReceivedFor = noMessageReceivedFor + sleep; // r�knar tid
			}

			Main.update();
			repaint();
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public static void initFonts() {

		InputStream is = Screen.class.getClassLoader().getResourceAsStream("fonts/Cinzel-Bold.ttf");

		try {
			standardFont = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void init() {
		Toolkit tk = Toolkit.getDefaultToolkit();

		// h�mtar datorns sk�rmstorlek
		Screen.screenWidth = (int) (tk.getScreenSize().getWidth());
		Screen.screenHeight = (int) (tk.getScreenSize().getHeight());

		initFonts();
	}
}
