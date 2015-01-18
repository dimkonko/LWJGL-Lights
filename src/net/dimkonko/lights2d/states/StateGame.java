package net.dimkonko.lights2d.states;

import static org.lwjgl.opengl.GL11.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_KEEP;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_REPLACE;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColorMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glStencilFunc;
import static org.lwjgl.opengl.GL11.glStencilOp;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;

import java.util.ArrayList;

import net.dimkonko.lights2d.Main;
import net.dimkonko.lights2d.obj.Block;
import net.dimkonko.lights2d.obj.Light;
import net.dimkonko.lights2d.utils.Shader;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

public class StateGame extends BasicState {

	private Shader shader;

	private Light handLight;
	
	public ArrayList<Light> lights = new ArrayList<Light>();
	public ArrayList<Block> blocks = new ArrayList<Block>();
	
	public StateGame() {
		setUpObjects();
	}

	@Override
	public void init() {
		shader = new Shader();
		shader.loadFragmentShader("res/shaders/shader.frag");
		shader.compile();

		glEnable(GL_STENCIL_TEST);
		glClearColor(0, 0, 0, 0);
	}

	private void setUpObjects() {
		int lightCount = 4;
		int blockCount = 5 + (int) (Math.random() * 1);

		// Lights
		for (int i = 1; i <= lightCount; i++) {
			Vector2f location = new Vector2f(
					(float) Math.random() * Main.WIDTH, 
					(float) Math.random() * Main.HEIGHT);
			lights.add(new Light(location, 
					(float) Math.random() * 10,
					(float) Math.random() * 10, 
					(float) Math.random() * 10,
					50
			));
		}
		
		handLight = new Light(new Vector2f(0, 0), 10, 2, 0, 50);
		lights.add(handLight);

		// Blocks
		for (int i = 1; i <= blockCount; i++) {
			int width = 50;
			int height = 50;
			int x = (int) (Math.random() * (Main.WIDTH - width));
			int y = (int) (Math.random() * (Main.HEIGHT - height));
			blocks.add(new Block(x, y, width, height));
		}
	}

	@Override
	public void update() {
		int mx = Mouse.getX();
		int my = Main.HEIGHT - Mouse.getY();
		
		handLight.setLocation(new Vector2f(mx, my));
	}

	@Override
	public void render() {
		for (Light light : lights) {
			glColorMask(false, false, false, false);
			glStencilFunc(GL_ALWAYS, 1, 1);
			glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);

			for (Block block : blocks) {
				Vector2f[] vertices = block.getVertices();
				for (int i = 0; i < vertices.length; i++) {
					Vector2f currentVertex = vertices[i];
					Vector2f nextVertex = vertices[(i + 1) % vertices.length];
					Vector2f edge = Vector2f.sub(nextVertex, currentVertex, null);
					Vector2f normal = new Vector2f(edge.getY(), -edge.getX());
					Vector2f lightToCurrent = Vector2f.sub(currentVertex,
							light.location, null);
					if (Vector2f.dot(normal, lightToCurrent) > 0) {
						Vector2f point1 = Vector2f.add(
								currentVertex,
								(Vector2f) Vector2f.sub(currentVertex, light.location, null).
								scale(800), 
								null
								);
						Vector2f point2 = Vector2f.add(
								nextVertex,
								(Vector2f) Vector2f.sub(nextVertex, light.location, null).
								scale(800), 
								null
								);
						glBegin(GL_QUADS);
						{
							glVertex2f(currentVertex.getX(), currentVertex.getY());
							glVertex2f(point1.getX(), point1.getY());
							glVertex2f(point2.getX(), point2.getY());
							glVertex2f(nextVertex.getX(), nextVertex.getY());
						}
						glEnd();
					}
				}
			}

			glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
			glStencilFunc(GL_EQUAL, 0, 1);
			glColorMask(true, true, true, true);

			shader.useProgram();
			glUniform2f(
					glGetUniformLocation(shader.getProgram(), "lightLocation"),
					light.location.getX(), Main.HEIGHT - light.location.getY());
			glUniform3f(
					glGetUniformLocation(shader.getProgram(), "lightColor"),
					light.red, light.green, light.blue);
			
			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE);

			glBegin(GL_QUADS);
			{
				glVertex2f(0, 0);
				glVertex2f(0, Main.HEIGHT);
				glVertex2f(Main.WIDTH, Main.HEIGHT);
				glVertex2f(Main.WIDTH, 0);
			}
			glEnd();

			glDisable(GL_BLEND);
			shader.unUse();
			glClear(GL_STENCIL_BUFFER_BIT);
		}

		glColor3f(0.05f, 0.05f, 0.05f);
		for (Block block : blocks) {
			glBegin(GL_QUADS);
			{
				for (Vector2f vertex : block.getVertices()) {
					glVertex2f(vertex.getX(), vertex.getY());
				}
			}
			glEnd();
		}
	}
	
	@Override
	public void keyTyped(int key) {
		if(key == Keyboard.KEY_SPACE) {
			lights.clear();
			blocks.clear();
			setUpObjects();
			return;
		}
	}
		

	@Override
	public void clean() {
		shader.clean();
	}
}
