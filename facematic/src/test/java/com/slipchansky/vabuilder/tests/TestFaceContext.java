package com.slipchansky.vabuilder.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.slipchansky.fm.mvc.annotations.FmViewComponent;
import com.slipchansky.fm.mvc.annotations.FmController;
import com.slipchansky.fm.producer.FaceReflectionHelper;



public class TestFaceContext {
	
	public class TestFaceContextBean {
		
		@FmViewComponent(name="content1")
		private String v1;
		
		@FmViewComponent(name="content2")
		private String v2;

		
		@FmViewComponent(name="a.content")
		private String v3;
		
		@FmViewComponent
		private String v4;

		public String getV1() {
			return v1;
		}

		public String getV2() {
			return v2;
		}

		public String getV3() {
			return v3;
		}

		public String getV4() {
			return v4;
		}
	}

	@Test
	public void test() {
		TestFaceContextBean t = new TestFaceContextBean  ();
		FaceReflectionHelper c= new FaceReflectionHelper (t);
		c.putString ("content1", "Mama");
		c.putString ("content2", "Papa");
		c.putString ("a.content", "Baba");
		c.putString ("v4", "Kaka");
		assertEquals(t.getV1(), "Mama");
		assertEquals(t.getV2(), "Papa");
		assertEquals(t.getV3(), "Baba");
		assertEquals(t.getV4(), "Kaka");
		
	}

}
