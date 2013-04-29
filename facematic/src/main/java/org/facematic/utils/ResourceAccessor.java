package org.facematic.utils;

import java.io.InputStream;

public interface ResourceAccessor {
	public InputStream getResourceStream (String path);
}
