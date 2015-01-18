package net.dimkonko.lights2d.utils;

import java.io.File;

public class Natives {
	
	public static void LoadNatives() {
        System.setProperty("java.library.path", "lib");

        //Extracted from Distributing Your LWJGL Application
        System.setProperty("org.lwjgl.librarypath", new File("lib/native/windows").getAbsolutePath());
	}
}
