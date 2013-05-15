package org.facematic.core.nvo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.management.RuntimeErrorException;

import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;

import com.vaadin.ui.Upload;

/**
 * Default upload receiver
 * 
 * @author "Stanislav Lipchansky"
 *
 */
public class DefaultUploadReceiver implements Upload.Receiver {
	static final Logger logger = LoggerFactory.getLogger(DefaultUploadReceiver.class);
	private String saveTo;

	public DefaultUploadReceiver (String saveTo) {
		if (saveTo.endsWith("/"))
			saveTo = saveTo.substring(0, saveTo.length()-1);
		this.saveTo = saveTo;
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		if (filename.startsWith("/"))
			filename = filename.substring(1);
		String saveToFileName = saveTo+"/"+filename;
		try {
			return new FileOutputStream(saveToFileName);
		} catch (FileNotFoundException e) {
			logger.error("Could not open destination file for upload: "+saveToFileName, e);
			throw new RuntimeException(e);
		}
	}

}
