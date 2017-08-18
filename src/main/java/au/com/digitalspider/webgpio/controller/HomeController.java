package au.com.digitalspider.webgpio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class HomeController {

	@RequestMapping(method = RequestMethod.GET, path = "")
	public String index() {
		return "<h4><a href='/'>Home</a></h4>&nbsp;|&nbsp;" +
				"<h4><a href='/'>WebGpio</a></h4>&nbsp;|&nbsp;" +
				"<h4><a href='/'>WebI2C</a></h4>&nbsp;|&nbsp;" +
				"<h4><a href='/esp8266'>Esp8266</a></h4>";
	}
}
