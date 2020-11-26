package v1;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ClockFaceTest {

	@Test
	public void testClockFacesAreDifferent() {
		ClockFace c1 = ClockFace.StartFace;
		ClockFace c2 = ClockFace.AlarmFace;
		assertTrue("Differences found", c1 != c2);
	}
	
	@Test
	public void testThatWeGetAllValues() {
		List<ClockFace> clockFaces = new ArrayList<>();
		for (ClockFace cf : ClockFace.values()) {
			clockFaces.add(cf);
		}
		// x should be hard coded for future tests
		assertTrue("Expected " + ClockFace.values().length + " but got another size!", 
			   ClockFace.values().length == clockFaces.size());
	}

}
