package t3.core.data.structures;

import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;

import static junit.framework.TestCase.assertTrue;

public class RoundRobinTest {

    @Test
    public void roundRobinTest() {
        Set<Integer> items = new TreeSet<>();
        items.add(1);
        items.add(2);
        items.add(3);
        RoundRobin systemUnderTest = new RoundRobin(items);
        RoundRobin.Node one = systemUnderTest.getHead();
        RoundRobin.Node two = one.getNext();
        RoundRobin.Node three = two.getNext();
        assertTrue(one == three);
    }

    @Test(expected = IllegalArgumentException.class)
    public void roundRobinTest2() {
        Set<Integer> items = new TreeSet<>();
        items.add(1);
        new RoundRobin(items);
    }
}
