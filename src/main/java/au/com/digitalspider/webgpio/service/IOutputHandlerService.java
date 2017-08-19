package au.com.digitalspider.webgpio.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import au.com.digitalspider.webgpio.WebgpioConstants;
import au.com.digitalspider.webgpio.handler.AbstractOutputHandler;

public interface IOutputHandlerService {

	public static final String DEFAULT_HANDLER_FILENAME = "handlers.xml";
	public static final String ESP_DATA_DIR_PATH = WebgpioConstants.USER_DIR + File.separator + ".esp8266";
	public static final File ESP_DATA_DIR = new File(ESP_DATA_DIR_PATH);

	public Map<String, List<AbstractOutputHandler>> readHandlerConfigFile() throws Exception;

	public Map<String, List<AbstractOutputHandler>> readHandlerConfigFile(File file) throws Exception;

	public void callHandlers(List<AbstractOutputHandler> handlers);
}
