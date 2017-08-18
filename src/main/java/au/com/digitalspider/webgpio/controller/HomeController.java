package au.com.digitalspider.webgpio.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

	// inject via application.properties
	@Value("${welcome.message:test}")
	private String message = "Hello World";

	@RequestMapping(method = RequestMethod.GET, path = "/")
	public String index(Map<String, Object> model) {
		model.put("message", this.message);
		return "welcome"; // Thymeleaf template
	}
}
