package au.com.digitalspider.webgpio.handler;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.VelocityEngine;

public class RawOutputHandler extends AbstractOutputHandler {

	public static final String DELIM = "|";

	protected String template;
	protected String fileSuffix;

	@Override
	public void init(HttpServletRequest request, HttpServletResponse response, VelocityEngine ve, String ipAddress, String chipId) {
		super.init(request, response, ve, ipAddress, chipId);
		template = properties.getProperty(PARAM_TEMPLATE, DEFAULT_TEMPLATE);
		fileSuffix = properties.getProperty(PARAM_FILESUFFIX, DEFAULT_FILESUFFIX);
	}

	@Override
	public String call() throws Exception {
		log.debug("call() at " + this);

		if (getData() != null) {
			Date now = new Date();
			String day = DFDAY.format(now);
			String time = DFTIM.format(now);

			String result = day + DELIM + time + DELIM + ipAddress + DELIM + getData().getHeapDump() + DELIM + getData().getValue();
			for (String value : getData().getValues()) {
				if (value != null && value.trim().length() > 0) {
					result += DELIM + value;
				}
			}
			log.debug("result=" + result);

			// Get the file requested, or create a new file
			File file = getOutputFile(fileSuffix);
			log.debug("file=" + file.getAbsolutePath());

			try (FileWriter fout = new FileWriter(file, true)) {
				fout.append(result + "\n");
				fout.flush();
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return super.toString() + " template=" + template + " fileSuffix=" + fileSuffix;
	}

}
