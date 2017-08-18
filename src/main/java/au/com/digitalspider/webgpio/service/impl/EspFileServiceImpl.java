package au.com.digitalspider.webgpio.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import au.com.digitalspider.webgpio.WebgpioConstants;
import au.com.digitalspider.webgpio.bean.Esp8266Data;
import au.com.digitalspider.webgpio.service.IEspFileService;
import au.com.digitalspider.webgpio.util.SuffixFilenameFilter;

/**
 * Convert content to and from file 20170818_670978_raw.txt (date_chipId)
 *
 * A line looks like: 20170818|201141|192.168.1.117|28432|0.987
 * This is <YYYYDDMM>|<HHmm>|<ipAddress>|<heapDump>|<data>
 *
 * @author davidv
 */
@Component
public class EspFileServiceImpl implements IEspFileService {

	private static final Logger LOG = Logger.getLogger(EspFileServiceImpl.class);

	@Override
	public String getEspDataDir() {
		String espHomeDir = System.getProperty("esp.home");
		if (StringUtils.isEmpty(espHomeDir)) {
			ESP_DATA_DIR.mkdirs();
			espHomeDir = ESP_DATA_DIR.getAbsolutePath();
		}
		if (!espHomeDir.endsWith(File.separator)) {
			espHomeDir += File.separator;
		}
		return espHomeDir;
	}

	@Override
	public List<String> listFiles(String chipId) throws IOException {
		List<String> filenames = new ArrayList<>();
		if (DATA_FILES.isEmpty()) {
			reloadEspDiretory();
		}
		for (File file : DATA_FILES.values()) {
			if (StringUtils.isEmpty(chipId) || (StringUtils.isNotEmpty(chipId) && file.getName().contains(chipId))) {
				filenames.add(file.getName());
			}
		}
		return filenames;
	}

	@Override
	public void reloadEspDiretory() {
		LOG.info("reloadEspDiretory() START");
		// Initialise file system ~/.esp8266
		ESP_DATA_DIR.mkdirs();
		File[] files = ESP_DATA_DIR.listFiles(new SuffixFilenameFilter(FILESUFFIX_DATA));
		for (File file : files) {
			String filename = file.getName();
			DATA_FILES.put(filename, file);
		}
		LOG.info("reloadEspDiretory(). DONE. files=" + DATA_FILES.size());
	}

	@Override
	public String getFileName(String chipId) {
		return getFileName(null, chipId);
	}

	@Override
	public String getFileName(Date date, String chipId) {
		if (date == null) {
			date = new Date();
		}
		String dateString = WebgpioConstants.dateFormatYYYYMMDD.format(date);
		String fileName = dateString + "_" + chipId + "_raw.txt";
		return fileName;
	}

	@Override
	public List<Esp8266Data> readFile(File file) throws FileNotFoundException, IOException {
		List<Esp8266Data> data = new ArrayList<>();
		try (FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader)) {
			String line = bufferedReader.readLine();

			Esp8266Data espData = mapLineToData(line);
			if (espData != null) {
				data.add(espData);
			}
		}
		return data;
	}

	private List<Esp8266Data> mapLinesToDataPoints(List<String> rawContentList) {
		List<Esp8266Data> dataPoints = new ArrayList<Esp8266Data>();
		for (String rawContent : rawContentList) {
			dataPoints.add(mapLineToData(rawContent));
		}
		return dataPoints;
	}

	@Override
	public Esp8266Data mapLineToData(String line) {
		if (!StringUtils.isEmpty(line)) {
			Esp8266Data espData = new Esp8266Data();
			String[] lineParts = line.replace("\n", "").split("\\|");
			for (int i = 0; i < lineParts.length; i++) {
				switch (i) {
				case 0:
					espData.setDate(lineParts[i]);
					break;
				case 1:
					espData.setTime(lineParts[i]);
					break;
				case 2:
					espData.setIpAddress(lineParts[i]);
					break;
				case 3:
					espData.setHeapDump(lineParts[i]);
					break;
				case 4:
					espData.setValue(lineParts[i]);
					break;
				}
				if (i > 4) {
					espData.getValues().add(lineParts[i]);
				}
			}
		}
		return null;
	}

	@Override
	public void writeToFile(File file, Esp8266Data data) throws IOException {
		if (file == null) {
			throw new IOException("File cannot be null, and must exist. " + file);
		}
		if (data == null) {
			throw new IOException("Cannot write null data to file " + file);
		}
		if (!file.exists()) {
			LOG.info("File does not exist. Tying to create " + file);
			file.createNewFile();
		}
		if (!file.canWrite()) {
			throw new IOException("Cannot write to file " + file);
		}
		String date = WebgpioConstants.dateFormatYYYYMMDD.format(new Date());
		String time = WebgpioConstants.dateFormatHHMMSS.format(new Date());
		String writeData = date + DELIM + time + DELIM + data.getIpAddress() + DELIM + data.getHeapDump() + DELIM + data.getValue();
		for (String value : data.getValues()) {
			if (StringUtils.isNotBlank(value)) {
				writeData += DELIM + value;
			}
		}
		try (FileWriter fileWriter = new FileWriter(file, true)) {
			fileWriter.append(writeData + "\n");
		}
	}

}