package net.dimkonko.lights2d.utils;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Shader {
	
	private int programId;
	private int fragmentShader;
	
	public Shader() {
		programId = glCreateProgram();
		fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
	}
	
	public void loadFragmentShader(String path) {
		StringBuilder fragmentShaderSource = new StringBuilder();

		try {
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(path));
			while ((line = reader.readLine()) != null) {
				fragmentShaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		glShaderSource(fragmentShader, fragmentShaderSource);
	}
	
	public void compile() {
		glCompileShader(fragmentShader);
		if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Fragment shader not compiled!");
		}
		glAttachShader(programId, fragmentShader);
		glLinkProgram(programId);
		glValidateProgram(programId);
	}
	
	public void useProgram() {
		glUseProgram(programId);
	}
	
	public void unUse() {
		glUseProgram(0);
	}
	
	public void clean() {
		glDeleteShader(fragmentShader);
		glDeleteProgram(programId);
	}
	
	public int getProgram() {
		return programId;
	}
}
