package au.com.digitalspider.webgpio.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;

import au.com.digitalspider.webgpio.Util;
import au.com.digitalspider.webgpio.WebgpioConstants;
import au.com.digitalspider.webgpio.bean.ModelPin;
import au.com.digitalspider.webgpio.service.IEspFileService;

@Controller
@RequestMapping("/webgpio")
public class WebGpioController implements InitializingBean {

	public static final Logger LOG = Logger.getLogger(WebGpioController.class);

	private static boolean initialised = false;

	public enum PinRequest {
		VALUE,
		DIRECTION,
		EDGE,
		TOGGLE,
		PWM
	};

	public enum PinDirection {
		IN,
		OUT
	};

	public enum PinEdge {
		NONE,
		RISING,
		FALLING,
		BOTH
	};

	@Autowired
	private IEspFileService espFileService;

	@RequestMapping(method = RequestMethod.GET, path = "")
	public String listFiles(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		model.put("message", "GPIO Page");
		model.put("baseUrl", Util.getBaseUrl(request));
		try {
			LOG.info("pathinfo=" + request.getPathInfo());
			String[] pathParts = Util.getPathParts(request);
			LOG.info("PathParts=" + pathParts);

			List<ModelPin> modelPins = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				ModelPin pin = new ModelPin();
				pin.setPinId(i + 1);
				pin.setEdge("e");
				pin.setState("s");
				modelPins.add(pin);
			}
			model.put("modelPins", modelPins);

			if (WebgpioConstants.isWindows) {
				return "webgpio"; // Thymeleaf template				
			}

			// Only do the below on linux
			int pin = -1;
			PinRequest pinRequest = null;
			String valueParam = null;
			int pinValue = -1;
			int pinDuty = 512;
			int pinCycle = 1024;
			PinEdge edge = null;
			PinDirection dir = null;
			for (int i = 0; i < pathParts.length; i++) {
				switch (i) {
				case 0:
					try {
						pin = Integer.parseInt(pathParts[i]);
						if (pin < 0 || pin > Gpio.NUM_PINS) {
							throw new Exception("pin value " + pin + " outside of range 0 to " + Gpio.NUM_PINS);
						}
					} catch (Exception e) {
						Util.writeError(request, response, "Invalid pin input: " + e.getMessage());
						return "error";
					}
					break;
				case 1:
					try {
						pinRequest = PinRequest.valueOf(pathParts[i].toUpperCase());
					} catch (Exception e) {
						Util.writeError(request, response, "Invalid pin request input: " + e.getMessage());
						return "error";
					}
					break;
				case 2:
					try {
						valueParam = pathParts[i];
						validateValueParam(pin, pinRequest, valueParam, pathParts);
						switch (pinRequest) {
						case VALUE:
							pinValue = Integer.parseInt(valueParam);
							break;
						case PWM:
							pinDuty = Integer.parseInt(valueParam);
							if (pathParts.length > i) {
								pinCycle = Integer.parseInt(pathParts[i + 1]);
							}
							break;
						case DIRECTION:
							dir = PinDirection.valueOf(valueParam.toUpperCase());
							break;
						case EDGE:
							edge = PinEdge.valueOf(valueParam.toUpperCase());
							break;
						case TOGGLE:
							break;
						}
					} catch (Exception e) {
						Util.writeError(request, response, "Invalid pin request input: " + e.getMessage());
						return "error";
					}
					break;
				default:
					break;
				}
			}

			createPinModel(model, pin, pinRequest, valueParam, pinValue, edge, dir, pinCycle, pinDuty);
		} catch (Exception e) {
			try {
				Util.writeError(request, response, e);
			} catch (IOException e1) {
				LOG.error(e1, e1);
			}
		}
		return "webgpio"; // Thymeleaf template
	}

	private void initPi4j() {
		LOG.info("initPi4j() called");
		if (!initialised) {
			try {
				LOG.info("initPi4j() START");
				GpioController gpio = GpioFactory.getInstance();
				LOG.info("gpio=" + gpio);
				GpioProvider provider = GpioFactory.getDefaultProvider();
				LOG.info("provider=" + provider);
				LOG.info("pins provision start");
				Integer[] includePins = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 10, 11, 21, 22, 24, 26, 27 };
				for (int i = 0; i < includePins.length; i++) {
					try {
						String pinId = "" + includePins[i];
						LOG.info("pinId=" + pinId);
						Pin gpiopin = RaspiPin.getPinByName("GPIO " + pinId);
						LOG.info("pin=" + gpiopin);
						gpio.provisionDigitalOutputPin(gpiopin);
					} catch (Exception e) {
						LOG.error(e, e);
					}
				}
				LOG.info("pins provision done");
				initialised = true;
				LOG.info("initPi4j() DONE");
			} catch (Exception e) {
				LOG.error(e, e);
			}
		}
	}

	private void validateValueParam(int pin, PinRequest pinRequest, String valueParam, String[] pathParts) throws Exception {
		switch (pinRequest) {
		case TOGGLE:
			break;
		case VALUE:
			int value = -1;
			try {
				value = Integer.parseInt(valueParam);
			} catch (Exception e) {
				throw new Exception("Parameter " + pinRequest.name().toLowerCase() + " for pin " + pin + " must be numeric!");
			}
			if (value < 0 || value > 1) {
				throw new Exception("Parameter " + pinRequest.name().toLowerCase() + " for pin " + pin + " must be 0 or 1!");
			}
			break;
		case PWM:
			int duty = -1;
			int cycle = -1;
			try {
				duty = Integer.parseInt(valueParam);
			} catch (Exception e) {
				throw new Exception("Parameter " + pinRequest.name().toLowerCase() + " for pin " + pin + " must be numeric!");
			}
			if (duty < 0 || duty > 4096) {
				throw new Exception("Parameter " + pinRequest.name().toLowerCase() + " for pin " + pin + " must be between 0 and 4096!");
			}
			try {
				if (pathParts.length > 3) {
					cycle = Integer.parseInt(pathParts[3]);
				}
			} catch (Exception e) {
				throw new Exception("Parameter " + pinRequest.name().toLowerCase() + " cycle for pin " + pin + " must be numeric!");
			}
			if (cycle < 0 || cycle > 4096) {
				throw new Exception("Parameter " + pinRequest.name().toLowerCase() + " cycle for pin " + pin + " must be between 0 and 4096!");
			}
			break;
		case DIRECTION:
			try {
				PinDirection dir = PinDirection.valueOf(valueParam.toUpperCase());
				if (dir == null) {
					throw new Exception("Parameter " + pinRequest.name().toLowerCase() + " for pin " + pin + " must be in or out!");
				}
			} catch (Exception e) {
				throw new Exception("Parameter " + pinRequest.name().toLowerCase() + " for pin " + pin + " must be in or out!");
			}
			break;
		case EDGE:
			try {
				PinEdge edge = PinEdge.valueOf(valueParam.toUpperCase());
				if (edge == null) {
					throw new Exception("Parameter " + pinRequest.name().toLowerCase() + " for pin " + pin + " must be rising,falling or both!");
				}
			} catch (Exception e) {
				throw new Exception("Parameter " + pinRequest.name().toLowerCase() + " for pin " + pin + " must be rising,falling or both!");
			}
			break;
		default:
			throw new Exception("Parameter " + pinRequest.name().toLowerCase() + " for pin " + pin + " is INVALID!");
		}
	}

	private void createPinModel(Map<String, Object> model, int pin, PinRequest pinRequest, String valueParam, int pinValue, PinEdge edge, PinDirection dir, int pinCycle, int pinDuty) throws IOException {
		model.put("pin", pin);
		model.put("pinRequest", pinRequest);

		String action = null;
		if (pinRequest != null) {
			action = pinRequest.name().toLowerCase();
		}
		if (valueParam != null) {
			switch (pinRequest) {
			case TOGGLE:
				break;
			case VALUE:
				action += "=" + pinValue;
				break;
			case PWM:
				action += "=" + pinDuty + "/" + pinCycle;
				break;
			case DIRECTION:
				action += "=" + dir.name().toLowerCase();
				break;
			case EDGE:
				action += "=" + edge.name().toLowerCase();
				break;
			}
			model.put("action", action);
		}
		GpioController gpio = GpioFactory.getInstance();
		GpioProvider provider = GpioFactory.getDefaultProvider();
		Collection<GpioPin> pins = gpio.getProvisionedPins();
		LOG.info("pins=" + pins);

		List<ModelPin> modelPins = new ArrayList<>();

		for (GpioPin gpiopin : pins) {
			ModelPin modelPin = new ModelPin();
			boolean selected = false;
			//LOG.info("gpiopin="+gpiopin);
			//LOG.info("gpiopin.getPin()="+gpiopin.getPin());
			String mode = provider.getMode(gpiopin.getPin()).toString();
			String state = provider.getState(gpiopin.getPin()).toString();
			int pedge = GpioUtil.getEdgeDetection(gpiopin.getPin().getAddress());
			Integer pinId = Integer.parseInt(gpiopin.getPin().getName().substring(5));
			if (pinId == pin) {
				selected = true;
				modelPin.setSelected(true);
			}
			if (selected && pinRequest != null) {
				if (gpiopin instanceof GpioPinDigitalOutput) {
					if (pinRequest == PinRequest.TOGGLE) {
						LOG.info("Toggle");
						((GpioPinDigitalOutput) gpiopin).toggle();
					}
					else if (pinRequest == PinRequest.PWM) {
						LOG.info("pwm duty=" + pinDuty + ", cycle=" + pinCycle);
						int count = 0;
						while (count < pinCycle * 10) {
							int x = ++count % pinCycle;
							if (x < pinDuty) {
								((GpioPinDigitalOutput) gpiopin).high();
							}
							else {
								((GpioPinDigitalOutput) gpiopin).low();
							}
						}
					}
					else if (pinRequest == PinRequest.VALUE && action.contains("=")) {
						PinState newState = PinState.getState(pinValue);
						LOG.info("newState=" + newState);
						gpio.setState(newState, (GpioPinDigitalOutput) gpiopin);
						LOG.info("setState() done");
					}
					state = provider.getState(gpiopin.getPin()).toString();
					;
				}
				modelPin.setActioned(true);
			}
			modelPin.setPinId(pinId);
			modelPin.setMode(mode.replace("PUT", ""));
			modelPin.setState(state);
			modelPin.setEdge(String.valueOf(pedge));
			//if (!gpiopin.equals(RaspiPin.GPIO_00)) {
			//	LOG.info("value="+provider.getValue(gpiopin.getPin()));
			//}
			modelPins.add(modelPin);
		}
		model.put("modelPins", modelPins);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!WebgpioConstants.isWindows) {
			initPi4j();
		}
	}

	public void setEspFileService(IEspFileService espFileService) {
		this.espFileService = espFileService;
	}
}
