package au.com.digitalspider.webgpio.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class EspFileServiceImplTest {

	private EspFileServiceImpl espFileServiceImpl;

	@Before
	public void setUp() {
		espFileServiceImpl = new EspFileServiceImpl();
	}

	@Test
	public void testWrite() {
		assertTrue(true);
		assertNotNull(espFileServiceImpl);
	}

	@Test
	public void testRead() {
		assertFalse(false);
	}
}
