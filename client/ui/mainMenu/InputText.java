package client.ui.mainMenu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import client.Screen;
import client.handlers.Images;

public class InputText extends Component {

	private boolean focused = false;

	private String value = "localhost";

	private String name = "";

	private boolean showMarker = false;
	private int markerCounter = 0;
	private int markerChangeInterval = 500;

	public InputText(int x, int y, int width, int height, String description, String name) {
		super(x, y, width, height, description);

		this.name = name;
		setImage(Images.readImageFromPath("ui/mainMenu/inputText.png"));
	}

	@Override
	public void paintDescription(Graphics2D g2d) {
		markerCounter += Screen.sleep;

		if (markerCounter >= markerChangeInterval) {
			markerCounter = 0;
			showMarker = !showMarker;
		}

		String marker = "";

		if (showMarker && focused) {
			marker = "|";
		}

		int size = 25;
		int textX = getX() + 15;
		int textY = (int) (getY() + getHeight() / 2 - size / 1.5);

		Screen.paintText(textX, textY, Screen.standardFont, Font.PLAIN, size, getDescription() + "" + value + marker, Color.white, g2d, 10000, false);
	}

	@Override
	public void handleClick(MouseEvent e) {
		focused = true;
	}

	public void handleNotClick(MouseEvent e) {
		focused = false;
	}

	@Override
	public void handleKeyEvent(KeyEvent e) {
		if (focused) {

			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				value = removeChar(value);
			} else if (e.getKeyCode() != KeyEvent.VK_TAB && e.getKeyCode() != KeyEvent.VK_ALT && e.getKeyCode() != KeyEvent.VK_ENTER && e.getKeyCode() != KeyEvent.VK_CONTROL && e.getKeyCode() != KeyEvent.VK_SHIFT) {
				value += e.getKeyChar();
			}
		}

	}

	public String removeChar(String value) {
		if (value.length() > 0) {
			value = value.substring(0, value.length() - 1);
		}

		return value;

	}

	public String getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

}
