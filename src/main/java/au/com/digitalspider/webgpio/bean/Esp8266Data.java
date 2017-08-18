package au.com.digitalspider.webgpio.bean;

import java.util.List;
import java.util.ArrayList;

public class Esp8266Data {

	private String date;
	private String time;
	private String ipAddress;
	private String heapDump;
	private String value;
	private List<String> values = new ArrayList<>();

	@Override
	public String toString() {
		return "Esp8266Data [date=" + date + ", time=" + time + ", ipAddress=" + ipAddress + ", heapDump=" + heapDump + ", value=" + value + "]";
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getHeapDump() {
		return heapDump;
	}

	public void setHeapDump(String heapDump) {
		this.heapDump = heapDump;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

}
