package au.com.digitalspider.webgpio.bean;

import java.util.List;
import java.util.ArrayList;

public class ModelPin {

	private boolean selected = false;
	private boolean actioned = false;
	private int pinId;
	private String mode;
	private String state;
	private String edge;

	@Override
	public String toString() {
		return "Pin [pinId=" + pinId + ", mode=" + mode + ", state=" + state + ", edge=" + edge + ", selected=" + selected + ", actioned=" + actioned + "]";
	}

}