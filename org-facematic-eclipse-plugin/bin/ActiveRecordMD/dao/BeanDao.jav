package ${controllerPackageName};

import java.util.Collection;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.facematic.core.logging.LoggerFactory;
import org.facematic.core.nvo.FacematicDao;
import org.facematic.utils.FacematicBeanUtils;
import ${className};


public class ${daoName} implements FacematicDao<${classSimpleName}> {
	private static final Logger logger = LoggerFactory.getLogger(${daoName}.class);
	
	//@Inject 
	//${classSimpleName}Facade facade;
	

	@Override
	public ${classSimpleName} newInstance() {
		return new ${classSimpleName} ();
	}
	
	public boolean create (${classSimpleName} bean) {
		return true;
	}
	
	@Override
	public Collection<${classSimpleName}> retrieve() {
		return FacematicBeanUtils.getFakeDataList(${classSimpleName}.class, 10);
	}

	@Override
	public ${classSimpleName} fetch(${classSimpleName} bean) {
		return bean;
	}

	@Override
	public boolean update(${classSimpleName} bean) {
		return true;
	}

	@Override
	public boolean delete(${classSimpleName} bean) {
		return true;
	}
 }
