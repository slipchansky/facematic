package org.test.ui.testbean;

import java.util.Collection;

import org.facematic.utils.FacematicBeanUtils;
import org.test.beans.TestBean;

public class TestBeanDaoFake extends TestBeanDao {
	@Override
	public TestBean newInstance() {
		return new TestBean ();
	}
	
	public boolean create (TestBean bean) {
		return true;
	}
	
	@SuppressWarnings("unchecked")
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
