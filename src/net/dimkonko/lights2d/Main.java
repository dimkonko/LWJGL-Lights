package net.dimkonko.lights2d;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import net.dimkonko.lights2d.states.BasicState;
import net.dimkonko.lights2d.states.StateGame;
import net.dimkonko.lights2d.utils.Natives;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

public class Main {

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;

	private static final int FRAME_RATE = 60;
	
	public static boolean isRunning = true;

	private BasicState game;

	public Main() {
		initDisplay();
		initGL();

		game = new StateGame();
		game.init();

		try {
			startLoop();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		clean();
	}

	private void initDisplay() {
		try {
			DisplayMode displayMode = null;
			DisplayMode[] modes = Display.getAvailableDisplayModes();
//			System.out.println(Arrays.toString(modes));

			for (int i = 0; i < modes.length; i++) {
				if (modes[i].getWidth() == WIDTH
						&& modes[i].getHeight() == HEIGHT
						&& modes[i].isFullscreenCapable()) {
					displayMode = modes[i];
				}
			}
			if(displayMode == null) {
				throw new LWJGLException("There are no supported display modes");
			}
			Display.setDisplayMode(displayMode);
			Display.setFullscreen(false);
			Display.setTitle("2D Lighting");
			Display.setVSyncEnabled(true);
			Display.setResizable(false);
			Display.create(new PixelFormat(0, 16, 1));
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	private void initGL() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, WIDTH, HEIGHT, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
	}

	private void startLoop() throws LWJGLException {
		while (isRunning) {
			glClear(GL_COLOR_BUFFER_BIT);
			
			while (Keyboard.next()) {
				if (Keyboard.getEventKeyState()) {
					if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
						if(Display.isFullscreen()) {
							Display.setFullscreen(false);
						} else {
							clean();
						}
					}
					game.keyTyped(Keyboard.getEventKey());
				}
			}

			game.update();
			game.render();

			Display.update();
			Display.sync(FRAME_RATE);
		}
	}

	private void clean() {
		game.clean();
		Display.destroy();
		System.out.println("Cleaned!");
		System.exit(0);
	}

	public static void main(String[] args) {
		Natives.LoadNatives();
		new Main();
	}
}
