package au.com.digitalspider.webgpio.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import au.com.digitalspider.webgpio.handler.AbstractOutputHandler;
import au.com.digitalspider.webgpio.service.IOutputHandlerService;

/**
 * Read handlers.xml
 */
@Component
public class OutputHandlerServiceImpl implements IOutputHandlerService {

	private static final Logger LOG = Logger.getLogger(OutputHandlerServiceImpl.class);

	// inject via application.properties
	@Value("${handler.filename:test}")
	private String handlerFilename = DEFAULT_HANDLER_FILENAME;

	@Override
	public Map<String, List<AbstractOutputHandler>> readHandlerConfigFile() throws Exception {
		Resource resource = new FileSystemResource(ESP_DATA_DIR_PATH + File.separator + handlerFilename);
		if (resource != null && resource.exists()) {
			Map<String, List<AbstractOutputHandler>> result = readHandlerConfigFile(resource.getFile());
			return result;
		}
		LOG.warn("Could not find file: " + resource + ". Create create this file.");
		return new HashMap<>();
	}

	/**
	 * Static method for reading the handlers.xml config file.
	 *
	 * This class uses reflection to create the <handler> elements of type {@link AbstractOutputHandler}.
	 *
	 * Sample expected content
	 * {@code
	 * <handlers>
	 * 	<sensor type="dist">
	 * 		<handler name="rawoutput" class="au.com.digitalspider.handler.RawOutputHandler">
	 * 			<param name="velocity.template">outputRawTemplate.vm</param>
	 * 			<param name="file.suffix">_raw.txt</param>
	 * 		</handler>
	 * 	</sensor>
	 * </handlers>
	 * }
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, List<AbstractOutputHandler>> readHandlerConfigFile(File file) throws Exception {
		Map<String, List<AbstractOutputHandler>> handlersMap = new HashMap<>();

		SAXReader reader = new SAXReader();
		Document doc = reader.read(file);
		Element root = doc.getRootElement();
		String eleName = root.getName();
		if (eleName == null || !eleName.equals("handlers")) {
			throw new Exception("Invalid XML. Root element is not called <handlers>, but <" + eleName + ">");
		}

		for (Iterator<Element> sensorElements = root.elementIterator(); sensorElements.hasNext();) {
			Element sensorElement = sensorElements.next();
			eleName = sensorElement.getName();
			if (eleName == null || !eleName.equals("sensor")) {
				throw new Exception("Invalid XML. Element is not called <sensor>, but <" + eleName + ">");
			}
			String type = sensorElement.attributeValue("type");

			List<AbstractOutputHandler> handlers = new ArrayList<AbstractOutputHandler>();
			handlersMap.put(type, handlers);

			for (Iterator<Element> handlerElements = sensorElement.elementIterator(); handlerElements.hasNext();) {
				Element handlerElement = handlerElements.next();
				eleName = handlerElement.getName();
				if (eleName == null || !eleName.equals("handler")) {
					throw new Exception("Invalid XML. Element is not called <handler>, but <" + eleName + ">");
				}

				// Get name and class attributes
				String handlerName = null;
				String handlerClassName = null;
				AbstractOutputHandler handler = null;
				for (Iterator<Attribute> handlerAttributes = handlerElement.attributeIterator(); handlerAttributes.hasNext();) {
					Attribute att = handlerAttributes.next();
					if (att.getName().equals("name")) {
						handlerName = att.getValue();
					}
					else if (att.getName().equals("class")) {
						handlerClassName = att.getValue();
					}
				}
				if (handlerName == null || handlerName.trim().length() == 0 || handlerClassName == null || handlerClassName.trim().length() == 0) {
					throw new Exception("handler does not have the required name='' and class='' attributes defined");
				}
				// Create new handler
				Class<?> clazz = Class.forName(handlerClassName);
				handler = (AbstractOutputHandler) clazz.newInstance();
				if (handler == null) {
					throw new Exception("handler name=" + handlerName + ", class=" + handlerClassName + " could not be initialised");
				}
				handler.setName(handlerName);
				handler.setType(type);
				handlers.add(handler);
				LOG.debug("Handler created " + handler);

				for (Iterator<Element> paramElements = handlerElement.elementIterator(); paramElements.hasNext();) {
					Element paramElement = paramElements.next();
					eleName = paramElement.getName();
					if (eleName == null || !eleName.equals("param")) {
						throw new Exception("Invalid XML. Element is not called <param>, but <" + eleName + ">");
					}

					String paramName = paramElement.attributeValue("name");
					String paramValue = paramElement.getStringValue();
					if (paramName != null && paramName.trim().length() > 0 && paramValue != null && paramValue.trim().length() > 0) {
						LOG.debug("  with property " + paramName + "=" + paramValue);
						handler.getProperties().put(paramName, paramValue);
					}
				}
			}
		}
		return handlersMap;
	}

	@Override
	public void callHandlers(List<AbstractOutputHandler> handlers) {
		if (handlers != null) {
			for (AbstractOutputHandler handler : handlers) {
				try {
					LOG.debug("Calling handler " + handler + " START");
					handler.call();
					LOG.debug("Calling handler " + handler + " DONE");
				} catch (Exception e) {
					LOG.error("Error in handler " + handler + ". " + e, e);
				}
			}
		}
	}

}