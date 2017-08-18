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
@RequestMapping("/webgpio")
public class WebGpioController {

	@Autowired
	private IEspFileService espFileService;

	@RequestMapping(method = RequestMethod.GET, path = "")
	public String listFiles(Map<String,Object> model) {
		model.put("message","GPIO Page");
		return "webgpio"; // Thymeleaf template
	}

	public void setEspFileService(IEspFileService espFileService) {
		this.espFileService = espFileService;
	}
}
