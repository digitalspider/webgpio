package au.com.digitalspider.webgpio.bean;

public class Esp8266Data {

	private String chipId;
	private String heapDump;
	private String data;
	private String type;

	@Override
	public String toString() {
		return "Esp8266Data [chipId=" + chipId + ", heapDump=" + heapDump + ", type=" + type + ", data=" + data + "]";
	}

	public String getChipId() {
		return chipId;
	}

	public void setChipId(String chipId) {
		this.chipId = chipId;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
