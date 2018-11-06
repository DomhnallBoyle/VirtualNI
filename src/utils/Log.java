package utils;

import java.util.Date;

public class Log {

	public static void debug(String message) {
		Date d = new Date(System.currentTimeMillis());
		System.out.printf("%s: %s\n", d.toString(), message);
	}

}
