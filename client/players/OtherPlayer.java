package client.players;

import java.awt.Graphics2D;

import client.Screen;

public class OtherPlayer extends Player {

	public OtherPlayer(int playerNumber, int characterType) {
		super(playerNumber, characterType);
	}

	public void update() {
		commonUpdate();
	}

	public void paint(Graphics2D g2d) {
		getHealthBar().paint(g2d);
		// om karakt�ren g�r �t h�ger ska bilden spelgas, detta g�rs genom att bredden g�rs negativ och x v�rdet flyttas lika l�ngt som karakt�rens bredd
		int modWidth = 1;
		int modX = 0;

		// om man g�r �t v�nster
		if (isFacingLeft()) {
			modWidth = -1;
			modX = -getWidth();
		}

		g2d.drawImage(getImage(), Screen.fixX(getX(), 1) - modX, Screen.fixY(getY(), 1), getWidth() * modWidth, getHeight(), null);

	}

}
