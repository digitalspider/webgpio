package au.com.digitalspider.webgpio.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.digitalspider.webgpio.WebgpioConstants;
import au.com.digitalspider.webgpio.bean.Esp8266Data;

public interface IEspFileService {

	public static final String DELIM = "|";
	public static final String ESP_DATA_DIR_PATH = WebgpioConstants.USER_DIR + File.separator + ".esp8266";
	public static final File ESP_DATA_DIR = new File(ESP_DATA_DIR_PATH);
	public static final Map<String, File> DATA_FILES = new HashMap<String, File>();
	public static final String FILESUFFIX_DATA = ".txt";
	public static final int BATCH_SIZE = 1000;
	public static final int DEFAULT_LINES = 1000;

	public void reloadEspDiretory();

	public String getEspDataDir();

	public List<String> listFiles(String chipId) throws IOException;

	public String getFileName(String chipId);

	public String getFileName(Date date, String chipId);

	public Esp8266Data mapLineToData(String line);

	public List<Esp8266Data> readFile(File file) throws FileNotFoundException, IOException;

	public void writeToFile(File file, Esp8266Data data) throws IOException;
}
