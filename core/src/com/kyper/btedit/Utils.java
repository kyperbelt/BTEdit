package com.kyper.btedit;

public class Utils {
	
	public static String tab = "    ";
	
	public static String tab(int amount) {
		String r = "";
		for (int i = 0; i < amount; i++) {
			r+=tab;
		}
		return r;
	}
}
