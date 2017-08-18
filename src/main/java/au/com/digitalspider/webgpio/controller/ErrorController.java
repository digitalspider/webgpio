package au.com.digitalspider.webgpio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/error")
public class ErrorController {

	@RequestMapping(method = RequestMethod.GET, path = "")
	public String handleNotFound() {
		return "The page you request could not be found!";
	}
}
