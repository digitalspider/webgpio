package au.com.digitalspider.webgpio.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.digitalspider.webgpio.Util;
import au.com.digitalspider.webgpio.WebgpioConstants;
import au.com.digitalspider.webgpio.bean.Esp8266Data;
import au.com.digitalspider.webgpio.handler.AbstractOutputHandler;
import au.com.digitalspider.webgpio.service.IEspFileService;

@Controller
@RequestMapping("/esp8266")
public class Esp8266Controller implements InitializingBean {

	private static final Logger LOG = Logger.getLogger(Esp8266Controller.class);

	@Autowired
	private IEspFileService espFileService;

	private Map<String, List<AbstractOutputHandler>> handlersMap = new HashMap<>();

	@RequestMapping(method = RequestMethod.GET, path = "")
	public String listFiles(Map<String, Object> model) {
		model.put("message", "ESP8266 Page");
		model.put("baseUrl", "/esp8266");
		try {
			List<String> filenames = espFileService.listFiles(null);
			model.put("filenames", filenames);
		} catch (IOException e) {
			LOG.error(e, e);
		}
		return "esp8266"; // Thymeleaf template
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{chipId}")
	public String listFiles(
			@PathVariable(value = "chipId") String chipId,
			Map<String, Object> model) {
		model.put("message", "ESP8266 Page");
		model.put("baseUrl", "/esp8266");
		try {
			List<String> filenames = espFileService.listFiles(chipId);
			model.put("filenames", filenames);
		} catch (IOException e) {
			LOG.error(e, e);
		}
		return "esp8266"; // Thymeleaf template		
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{chipId}/{filename}")
	public @ResponseBody List<Esp8266Data> read(
			@PathVariable(value = "chipId") String chipId,
			@PathVariable(value = "filename") String filename,
			Map<String, Object> model) throws Exception {

		if (StringUtils.isEmpty(chipId)) {
			throw new Exception("<h1>Please provide chipId</h1>");
		}

		FileSystemResource resource = new FileSystemResource(IEspFileService.ESP_DATA_DIR_PATH + File.separator + filename + ".txt");
		if (resource == null || !resource.exists()) {
			throw new Exception("<h1>Chip name does not exist " + filename + "</h1>");
		}
		List<Esp8266Data> data = espFileService.readFile(resource.getFile());
		model.put("message", "Data has been read");
		return data;
	}

	@RequestMapping(method = RequestMethod.GET, path = "/w/{chipId}/{type}/{heapDump}/{data}")
	public @ResponseBody void write(
			@PathVariable(value = "chipId") String chipId,
			@PathVariable(value = "type") String type,
			@PathVariable(value = "heapDump") String heapDump,
			@PathVariable(value = "data") String data,
			HttpServletRequest request) throws Exception {

		String date = WebgpioConstants.dateFormatYYYYMMDD.format(new Date());
		String time = WebgpioConstants.dateFormatHHMMSS.format(new Date());

		Esp8266Data espData = new Esp8266Data();
		espData.setDate(date);
		espData.setTime(time);
		espData.setIpAddress(request.getRemoteAddr());
		espData.setValue(data);
		espData.setHeapDump(heapDump);

		String fileName = espFileService.getFileName(chipId);
		Resource resource = new FileSystemResource(fileName);
		if (!resource.exists()) {
			resource.createRelative(".");
		}
		espFileService.writeToFile(resource.getFile(), espData);

		for (AbstractOutputHandler handler : handlersMap.get(type)) {
			//handler.call();
		}
	}

	public void setEspFileService(IEspFileService espFileService) {
		this.espFileService = espFileService;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Resource resource = new FileSystemResource("handlers.xml");
		if (resource != null && resource.exists()) {
			handlersMap = Util.readHandlerConfigFile(resource.getFile());
		}
	}
}
