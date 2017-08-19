package au.com.digitalspider.webgpio.handler;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import au.com.digitalspider.webgpio.Util;
import au.com.digitalspider.webgpio.bean.Esp8266Data;
import au.com.digitalspider.webgpio.service.IEspFileService;

public abstract class AbstractOutputHandler implements Callable<String> {

	public static final DateFormat DFDAY = new SimpleDateFormat("YYYYMMdd");
	public static final DateFormat DFTIM = new SimpleDateFormat("HHmmss");

	public static final String PARAM_TEMPLATE = "velocity.template";
	public static final String PARAM_FILESUFFIX = "file.suffix";

	protected final String DEFAULT_TEMPLATE = "outputRawTemplate.vm";
	protected String DEFAULT_FILESUFFIX = "_raw.txt";

	protected Logger log = Logger.getLogger(getClass());
	protected Properties properties = new Properties();
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected String name;
	protected VelocityEngine ve;
	protected String ipAddress;
	protected String chipId;
	protected String type;
	private Esp8266Data data;

	public void init(HttpServletRequest request, HttpServletResponse response, VelocityEngine ve, String ipAddress, String chipId) {
		this.request = request;
		this.response = response;
		this.ve = ve;
		this.ipAddress = ipAddress;
		this.chipId = chipId;
	}

	protected File getOutputFile(String fileSuffix) throws IOException {
		// Get filename based on todays date
		String day = DFDAY.format(new Date());
		String filename = day + "_" + chipId + fileSuffix;

		File file = new File(IEspFileService.ESP_DATA_DIR, filename);
		if (!file.exists()) {
			IEspFileService.ESP_DATA_DIR.mkdir();
			file.createNewFile();
		}
		return file;
	}

	protected VelocityContext getVelocityContext() {
		Date now = new Date();
		String day = DFDAY.format(now);
		String time = DFTIM.format(now);

		VelocityContext context = Util.getDefaultVelocityContext();
		context.put("date", day);
		context.put("time", time);
		context.put("ip", ipAddress);
		context.put("chipid", chipId);
		context.put("type", type);
		context.put("heap", data.getHeapDump());
		int i = 0;
		for (String value : data.getValues()) {
			if (i == 0) {
				context.put("value", value);
			}
			else {
				context.put("value" + i, value);
			}
		}
		return context;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + name + "]";
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Esp8266Data getData() {
		return data;
	}

	public void setData(Esp8266Data data) {
		this.data = data;
	}
}
