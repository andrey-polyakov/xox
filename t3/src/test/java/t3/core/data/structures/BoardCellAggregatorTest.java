package t3.core.data.structures;

import misc.CellUtil;
import org.junit.Before;
import org.junit.Test;
import t3.core.data.structures.aggregators.IncrementalShapeAggregator;
import t3.core.data.structures.aggregators.XOXAggregators;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class BoardCellAggregatorTest {

    private BoardCell[] cells;
    private IncrementalShapeAggregator aggregator;
    private static final BoardCellCoordinates pivot = new BoardCellCoordinates(0, 2);

    /**
     * Boiler plate code associating cells with aggregator
     */
    @Before
    public void setup() {
        cells = CellUtil.create1x5();
    }

    private void attachAggregator(int sizeOfTheSegment) {
        aggregator = XOXAggregators.newColumnAggregator(sizeOfTheSegment);
        for (BoardCell c : cells) {
            c.addAggregator(aggregator);
        }
    }

    @Test
    public void onePlayerVictoryShortSegmentTest() {
        attachAggregator(3);
        Integer winner;
        for (int ii = 0; ii < 2; ii++) {
            cells[ii].mark(1);
        }
        winner = cells[3].mark(1);
        assertNull("No winner", winner);//XX_X_  enough cells but with a gap
        winner = cells[2].mark(1);//    XXXX_  victorious combination

        assertEquals("Winner Id mismatch", Integer.valueOf(1), aggregator.getWinner());
        assertEquals("Unexpected winner", Integer.valueOf(1), winner);
    }

    private void gapSetup(int player2) {
        attachAggregator(3);
        assertNull("Should be no winner yet", cells[0].mark(1));
        assertTrue(aggregator.getWinningCoordinates().isEmpty());
        assertNull("Should be no winner yet", cells[1].mark(1));
        assertEquals("Expected exactly one combination", 1, aggregator.getWinningCoordinates().size());
        assertTrue(aggregator.getWinningCoordinates().get(pivot).contains(1));

        for (int ii = 3; ii < cells.length; ii++) {
            assertNull("Should be no winner yet", cells[ii].mark(player2));
        }
        assertEquals("Expected exactly one combination", 1, aggregator.getWinningCoordinates().size());
    }

    @Test
    public void twoPlayersCanWinTest() {
        gapSetup(2);
        assertEquals(2, aggregator.getWinningCoordinates().get(pivot).size());
        assertTrue(aggregator.getWinningCoordinates().get(pivot).contains(1));
        assertTrue(aggregator.getWinningCoordinates().get(pivot).contains(2));
        assertEquals("Player 1 expected to win", Integer.valueOf(1), cells[2].mark(1));
    }

    @Test
    public void onePlayerFillTheGapTest() {
        gapSetup(1);
        assertEquals(1, aggregator.getWinningCoordinates().get(pivot).size());
        assertTrue(aggregator.getWinningCoordinates().get(pivot).contains(1));
        assertEquals(1, aggregator.getWinningCoordinates().size());
        assertEquals("Player 1 expected to win", Integer.valueOf(1), cells[2].mark(1));
    }

    @Test
    public void onePlayerVictoryTest() {
        attachAggregator(cells.length);
        for (int ii = 0; ii < 4; ii++) {
            cells[ii].mark(1);
        }
        Integer winner = cells[4].mark(1);
        assertEquals("Cells and underlying aggregator mismatch", Integer.valueOf(1), aggregator.getWinner());
        assertEquals("Unexpected winner", Integer.valueOf(1), winner);
    }

    @Test
    public void cellAggregatorNoWinnerTest() {
        attachAggregator(cells.length);
        for (int ii = 0; ii < 3; ii++) {
            cells[ii].mark(1);
        }
        // there is one unmarked cell
        assertFalse(cells[3].isMarked());
        cells[4].mark(2);
    }
}
