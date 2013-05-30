package org.test.ui.testbean;

import java.util.Collection;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.nvo.FacematicDao;
import org.facematic.utils.FacematicBeanUtils;
import org.test.beans.TestBean;

public class TestBeanDao implements FacematicDao<TestBean> {
	private static final Logger logger = LoggerFactory.getLogger(TestBeanDao.class);
	
	//@Inject 
	//TestBeanFacade testService;
	

	@Override
	public TestBean newInstance() {
		return new TestBean ();
	}
	
	public boolean create (TestBean bean) {
		return true;
	}
	
	@Override
	public Collection<TestBean> retrieve() {
		return FacematicBeanUtils.getFakeDataList(TestBean.class, 5);
	}

	@Override
	public TestBean fetch(TestBean bean) {
		return bean;
	}

	@Override
	public boolean update(TestBean bean) {
		return true;
	}

	@Override
	public boolean delete(TestBean bean) {
		return true;
	}
 }
