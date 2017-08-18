package au.com.digitalspider.webgpio.util;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.lang3.StringUtils;

public class SuffixFilenameFilter implements FilenameFilter {

	private String suffix;

	public SuffixFilenameFilter(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public boolean accept(File dir, String name) {
		if (StringUtils.isNotEmpty(name) && name.endsWith(suffix)) {
			return true;
		}
		return false;
	}

}
