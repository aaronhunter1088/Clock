package v3;

import org.junit.jupiter.api.Test;
import v2.ClockFace;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClockFaceTest {

	@Test
	public void testClockFacesAreDifferent() {
		v2.ClockFace c1 = v2.ClockFace.StartFace;
		v2.ClockFace c2 = v2.ClockFace.AlarmFace;
		assertTrue(c1 != c2);
	}
	
	@Test
	public void testThatWeGetAllValues() {
		List<v2.ClockFace> clockFaces = new ArrayList<>();
		for (v2.ClockFace cf : v2.ClockFace.values()) {
			clockFaces.add(cf);
		}
		// x should be hard coded for future tests
		assertTrue(ClockFace.values().length == clockFaces.size());
	}

}
