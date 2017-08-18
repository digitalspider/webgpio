package au.com.digitalspider.webgpio.bean;

public class Esp8266Data {

	private String date;
	private String time;
	private String ipAddress;
	private String heapDump;
	private String data;

	@Override
	public String toString() {
		return "Esp8266Data [date=" + date + ", time=" + time + ", ipAddress=" + ipAddress + ", heapDump=" + heapDump + ", data=" + data + "]";
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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
