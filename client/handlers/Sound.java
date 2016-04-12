/*
 I denna klassen finns alla metoder som behÃ¶vs fÃ¶r att spela upp ljud
    
 */
package client.handlers;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import client.Main;
import client.Screen;

/**
 *
 * @author Jakob
 */
public class Sound {

	static float masterVolumeMod = 0.7f;

	static Clip clipMusic;
	static float musicVolume;

	// hÃ¶jer eller sÃ¤kner musikvolymen
	static void changeMusicVolume(float newVolume) {
		FloatControl volume = (FloatControl) clipMusic.getControl(FloatControl.Type.MASTER_GAIN);

		if (newVolume > 6) {
			newVolume = 6;
		} else if (newVolume < -79) {
			newVolume = -79;
		}

		volume.setValue(newVolume * masterVolumeMod);

		musicVolume = volume.getValue();
	}

	// startar musiken
	static void startMusic() {
		try {
			String path = "sounds/music/music.wav";
			URL urlMusic = Sound.class.getClassLoader().getResource(path);

			AudioInputStream audioIn = AudioSystem.getAudioInputStream(urlMusic);

			// Get a sound clip resource.
			clipMusic = AudioSystem.getClip();

			// Open audio clip and load samples from the audio input stream.
			clipMusic.open(audioIn);
			clipMusic.loop(Clip.LOOP_CONTINUOUSLY);
			clipMusic.start();
			changeMusicVolume(-5);
		} catch (LineUnavailableException ex) {
			Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
		} catch (UnsupportedAudioFileException ex) {
			Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// stoppar musiken
	static void stopMusic() {
		clipMusic.stop();
	}

	/*
	 * static void play(URL url, int soundX, int soundY) { AudioInputStream sound = readSoundFile(url.getPath()); play(sound, soundX, soundY); }
	 */

	// spelar upp ett ljud precis dÃ¤r du Ã¤r, sÃ¥ att ljudet inte pÃ¥verkas av avstÃ¥nd osv
	public static void play(URL sound, float specificMod) {
		if (Main.clientPlayer == null) {
			play(sound, 0, 0, specificMod);
		} else {
			play(sound, Main.clientPlayer.getX(), Main.clientPlayer.getY(), specificMod);
		}
	}

	public static void play(final URL url, final int soundX, final int soundY, final float specificMod) {

		new Thread(new Runnable() {
			public void run() {
				try {
					
					int balanceModifier = 1;

					int xDiff;
					int yDiff;
					
					// kollar så att din karaktär har skapats
					if (Main.clientPlayer != null) {
						xDiff = soundX - Main.clientPlayer.getX();
						yDiff = soundY - Main.clientPlayer.getY();
					} else {
						xDiff = 0;
						yDiff = 0;
					}
					
					// räknar ut avståndet mellan ljudet och din karaktär
					int distance = Math.abs((int) Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2)));
					
					// räknar ut hur hög volymet ska vara, ju hÃ¶gre avstÃ¥nd desto lÃ¤gre ljud (bÃ¶rjar pÃ¥ 6 fÃ¶r max vÃ¤rdet pÃ¥ volymet Ã¤r 6)
					float flVolume = 6 - (float) (distance / (75 * 1.0));

					float flBalance; // balansen i hÃ¶gtalaren

					// kollar om ljudet kommer frÃ¥n hÃ¶ger eller vÃ¤nster
					if (xDiff > 0) {
						balanceModifier = 1;
					} else if (xDiff < 0) {
						balanceModifier = -1;
					}
					
					// om skottet Ã¤r väldigt nära din karaktär blir balancen 0
					if (Math.abs(xDiff) < 120) {
						balanceModifier = 0;
					}
					
					// om avstÃ¥ndet Ã¤r 0 sÃ¥ blir balancen 0. Ju högre avståndet är desto högre blir flBalance
					if (distance != 0 && xDiff != 0) {
						flBalance = (float) (1 - (1D / (Math.abs(xDiff) * 0.01))) * balanceModifier;
					} else {
						flBalance = 0f;
					}
					
					// när distance blir för litet ibland så blir flBalance över 1 och då blir den 0
					if (flBalance > 1 || flBalance < -1) {
						flBalance = 0;
					}

					// -80 är min-värdet för volym
					if (flVolume < -80) {
						flVolume = -80;
					}
					
					AudioInputStream sound = AudioSystem.getAudioInputStream(url);
					
					// hÃ¤mtar klippet
					final Clip clip = AudioSystem.getClip();
					
					clip.open(sound);
					
					try {
						sound.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					// sätter balansen i högtalarna
					FloatControl balance = (FloatControl) clip.getControl(FloatControl.Type.BALANCE);
					
					balance.setValue(flBalance);
					
					// sätter volymen
					FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
					
					volume.setValue(fixVolume(flVolume, masterVolumeMod, specificMod));
					
					clip.start();
					
					// gör så att klippet stängs efter att det spelats klart, detta är för att skona ramminnet
					clip.addLineListener(new LineListener() {
						@Override
						public void update(LineEvent event) {
							if (event.getType() == LineEvent.Type.STOP) {

								
								clip.close();
								

							}
						}
					});
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	// fixar volymen enligt en modifer så volymen kan justeras
	public static float fixVolume(float oldVolume, float modifer, float specificMod) {
		float newVolume = oldVolume;

		// all volym under -45 går knappt att höra
		float volDistFromMin = Math.abs(-45 - oldVolume);

		newVolume = -45 + volDistFromMin * modifer * specificMod;

		return newVolume;
	}

	// spela upp ett ljud, soundX och soundY Ã¤r var i vÃ¤rlden ljudet ska spelas upp, detta behÃ¶vs fÃ¶r att det ska bli surround-ljud
	/*
	 * static void play(URL url, int soundX, int soundY) {
	 * 
	 * try { int balanceModifier = 1;
	 * 
	 * int xDiff; int yDiff;
	 * 
	 * // kollar sÃ¥ att din karaktÃ¤r har skapats if (Main.clientPlayer != null) { xDiff = soundX - Main.clientPlayer.getX(); yDiff = soundY - Main.clientPlayer.getY(); } else { xDiff = 0; yDiff = 0; }
	 * 
	 * // rÃ¤knar ut avstÃ¥ndet mellan ljudet och din karaktÃ¤rr int distance = Math.abs((int) Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2)));
	 * 
	 * // rÃ¤knar ut hur hÃ¶g volymet ska vara, ju hÃ¶gre avstÃ¥nd desto lÃ¤gre ljud (bÃ¶rjar pÃ¥ 6 fÃ¶r max vÃ¤rdet pÃ¥ volymet Ã¤r 6) float flVolume = 6 - (float) (distance / (75 * 1.0));
	 * 
	 * float flBalance; // balansen i hÃ¶gtalaren
	 * 
	 * // kollar om ljudet kommer frÃ¥n hÃ¶ger eller vÃ¤nster if (xDiff > 0) { balanceModifier = 1; } else if (xDiff < 0) { balanceModifier = -1; } // om skottet Ã¤r vÃ¤ldigt nÃ¤ra din karaktÃ¤r blir balancen 0 if (Math.abs(xDiff) < 120) { balanceModifier = 0; }
	 * 
	 * // om avstÃ¥ndet Ã¤r 0 sÃ¥ blir balancen 0. Ju hÃ¶gre avstÃ¥ndet Ã¤r desto hÃ¶gre blir flBalance if (distance != 0 && xDiff != 0) { flBalance = (float) (1 - (1D / (Math.abs(xDiff) * 0.01))) * balanceModifier; } else { flBalance = 0f; }
	 * 
	 * // när distance blir för litet ibland så blir flBalance över 1 och då blir den 0 if (flBalance > 1 || flBalance < -1) { flBalance = 0; }
	 * 
	 * // -80 är min-värdet för volym if (flVolume < -80) { flVolume = -80; } // System.out.println(flBalance);
	 * 
	 * // lÃ¤ser in ljudfilen med hjÃ¤lp utav dess url AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
	 * 
	 * // hÃ¤mtar klippet final Clip clip = AudioSystem.getClip(); clip.open(audioIn);
	 * 
	 * // sÃ¤tter balansen i hÃ¶gtalarna FloatControl balance = (FloatControl) clip.getControl(FloatControl.Type.BALANCE); balance.setValue(flBalance);
	 * 
	 * // sÃ¤tter volymen FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN); volume.setValue(flVolume);
	 * 
	 * // System.out.println("balance: " + flBalance + " xdiff : " + xDiff + " ydiff " + yDiff + " distance : " + distance); clip.start();
	 * 
	 * // hÃ¶r sÃ¥ att klippet stÃ¤ngs efter att det spelats klart, detta Ã¤r fÃ¶r att skona ramminnet clip.addLineListener(new LineListener() {
	 * 
	 * @Override public void update(LineEvent event) { if (event.getType() == LineEvent.Type.STOP) { clip.close(); } } });
	 * 
	 * // clip.loop(Clip.LOOP_CONTINUOUSLY); } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * }
	 *//*
		*/
	public static URL readSoundFile(String path) {
		URL url = Sound.class.getClassLoader().getResource(path);

		return url;
	}
	/*
	 * public static AudioInputStream readSoundFile(String path) {
	 * 
	 * URL url = Sound.class.getClassLoader().getResource(path); AudioInputStream sound = null;
	 * 
	 * try { sound = AudioSystem.getAudioInputStream(url); } catch (UnsupportedAudioFileException | IOException e) { e.printStackTrace(); }
	 * 
	 * return sound;
	 * 
	 * }
	 */

}
