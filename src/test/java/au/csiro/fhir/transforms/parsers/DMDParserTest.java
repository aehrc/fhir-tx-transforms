package au.csiro.fhir.transforms.parsers;

import static org.junit.Assert.*;

import org.junit.Test;

public class DMDParserTest {

	@Test
	public void testEnumFindByName() {
		assertEquals(LookUpTag.BASIS_OF_NAME,LookUpTag.findByName("BASIS_OF_NAME"));
		assertEquals(LookUpTag.COMBINATION_PACK_IND,LookUpTag.findByName("COMBINATION_PACK_IND"));
		assertNull(LookUpTag.findByName("BASIS_OF_"));
		
	}

}
