package client.ui.mainMenu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import client.Main;
import client.Screen;
import client.handlers.Images;
import client.handlers.Sound;

public class Button extends Component {

	URL hoverSound;

	Method method;

	public Button(int x, int y, int width, int height, int version, String description, Method method) {
		super(x, y, width, height, description);

		setImage(Images.readImageFromPath("ui/mainMenu/button" + version + ".png"));

		setHoverImage(Images.readImageFromPath("ui/mainMenu/buttonHover.png"));

		hoverSound = Sound.readSoundFile("sounds/ui/hover.wav");

		this.method = method;

	}

	@Override
	public void paintDescription(Graphics2D g2d) {

		int size = (int) (getHeight() * 0.5);
		int x = getX() + getWidth() / 2;
		int y = getY() + getHeight() / 2 - size / 2 - 8;

		Color color;

		color = Color.WHITE;

		Screen.paintText(x, y, Screen.standardFont, Font.BOLD, size, getDescription(), color, g2d, getWidth(), true);

	}

	@Override
	public void setHovered(boolean hovered) {
		// om det nya är att den ska vara hovrad och det gamla är att den inte var det
		if (hovered && !getHovered()) {
			Sound.play(hoverSound, 1f);
		}

		super.setHovered(hovered);
	}

	// körs när man klickar på knappen
	@Override
	public void handleClick(MouseEvent e) {
		try {
			method.invoke(Main.mainMenu, null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	@Override
	public void handleNotClick(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleKeyEvent(KeyEvent e) {
		
	}
}
