package t3.core.data.structures;

import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class RoundRobinTest {

    @Test
    public void roundRobinTest() {
        Set<Integer> items = new TreeSet<>();
        items.add(1);
        items.add(2);
        items.add(3);
        RoundRobin systemUnderTest = new RoundRobin(items);
        assertEquals(1, systemUnderTest.passTurn().intValue());
        assertEquals(2, systemUnderTest.passTurn().intValue());
        assertEquals(3, systemUnderTest.passTurn().intValue());
        assertEquals(1, systemUnderTest.passTurn().intValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void roundRobinValidationTest() {
        Set<Integer> items = new TreeSet<>();
        items.add(1);
        new RoundRobin(items);
    }

}
