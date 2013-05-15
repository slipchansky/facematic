package org.facematic.core.producer.builders;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.nvo.DefaultUploadReceiver;
import org.facematic.core.nvo.UploadProgressEvent;
import org.facematic.core.producer.FaceProducer;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;

/**
 * Adds listeners to Upload instance
 * @author "Stanislav Lipchansky"
 *
 */
public class UploadBuilder extends AbstractFieldBuilder {
	private static Logger logger = LoggerFactory.getLogger(AbstractFieldBuilder.class);

	@Override
	public Class getBuildingClass() {
		return Upload.class;
	}

	/* Adds listeners to Upload instance
	 * (non-Javadoc)
	 * @see org.facematic.core.producer.builders.AbstractFieldBuilder#build(org.facematic.core.producer.FaceProducer, java.lang.Object, org.dom4j.Element)
	 */
	@Override
	public void build(FaceProducer builder, Object oComponent, Element configuration) {
		super.build(builder, oComponent, configuration);
		Upload upload = (Upload)oComponent;
		
		addOnStartedListenr    (upload, builder, configuration);
		addOnProgressListener  (upload, builder, configuration);
		addOnSucceededListener (upload, builder, configuration);
		addOnFailedListener    (upload, builder, configuration);
		addOnFinishedListener  (upload, builder, configuration);
		
		String saveTo = configuration.attributeValue("saveTo");
		if (saveTo != null) {
			upload.setReceiver(new DefaultUploadReceiver(saveTo));
			logger.trace("Default upload receiver sets to Upload component");
		}
	}

	
	private void addOnFinishedListener(Upload upload, FaceProducer producer, Element configuration) {
		final Method method = getMethod (upload, producer, configuration, "onFinished", StartedEvent.class);
		if (method==null) return;
		final Object controller = producer.getControllerInstance();
		
		upload.addStartedListener(new Upload.StartedListener() {
			@Override
			public void uploadStarted(StartedEvent event) {
				try {
					method.invoke(controller, event);
				} catch (Exception e) {
					logger.error ("Event processing error");
				}
			}
		});
	}


	private void addOnFailedListener(Upload upload, FaceProducer producer, Element configuration) {
		final Method method = getMethod (upload, producer, configuration, "onFailed", FailedEvent.class);
		if (method==null) return;
		final Object controller = producer.getControllerInstance();
		
		upload.addFailedListener(new Upload.FailedListener() {
			
			@Override
			public void uploadFailed(FailedEvent event) {
				try {
					method.invoke(controller, event);
				} catch (Exception e) {
					logger.error ("Event processing error");
				}
			}
		});
		
	}

	private void addOnSucceededListener(Upload upload, FaceProducer producer, Element configuration) {
		final Method method = getMethod (upload, producer, configuration, "onSucceeded", SucceededEvent.class);
		if (method==null) return;
		final Object controller = producer.getControllerInstance();
		
		upload.addSucceededListener(new Upload.SucceededListener() {
		
			@Override
			public void uploadSucceeded(SucceededEvent event) {
				try {
					method.invoke(controller, event);
				} catch (Exception e) {
					logger.error ("Event processing error");
				}
			}
		});
		
	}

	private void addOnProgressListener(Upload upload, FaceProducer producer, Element configuration) {
		final Method method = getMethod (upload, producer, configuration, "onProgress", UploadProgressEvent.class);
		if (method==null) return;
		final Object controller = producer.getControllerInstance();
		
		upload.addProgressListener(new Upload.ProgressListener() {
			
			@Override
			public void updateProgress(long readBytes, long contentLength) {
				try {
					method.invoke(controller, new UploadProgressEvent(readBytes, contentLength));
				} catch (Exception e) {
					logger.error ("Event processing error");
				}
			}
		});
		
	}

	private void addOnStartedListenr(Upload upload, FaceProducer producer, Element configuration) {
		final Method method = getMethod (upload, producer, configuration, "onStarted", StartedEvent.class);
		if (method==null) return;
		final Object controller = producer.getControllerInstance();
		
		upload.addStartedListener(new Upload.StartedListener() {
			@Override
			public void uploadStarted(StartedEvent event) {
				try {
					method.invoke(controller, event);
				} catch (Exception e) {
					logger.error ("Event processing error");
				}
			}
		});
	}
}
