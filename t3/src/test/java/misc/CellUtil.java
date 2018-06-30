package misc;

import t3.core.data.structures.BoardCell;
import t3.core.data.structures.BoardCellCoordinates;

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
}
