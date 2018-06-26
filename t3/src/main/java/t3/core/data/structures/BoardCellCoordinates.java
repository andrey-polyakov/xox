package t3.core.data.structures;

/**
 * Represents cell coordinates on the board.
 */
public class BoardCellCoordinates implements Comparable<BoardCellCoordinates> {
    private Integer row, column;

    public BoardCellCoordinates(int row, int col) {
        this.row = row;
        this.column = col;
    }

    public Integer getRow() {
        return row;
    }

    public Integer getColumn() {
        return column;
    }

    @Override
    public int compareTo(BoardCellCoordinates o) {
        int rowComparison = row.compareTo(o.row);
        int colComparison = column.compareTo(o.column);
        if (rowComparison != 0)
            return rowComparison;
        if (colComparison != 0)
            return colComparison;
        return 0;
    }

    @Override
    public String toString() {
        return "BoardCellCoordinates{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }
}
