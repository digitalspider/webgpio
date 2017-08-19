package au.com.digitalspider.webgpio;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class WebgpioConstants {
	public static final String USER_DIR = System.getProperty("user.home");
	public static final String OS_NAME = System.getProperty("os.name");
	public static final boolean isWindows = OS_NAME.toLowerCase().startsWith("win");
	public static final DateFormat dateFormatYYYY_MM_DD = new SimpleDateFormat("YYYY-MM-dd");
	public static final DateFormat dateFormatYYYYMMDD = new SimpleDateFormat("YYYYMMdd");
	public static final DateFormat dateFormatHHMM = new SimpleDateFormat("HHmm");
	public static final DateFormat dateFormatHHMMSS = new SimpleDateFormat("HHmmss");
	public static final DateFormat dateFormatDD_MM_YYYY = new SimpleDateFormat("dd/MM/YYYY");

}
