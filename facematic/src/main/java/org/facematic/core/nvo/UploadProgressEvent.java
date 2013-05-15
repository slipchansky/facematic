package org.facematic.core.nvo;

/**
 * For unification event processing model.
 * Base upload progress listener receives two longs instead of one event. So we arrange this issue...
 * @see org.facematic.core.producer.builders.UploadBuilder 
 * 
 * @author "Stanislav Lipchansky"
 *
 */
public class UploadProgressEvent {
	private long readBytes;
	private long contentLength;
	
	public UploadProgressEvent(long readBytes, long contentLength) {
		super();
		this.readBytes = readBytes;
		this.contentLength = contentLength;
	}
	
	public long getReadBytes() {
		return readBytes;
	}
	
	public long getContentLength() {
		return contentLength;
	}
	

}
