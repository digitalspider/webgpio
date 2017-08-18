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

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import au.com.digitalspider.webgpio.WebgpioConstants;
import au.com.digitalspider.webgpio.bean.Esp8266Data;
import au.com.digitalspider.webgpio.service.IEspFileService;

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

	public String getFileName(String chipId) {
		return getFileName(null, chipId);
	}

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
			if (!StringUtils.isEmpty(line)) {
				Esp8266Data espData = new Esp8266Data();
				String[] dataSet = line.split("|");
				if (dataSet.length == 5) {
					espData.setDate(dataSet[0]);
					espData.setTime(dataSet[1]);
					espData.setIpAddress(dataSet[2]);
					espData.setHeapDump(dataSet[3]);
					espData.setData(dataSet[4]);
					data.add(espData);
				}
			}
		}
		return data;
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
			System.out.println("File does not exist. Tying to create " + file);
			file.createNewFile();
		}
		if (!file.canWrite()) {
			throw new IOException("Cannot write to file " + file);
		}
		String date = WebgpioConstants.dateFormatYYYYMMDD.format(new Date());
		String time = WebgpioConstants.dateFormatHHMM.format(new Date());
		String writeData = date + "|" + time + "|" + data.getIpAddress() + "|" + data.getHeapDump() + "|" + data.getData();
		try (FileWriter fileWriter = new FileWriter(file)) {
			fileWriter.write(writeData);
		}
	}

}