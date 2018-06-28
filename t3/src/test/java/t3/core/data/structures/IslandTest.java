package t3.core.data.structures;

import org.junit.Test;
import t3.core.data.structures.aggregators.AbstractIncrementalAggregator;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertNotNull;

public class IslandTest {

    @Test
    public void devonIslandTest() {
        IslandPartition island = new IslandPartition();
        BoardCell[] cells = CellUtil.create1x5();
        for (BoardCell cell : cells) {
            island.add(cell);
        }
        assertNull(island.getEast());
        assertNull(island.getWest());
        assertNull(island.getBelongsTo());// no inhabitants
        assertEquals(cells.length, island.getSize());
    }

    class StubAgg extends AbstractIncrementalAggregator {

        public StubAgg(int winningSegmentSize) {
            super(winningSegmentSize);
        }

        public IslandPartition getIsland() {
            return island;
        }
    }

    @Test
    public void splitCyprusTest() {
        StubAgg a = new StubAgg(5);
        Integer FREE = null;
        Integer TURKS = 1;
        Integer GREEK = 2;
        Integer BRITS = 3;
        BoardCell[] cells = CellUtil.create1x5();
        for (BoardCell cell : cells) {
            cell.addAggregator(a);
        }
        // we start from                                                             _____
        // HEAD
        cells[0].mark(TURKS);//                                                      X____
        IslandPartition systemUnderTest = a.getIsland();
        assertNotNull(systemUnderTest.getEast());
        assertNull(systemUnderTest.getWest());
        assertEquals(TURKS, systemUnderTest.getBelongsTo());
        assertEquals(FREE, systemUnderTest.getEast().getBelongsTo());
        assertEquals(cells.length - 1, systemUnderTest.getEast().getSize());
        assertEquals(1, systemUnderTest.getSize());
        // TAIL
        cells[4].mark(GREEK);//                                                      X___O
        systemUnderTest = a.getIsland().getEast();// second partition
        assertNotNull(systemUnderTest);
        assertNotNull(systemUnderTest.getWest());
        assertEquals(cells.length - 2, systemUnderTest.getSize());


        systemUnderTest = systemUnderTest.getEast();// third partition
        assertNotNull(systemUnderTest);
        assertNotNull(systemUnderTest.getWest());
        assertNull(systemUnderTest.getEast());

        assertEquals(GREEK, systemUnderTest.getBelongsTo());
        assertEquals(1, systemUnderTest.getSize());
    }

}
