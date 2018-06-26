package t3.core.engine;

import org.junit.Before;
import org.junit.Test;
import t3.core.data.structures.BoardCell;
import t3.core.data.structures.BoardCellCoordinates;

import java.util.Set;
import java.util.TreeSet;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class BoardTest {

    private Set<Integer> players = new TreeSet<>();

    @Before
    public void init() {
        players.add(1);
        players.add(2);
    }

    @Test
    public void board554Test() {
        GenericTicTacToe systemUnderTest = new GenericTicTacToe(players, 5, 4);
        BoardCellCoordinates[] coordinates = {
                // left to right diagonal
                new BoardCellCoordinates(0, 0),
                new BoardCellCoordinates(0, 1),
                // right to left diagonal
                new BoardCellCoordinates(0, 4),
                new BoardCellCoordinates(0, 3)
        };
        iterateAndAssert(systemUnderTest, coordinates, 3);
        BoardCell center = systemUnderTest.getCell(new BoardCellCoordinates(2, 2));
        assertEquals("Should have been 2 diagonals + 1 row + 1 column = 4",4,  center.getAggregators().size());
    }

    @Test
    public void board553Test() {
        GenericTicTacToe systemUnderTest = new GenericTicTacToe(players, 5, 3);
        BoardCellCoordinates [] coordinates = {
                new BoardCellCoordinates(0, 0),
                new BoardCellCoordinates(0, 1),
                new BoardCellCoordinates(0, 4),
                new BoardCellCoordinates(0, 3),
                new BoardCellCoordinates(3, 0),
                new BoardCellCoordinates(4, 3)
        };
        iterateAndAssert(systemUnderTest, coordinates, 3);
        BoardCellCoordinates [] coordinates1 = {
                // double diagonals
                new BoardCellCoordinates(0, 2),
                new BoardCellCoordinates(1, 1),
                new BoardCellCoordinates(1, 2),
                new BoardCellCoordinates(1, 3),
                new BoardCellCoordinates(2, 1),
                new BoardCellCoordinates(2, 2),
                new BoardCellCoordinates(2, 3),
                new BoardCellCoordinates(3, 2)
        };
        iterateAndAssert(systemUnderTest, coordinates1, 4);

        BoardCell center = systemUnderTest.getCell(new BoardCellCoordinates(3, 0));
        assertEquals(3,  center.getAggregators().size());// row & column, only
    }

    private void iterateAndAssert(GenericTicTacToe systemUnderTest, BoardCellCoordinates[] coordinates, int expected) {
        for (BoardCellCoordinates coordinate : coordinates) {
            BoardCell cell = systemUnderTest.getCell(coordinate);
            assertTrue(coordinate.toString(), cell.getAggregators().stream().anyMatch(item -> item.isHorizontalLineSegment()));
            assertTrue(coordinate.toString(), cell.getAggregators().stream().anyMatch(item -> item.isVerticalLineSegment()));
            assertTrue(coordinate.toString(), cell.getAggregators().stream().anyMatch(item -> item.isDiagonalLineSegment()));
            assertEquals(coordinate.toString(), expected,  cell.getAggregators().size());
        }
    }


}
