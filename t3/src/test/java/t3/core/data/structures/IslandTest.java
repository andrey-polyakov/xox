package t3.core.data.structures;

import misc.CellUtil;
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
        PartitionedIsland island = new PartitionedIsland();
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

        public PartitionedIsland getIsland() {
            return island.getWestmostPoint();
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
        PartitionedIsland systemUnderTest = a.getIsland();
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
        PartitionedIsland systemUnderTest = a.getIsland();
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
        PartitionedIsland systemUnderTest = a.getIsland();
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
        paradiseIsland[1].mark(WILD_BIRDS);     //_X___
        paradiseIsland[3].mark(WILD_BIRDS);     //_X_X_

        PartitionedIsland systemUnderTest = a.getIsland();                      //0
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

    @Test
    public void fillTheGap2Test() {
        StubAgg a = new StubAgg(5);
        Integer WILD_BIRDS = 66;
        BoardCell[] paradiseIsland = CellUtil.create1x5();
        for (BoardCell cell : paradiseIsland) {
            cell.addAggregator(a);
        }
        paradiseIsland[0].mark(WILD_BIRDS);     //X____
        paradiseIsland[1].mark(WILD_BIRDS);     //XX___
        paradiseIsland[3].mark(WILD_BIRDS);     //XX_X_
        paradiseIsland[4].mark(WILD_BIRDS);     //XX_XX

        PartitionedIsland systemUnderTest = a.getIsland();
        assertNotNull(systemUnderTest.getEast());
        assertNull(systemUnderTest.getWest());
        assertEquals(WILD_BIRDS, systemUnderTest.getBelongsTo());
        assertEquals(2, systemUnderTest.getSize());

        systemUnderTest = a.getIsland().getEast().getEast();
        assertEquals(WILD_BIRDS, systemUnderTest.getBelongsTo());
        assertEquals(2, systemUnderTest.getSize());

        paradiseIsland[2].mark(WILD_BIRDS);     //XXXXX
        systemUnderTest = a.getIsland();
        assertEquals(WILD_BIRDS, systemUnderTest.getBelongsTo());
        assertEquals(5, systemUnderTest.getSize());
        assertNull(systemUnderTest.getEast());
    }

    @Test
    public void fillTheGapTest() {
        StubAgg a = new StubAgg(5);
        Integer WILD_BIRDS = 66;
        BoardCell[] paradiseIsland = CellUtil.create1x5();
        for (BoardCell cell : paradiseIsland) {
            cell.addAggregator(a);
        }
        paradiseIsland[1].mark(WILD_BIRDS);     //_X___
        paradiseIsland[3].mark(WILD_BIRDS);     //_X_X_
        paradiseIsland[4].mark(WILD_BIRDS);     //_X_XX

        PartitionedIsland systemUnderTest = a.getIsland();                      //0
        assertNotNull(systemUnderTest.getEast());
        assertNull(systemUnderTest.getWest());
        assertEquals(null, systemUnderTest.getBelongsTo());

        systemUnderTest = systemUnderTest.getEast();                            //1   X
        assertEquals(WILD_BIRDS, systemUnderTest.getBelongsTo());
        assertEquals(1, systemUnderTest.getSize());

        systemUnderTest = systemUnderTest.getEast();                            //2
        assertEquals(null, systemUnderTest.getBelongsTo());
        assertEquals(1, systemUnderTest.getSize());

        systemUnderTest = a.getIsland();
        paradiseIsland[2].mark(WILD_BIRDS);     //_XXXX
        assertEquals(null, systemUnderTest.getBelongsTo());
        assertEquals(1, systemUnderTest.getSize());

        systemUnderTest = a.getIsland().getEast();                            //XXX
        assertEquals(WILD_BIRDS, systemUnderTest.getBelongsTo());
        assertEquals(4, systemUnderTest.getSize());
    }

    @Test
    public void advancedTestII() {
        StubAgg a = new StubAgg(5);
        Integer WILD_BIRDS = 66;
        BoardCell[] paradiseIsland = CellUtil.create1x5();
        for (BoardCell cell : paradiseIsland) {
            cell.addAggregator(a);
        }
        paradiseIsland[0].mark(WILD_BIRDS);     //X____
        paradiseIsland[2].mark(WILD_BIRDS);     //X_X__

        PartitionedIsland systemUnderTest = a.getIsland();                      //0   X
        assertNotNull(systemUnderTest.getEast());
        assertNull(systemUnderTest.getWest());
        assertEquals(WILD_BIRDS, systemUnderTest.getBelongsTo());

        systemUnderTest = systemUnderTest.getEast();                            //1   _
        assertEquals(null, systemUnderTest.getBelongsTo());
        assertEquals(1, systemUnderTest.getSize());

        systemUnderTest = systemUnderTest.getEast();                            //2   X
        assertEquals(WILD_BIRDS, systemUnderTest.getBelongsTo());
        assertEquals(1, systemUnderTest.getSize());

        systemUnderTest = systemUnderTest.getEast();                            //3   __
        assertEquals(null, systemUnderTest.getBelongsTo());
        assertEquals(2, systemUnderTest.getSize());
        assertNull(systemUnderTest.getEast());

        paradiseIsland[4].mark(WILD_BIRDS);     //X_X_X

        systemUnderTest = systemUnderTest.getEast();                            //3   X
        assertEquals(WILD_BIRDS, systemUnderTest.getBelongsTo());
        assertEquals(1, systemUnderTest.getSize());
        assertNull(systemUnderTest.getEast());
    }

}
