package t3.core.data.structures;

import org.junit.Test;
import t3.core.data.structures.aggregators.AbstractIncrementalAggregator;

import java.util.stream.Collectors;

import static java.util.stream.IntStream.rangeClosed;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertNotNull;

public class IslandTest {

    @Test
    public void noMansLandTest() {
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
    public void fromTheSidesTest() {
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

    @Test
    public void westToEastTest() {
        StubAgg a = new StubAgg(5);
        Integer GERMAN = 4;
        BoardCell[] czechoslovakia = CellUtil.create1x5();
        for (BoardCell cell : czechoslovakia) {
            cell.addAggregator(a);
        }
        for (BoardCell cell : czechoslovakia) {
            cell.mark(GERMAN);
        }
        IslandPartition systemUnderTest = a.getIsland();
        assertNull(systemUnderTest.getEast());
        assertNull(systemUnderTest.getWest());
        assertEquals(GERMAN, systemUnderTest.getBelongsTo());
        assertEquals(czechoslovakia.length, systemUnderTest.getSize());
    }

    @Test
    public void eastToWestTest() {
        StubAgg a = new StubAgg(5);
        Integer RUSSIAN = 5;
        BoardCell[] prussia = CellUtil.create1x5();
        for (BoardCell cell : prussia) {
            cell.addAggregator(a);
        }
        int size = prussia.length;
        for (BoardCell cell : rangeClosed(1, size).mapToObj(i -> prussia[size - i]).collect(Collectors.toList())) {
            cell.mark(RUSSIAN);
        }
        IslandPartition systemUnderTest = a.getIsland();
        assertNull(systemUnderTest.getEast());
        assertNull(systemUnderTest.getWest());
        assertEquals(RUSSIAN, systemUnderTest.getBelongsTo());
        assertEquals(prussia.length, systemUnderTest.getSize());
    }

    @Test
    public void advancedTest() {
        StubAgg a = new StubAgg(5);
        Integer WILD_BIRDS = 66;
        BoardCell[] paradiseIsland = CellUtil.create1x5();
        for (BoardCell cell : paradiseIsland) {
            cell.addAggregator(a);
        }
        int size = paradiseIsland.length;
        paradiseIsland[1].mark(WILD_BIRDS);     //_X___
        paradiseIsland[3].mark(WILD_BIRDS);     //_X_X_

        IslandPartition systemUnderTest = a.getIsland();                        //0
        assertNotNull(systemUnderTest.getEast());
        assertNull(systemUnderTest.getWest());
        assertEquals(null, systemUnderTest.getBelongsTo());

        systemUnderTest = systemUnderTest.getEast();                            //1   X
        assertEquals(WILD_BIRDS, systemUnderTest.getBelongsTo());
        assertEquals(1, systemUnderTest.getSize());

        systemUnderTest = systemUnderTest.getEast();                            //2
        assertEquals(null, systemUnderTest.getBelongsTo());
        assertEquals(1, systemUnderTest.getSize());

        systemUnderTest = systemUnderTest.getEast();                            //3   X
        assertEquals(WILD_BIRDS, systemUnderTest.getBelongsTo());
        assertEquals(1, systemUnderTest.getSize());

        systemUnderTest = systemUnderTest.getEast();                            //4
        assertEquals(null, systemUnderTest.getBelongsTo());
        assertEquals(1, systemUnderTest.getSize());
    }

}
