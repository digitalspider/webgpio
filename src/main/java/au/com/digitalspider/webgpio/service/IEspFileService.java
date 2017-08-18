package au.com.digitalspider.webgpio.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import au.com.digitalspider.webgpio.bean.Esp8266Data;

public interface IEspFileService {

	public List<Esp8266Data> readFile(File file) throws FileNotFoundException, IOException;

	public void writeToFile(File file, Esp8266Data data) throws IOException;
}
