package t3.core.engine;

import org.junit.Before;
import org.junit.Test;
import t3.core.data.structures.BoardCell;
import t3.core.data.structures.BoardCellCoordinates;

import java.util.*;

import static javax.swing.UIManager.get;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNull;

public class BoardTest {

    public static final int WINNING_SCORE = 3;
    private Set<Integer> players = new TreeSet<>();
    private Set<Integer> trio = new TreeSet<>();

    @Before
    public void init() {
        players.add(1);
        players.add(2);
        trio.addAll(players);
        trio.add(3);
    }

    @Test
    public void board554Test() {
        GenericTicTacToe systemUnderTest = new GenericTicTacToe(players, 5, 4, false);
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
        GenericTicTacToe systemUnderTest = new GenericTicTacToe(players, 5, 3, false);
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

    @Test
    public void basic4x4x3Test() {
        GenericTicTacToe systemUnderTest = new GenericTicTacToe(players, 4, WINNING_SCORE, false);
        assertNull(systemUnderTest.takeTurn(new BoardCellCoordinates(0, 0)));
        assertNull(systemUnderTest.takeTurn(new BoardCellCoordinates(1, 1)));
        assertNull(systemUnderTest.takeTurn(new BoardCellCoordinates(2, 0)));
        assertNull(systemUnderTest.takeTurn(new BoardCellCoordinates(2, 2)));
        assertNull(systemUnderTest.takeTurn(new BoardCellCoordinates(0, 2)));

        Map<BoardCellCoordinates, Set<Integer>> jackPot = systemUnderTest.getWinningCoordinates();
        assertEquals("See illustration",3, jackPot.size());
        // 3 * 1 - mark of player I
        // 2 * 2 - mark of player II
        // 2 * X - where #1 can win: 0,1 & 1,0
        // 1 * Y - where #2 can win: 3,3
        //
        //  1 X 1 _
        //  X 2 _ _
        //  1 _ 2 _
        //  _ _ _ Y
        //
        // winning score : 3
        int[] data = {10,20,30,40,50,60,71,80,90,91};

        List<Set<Integer>> cells = new LinkedList<>();
        cells.add(jackPot.get(new BoardCellCoordinates(0, 1)));
        cells.add(jackPot.get(new BoardCellCoordinates(1, 0)));
        for (Set<Integer> cell : cells) {
            assertNotNull(cell);
            assertTrue(cell.contains(1));
            assertEquals(1, cell.size());
        }

        Set<Integer> cell33 = jackPot.get(new BoardCellCoordinates(3, 3));
        assertNotNull(cell33);
        assertEquals(1, cell33.size());
        assertTrue(cell33.contains(2));
    }


}
