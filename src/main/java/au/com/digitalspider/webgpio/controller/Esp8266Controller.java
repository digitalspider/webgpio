package au.com.digitalspider.webgpio.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.digitalspider.webgpio.WebgpioConstants;
import au.com.digitalspider.webgpio.bean.Esp8266Data;
import au.com.digitalspider.webgpio.service.IEspFileService;

@Controller
@RequestMapping("/esp8266")
public class Esp8266Controller {

	@Autowired
	private IEspFileService espFileService;

	@RequestMapping(method = RequestMethod.GET, path = "")
	public String listFiles(Map<String,Object> model) {
		model.put("message","ESP8266 Page");
		return "esp8266"; // Thymeleaf template
	}

	@RequestMapping(method = RequestMethod.GET, path = "/r/@chipId{/@date}")
	public @ResponseBody List<Esp8266Data> read(
			@PathParam(value = "chipId") String chipId,
			@PathParam(value = "date") String date,
			Map<String,Object> model) throws Exception {

		if (StringUtils.isEmpty(chipId)) {
			throw new Exception("<h1>Please provide chipId</h1>");
		}
		if (StringUtils.isEmpty(date)) {
			date = WebgpioConstants.dateFormatYYYYMMDD.format(new Date());
		}

		String chipName = chipId + "_" + date;
		FileSystemResource resource = new FileSystemResource(WebgpioConstants.ESP_DATA_DIR + "/" + chipName);
		if (resource == null || !resource.exists()) {
			throw new Exception("<h1>Chip name does not exist " + chipName + "</h1>");
		}
		List<Esp8266Data> data = espFileService.readFile(resource.getFile());
		model.put("message","Data has been read");
		return data;
	}

	@RequestMapping(method = RequestMethod.GET, path = "/w/@chipId/@type/@heapDump/@data")
	public @ResponseBody void write(
			@PathParam(value = "chipId") String chipId,
			@PathParam(value = "type") String type,
			@PathParam(value = "heapDump") String heapDump,
			@PathParam(value = "data") String data,
			HttpServletRequest request) throws Exception {

		String date = WebgpioConstants.dateFormatYYYYMMDD.format(new Date());
		String time = WebgpioConstants.dateFormatHHMM.format(new Date());

		Esp8266Data espData = new Esp8266Data();
		espData.setDate(date);
		espData.setTime(time);
		espData.setIpAddress(request.getRemoteAddr());
		espData.setValue(data);
		espData.setHeapDump(heapDump);

		String chipName = chipId + "_" + date;
		String fileName = WebgpioConstants.ESP_DATA_DIR + "/" + chipName;
		Resource resource = new FileSystemResource(fileName);
		if (!resource.exists()) {
			resource.createRelative(".");
		}
		espFileService.writeToFile(resource.getFile(), espData);
	}

	public void setEspFileService(IEspFileService espFileService) {
		this.espFileService = espFileService;
	}
}
