package net.dimkonko.lights2d.obj;

import org.lwjgl.util.vector.Vector2f;

public class Light {
	public Vector2f location;
	public float red;
	public float green;
	public float blue;
	private float radius;

	public Light(Vector2f location, float red, float green, float blue, float radius) {
		this.location = location;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public void setLocation(Vector2f location) {
		this.location = location;
	}
	
	public Vector2f getLocation() {
		return location;
	}
	
	public float getRadius() {
		return radius;
	}
}
