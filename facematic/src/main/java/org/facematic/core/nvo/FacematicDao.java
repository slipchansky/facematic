package org.facematic.core.nvo;

import java.util.Collection;

public interface FacematicDao<BEANTYPE> {
	public BEANTYPE newInstance ();
	public Collection<BEANTYPE> retrieve ();
	public BEANTYPE fetch  (BEANTYPE bean);
	public boolean  create (BEANTYPE bean);
	public boolean  update (BEANTYPE bean);
	public boolean  delete (BEANTYPE bean);
}
