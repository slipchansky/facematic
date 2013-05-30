package org.facematic.utils;

import org.facematic.core.nvo.FacematicDao;

public class FacematicDaoUtils {
	
	public static <T extends FacematicDao> T getDao (Class<T> clazz) {
		try {
			return (T) Class.forName (clazz.getCanonicalName()+"Fake").newInstance ();
		} catch (Exception e) {
			return (T)FacematicCdiUtils.getClassInstance (clazz);
		}
	}

}
