/*
 I denna klassen sker all event-hantering. Om man är i spelet sker hanteringen 
 direkt i metoderna i denna klassen och om man är i huvudmenyn skickas eventen
 till en metod i StartMenu.java som hanterar eventen.


 */
package client.handlers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import client.Main;
import client.Screen;
import client.players.YourPlayer;


public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {


	public static int onScreenMouseX, onScreenMouseY; // musens lokala råa x och y koordinat på skärmen
	public static int scaledOnScreenMouseX; // skalade lokala koordinater, som tar hänsyn till att din klients upplösning är skalad
	public static int scaledOnScreenMouseY;

	public static boolean clicking = false;

	@Override
	public void keyPressed(KeyEvent e) {
		// System.out.print(e.getKeyChar());

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}

		if (Main.state == Main.States.inGame) {
			Main.clientPlayer.getActionBar().handleKeyEvent(e);

			if (e.getKeyCode() == KeyEvent.VK_D) {
				Main.clientPlayer.setMovingRight(true);
				Main.clientPlayer.setMovingLeft(false);
			}
			if (e.getKeyCode() == KeyEvent.VK_A) {
				Main.clientPlayer.setMovingLeft(true);
				Main.clientPlayer.setMovingRight(false);
			}
			if (e.getKeyCode() == KeyEvent.VK_W) {
				Main.clientPlayer.setMovingUp(true);
			}
			if (e.getKeyCode() == KeyEvent.VK_S) {
				Main.clientPlayer.setMovingDown(true);
			}

			if (e.getKeyCode() == KeyEvent.VK_E) {
				Main.clientPlayer.setHoldingInteract(true);
			}

			if (e.getKeyCode() == KeyEvent.VK_F3) {
				Screen.devMode = !Screen.devMode;
			}

			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				Main.clientPlayer.startJump();
			}

			if (e.getKeyCode() == KeyEvent.VK_F) {
				Screen.toggleZoom();
			}

			if (e.getKeyCode() == KeyEvent.VK_B) {
				// MapHandler.causeBubbleEffect(Main.clientPlayer.getX(), Main.clientPlayer.getY(), 10, 0);
			}

			if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				Main.clientPlayer.startDash();
			}
			if (e.getKeyCode() == KeyEvent.VK_G) {
				Main.clientPlayer.toggleGodMode();
			}

			if (e.getKeyCode() == KeyEvent.VK_F2) {
				Main.clientPlayer.createDoubleJumpEffect();
			}

			if (e.getKeyCode() == KeyEvent.VK_F4) {
				Sound.startMusic();
			}
		} else if (Main.state == Main.States.inMainMenu) {
			Main.mainMenu.handleKeyEvent(e);
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (Main.state == Main.States.inGame) {
			if (e.getKeyCode() == KeyEvent.VK_D) {
				Main.clientPlayer.setMovingRight(false);
			}
			if (e.getKeyCode() == KeyEvent.VK_A) {
				Main.clientPlayer.setMovingLeft(false);
			}
			if (e.getKeyCode() == KeyEvent.VK_W) {
				Main.clientPlayer.setMovingUp(false);
			}
			if (e.getKeyCode() == KeyEvent.VK_S) {
				Main.clientPlayer.setMovingDown(false);
			}

			if (e.getKeyCode() == KeyEvent.VK_E) {
				Main.clientPlayer.setHoldingInteract(false);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent a) {
		clicking = true;
		// om man är ingame
		if (Main.state == Main.States.inGame) {
			Main.clientPlayer.getActionBar().handleMouseClicked(a);
		} else if (Main.state == Main.States.inMainMenu) {
			Main.mainMenu.handleMouseClicked(a);
		}
	}

	@Override
	public void mouseReleased(MouseEvent a) {
		clicking = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		setCoordsFromEvent(e);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		setCoordsFromEvent(e);
	}

	void setCoordsFromEvent(MouseEvent e) {

		onScreenMouseX = e.getX();
		onScreenMouseY = e.getY();

		// räknar ut skalad x och y får musen
		scaledOnScreenMouseX = (int) (e.getX() / Screen.scaleWidth);
		scaledOnScreenMouseY = (int) (e.getY() / Screen.scaleHeight);

		// om man är ingame
		if (Main.state == Main.States.inGame) {
			Main.clientPlayer.getActionBar().handleMouseMoved(e);
		} else if (Main.state == Main.States.inMainMenu) {
			Main.mainMenu.handleMouseMoved(e);
		}

	}

	public static double getMouseAngle() {
		double angle = 0;

		int yDist = scaledOnScreenMouseY - YourPlayer.onScreenY;
		int xDist = scaledOnScreenMouseX - YourPlayer.onScreenX;

		angle = Math.atan2(yDist, xDist);

		return angle;
	}

	public static int getWorldMouseX() {
		int worldX = onScreenMouseX + Main.clientPlayer.getX() - Main.clientPlayer.getOnScreenX();
		return worldX;
	}

	public static int getWorldMouseY() {
		int worldY = onScreenMouseY + Main.clientPlayer.getY() - Main.clientPlayer.getOnScreenY();
		return worldY;
	}
}
