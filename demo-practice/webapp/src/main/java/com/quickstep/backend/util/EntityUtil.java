package com.quickstep.backend.util;

import java.awt.*;

public final class EntityUtil {

	public static final String SEQUENCE_NAME = "quickstep_sequence";
	public static final int SEQUENCE_ALLOCATION_SIZE = 100;

	public static final String getName(Class type) {
		return type.getSimpleName();
	}

	public static String toHex(Color c)
	{
		String hex = Integer.toHexString(c.getRGB() & 0xffffff);
		while(hex.length() < 6){
			hex = "0" + hex;
		}
		return "#" + hex;
	}
}
