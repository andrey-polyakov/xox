package misc;

import t3.core.data.structures.BoardCell;
import t3.core.data.structures.BoardCellCoordinates;

/**
 * Reusable inputs for partitioned island.
 */
public class CellUtil {

    public static BoardCell[] create1x5() {
        BoardCell[] newCells = {
                new BoardCell(new BoardCellCoordinates(0, 0)),
                new BoardCell(new BoardCellCoordinates(0, 1)),
                new BoardCell(new BoardCellCoordinates(0, 2)),
                new BoardCell(new BoardCellCoordinates(0, 3)),
                new BoardCell(new BoardCellCoordinates(0, 4))
        };
        return newCells;
    }

    public static BoardCell[] create1x4diagonal() {
        BoardCell[] newCells = {
                new BoardCell(new BoardCellCoordinates(0, 3)),
                new BoardCell(new BoardCellCoordinates(1, 2)),
                new BoardCell(new BoardCellCoordinates(2, 1)),
                new BoardCell(new BoardCellCoordinates(3, 0))
        };
        return newCells;
    }

    public static BoardCell[] create1x4() {
        BoardCell[] newCells = {
                new BoardCell(new BoardCellCoordinates(0, 0)),
                new BoardCell(new BoardCellCoordinates(1, 0)),
                new BoardCell(new BoardCellCoordinates(2, 0)),
                new BoardCell(new BoardCellCoordinates(3, 0))
        };
        return newCells;
    }
}
