package net.dimkonko.lights2d.states;

public abstract class BasicState {
	
	public BasicState() {
		init();
	}
	
	public abstract void init();
	public abstract void update();
	public abstract void render();
	public abstract void clean();

	public void keyTyped(int eventKey) {}
}
