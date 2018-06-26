package t3.core.data.structures;

import org.junit.Before;
import org.junit.Test;
import t3.core.data.structures.aggregators.IncrementalAggregator;
import t3.core.data.structures.aggregators.XOXAggregators;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class BoardCellAggregatorTest {

    private BoardCell[] cells;
    private IncrementalAggregator aggregator;

    /**
     * Boiler plate code associating cells with aggregator
     */
    @Before
    public void setup() {
        BoardCell[] newCells = {
                new BoardCell(new BoardCellCoordinates(0, 0)),
                new BoardCell(new BoardCellCoordinates(0, 1)),
                new BoardCell(new BoardCellCoordinates(0, 2)),
                new BoardCell(new BoardCellCoordinates(0, 3)),
                new BoardCell(new BoardCellCoordinates(0, 4))
        };
        cells = newCells;
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
        winner = cells[2].mark(1);//       XXXX_  victorious combination

        assertEquals("Winner Id is inconsistent between cells and underlying aggregator",
                Integer.valueOf(1), aggregator.getWinner());
        assertEquals("Unexpected winner", Integer.valueOf(1), winner);
    }

    @Test
    public void twoPlayersCanWinTest() {
        attachAggregator(3);
        for (int ii = 0; ii < 2; ii++) {
            assertNull("Should be no winner in this setup", cells[ii].mark(1));
        }
        for (int ii = 3; ii < cells.length; ii++) {
            assertNull("Should be no winner in this setup", cells[ii].mark(2));
        }
        assertEquals("Expected only two combinations", 2, aggregator.getWinningCoordinates().size());
        BoardCellCoordinates gap = new BoardCellCoordinates(0, 2);
        assertEquals("Only one coordinate expected", aggregator.getWinningCoordinates().size());
        assertTrue("Expected 1", aggregator.getWinningCoordinates().get(0).getPlayers().contains(1));
        assertTrue("Expected 2", aggregator.getWinningCoordinates().get(0).getPlayers().contains(2));
    }

    @Test
    public void onePlayerVictoryTest() {
        attachAggregator(cells.length);
        for (int ii = 0; ii < 4; ii++) {
            cells[ii].mark(1);
            assertEquals("Player 1 still must be able to win", true, aggregator.isCompletable());
        }
        Integer winner = cells[4].mark(1);
        assertEquals("Winner Id is inconsistent between cells and underlying aggregator",
                Integer.valueOf(1), aggregator.getWinner());
        assertEquals("Unexpected winner", Integer.valueOf(1), winner);
    }

    @Test
    public void cellAggregatorNoWinnerTest() {
        attachAggregator(cells.length);
        for (int ii = 0; ii < 3; ii++) {
            cells[ii].mark(1);
            assertEquals("Player 1 still must be able to win", true, aggregator.isCompletable());
        }
        // there is one unmarked cell
        assertFalse(cells[3].isMarked());
        cells[4].mark(2);
        assertEquals("Must not be winnable: xxx_o", false, aggregator.isCompletable());
    }
}
