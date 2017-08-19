package au.com.digitalspider.webgpio.bean;

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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isActioned() {
		return actioned;
	}

	public void setActioned(boolean actioned) {
		this.actioned = actioned;
	}

	public int getPinId() {
		return pinId;
	}

	public void setPinId(int pinId) {
		this.pinId = pinId;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getEdge() {
		return edge;
	}

	public void setEdge(String edge) {
		this.edge = edge;
	}

}