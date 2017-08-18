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

@Component
public class EspFileServiceImpl implements IEspFileService {

	public List<Esp8266Data> readFile(File file) throws FileNotFoundException, IOException {
		List<Esp8266Data> data = new ArrayList<>();
		try (FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader)) {
			String line = bufferedReader.readLine();
			if (!StringUtils.isEmpty(line)) {
				Esp8266Data espData = new Esp8266Data();
				String[] dataSet = line.split("|");
				if (dataSet.length == 4) {
					espData.setChipId(dataSet[0]);
					espData.setType(dataSet[1]);
					espData.setHeapDump(dataSet[2]);
					espData.setData(dataSet[3]);
					data.add(espData);
				}
			}
		}
		return data;
	}

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
		String writeData = date + "|" + time + "|" + data.getHeapDump() + "|" + data.getChipId() + "|" + data.getData();
		try (FileWriter fileWriter = new FileWriter(file)) {
			fileWriter.write(writeData);
		}
	}

}