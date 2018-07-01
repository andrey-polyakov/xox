package t3.core.engine;

import t3.core.data.structures.BoardCell;
import t3.core.data.structures.BoardCellCoordinates;
import t3.core.data.structures.aggregators.IncrementalShapeAggregator;
import t3.core.data.structures.aggregators.XOXAggregators;

import java.util.*;

/**
 * Implements rules of tic-tac-toe on a square board.
 */
public class GenericTicTacToe extends BoardGame {

    private int edgeLength;
    private int winingSegmentLength;
    private List<IncrementalShapeAggregator> aggregators = new LinkedList<>();


    public GenericTicTacToe(Set<Integer> players, int n, int winingLength) {
        super(players);
        if (n < 3 || n > 10) {
            throw new IllegalArgumentException("Valid board length is between 3 and 10.");
        }
        edgeLength = n;
        cells = new BoardCell[n][n];
        winingSegmentLength = winingLength;
        remainingCells = edgeLength * edgeLength;
        initializeBoard();
    }

    /**
     * Goes over all possibly winnable shapes and beef up aggregators with corresponding cells. Cell may report
     * to more than one aggregator. For instance, cell always belong to vertical and horizontal lines. It may or may not
     * belong to a number of diagonal lines too.
     */
    private void initializeBoard() {
        List<IncrementalShapeAggregator> diagonal = new LinkedList<>();
        Map<Integer, IncrementalShapeAggregator> horizontal = new TreeMap<>();
        Map<Integer, IncrementalShapeAggregator> vertical = new TreeMap<>();
        Map<Integer, IncrementalShapeAggregator> left2RightDiagonalIndices1 = new TreeMap<>();
        Map<Integer, IncrementalShapeAggregator> left2RightDiagonalIndices2 = new TreeMap<>();
        Map<Integer, IncrementalShapeAggregator> right2LeftDiagonalIndices1 = new TreeMap<>();
        Map<Integer, IncrementalShapeAggregator> right2LeftDiagonalIndices2 = new TreeMap<>();

        for (int row = 0; row < edgeLength; row++) {
            //handling horizontal sequences
            IncrementalShapeAggregator horizontalAggregator = XOXAggregators.newRowAggregator(winingSegmentLength);
            horizontal.put(row, horizontalAggregator);
            //
            for (int column = 0; column < edgeLength; column++) {
                // handling vertical sequences
                IncrementalShapeAggregator verticalAggregator = vertical.get(column);
                if (verticalAggregator == null) {
                    verticalAggregator = XOXAggregators.newColumnAggregator(winingSegmentLength);
                    vertical.put(column, verticalAggregator);
                }
                // Create a cell and attach aggregators to it
                BoardCell cell = cells[row][column] = new BoardCell(new BoardCellCoordinates(row, column));
                cell.addAggregator(horizontalAggregator);
                cell.addAggregator(verticalAggregator);
                // the following initiates diagonal sequence
                if (isOnLeft2RightDiagonal(row, column)) {
                    IncrementalShapeAggregator newAggregator = XOXAggregators.newDiagonalAggregator(winingSegmentLength);
                    left2RightDiagonalIndices1.put(column, newAggregator);
                }
                if (isOnRight2LeftDiagonal(row, column)) {
                    IncrementalShapeAggregator newAggregator = XOXAggregators.newDiagonalAggregator(winingSegmentLength);
                    right2LeftDiagonalIndices1.put(column, newAggregator);
                }
                if (left2RightDiagonalIndices1.containsKey(column)) {
                    IncrementalShapeAggregator leftToRightDiagonal = left2RightDiagonalIndices1.get(column);
                    cell.addAggregator(leftToRightDiagonal);
                    if (column + 2 > edgeLength) {// end of sequence, move to array
                        diagonal.add(leftToRightDiagonal);
                    } else {
                        // shift coordinates for use in the next row
                        left2RightDiagonalIndices2.put(column + 1, leftToRightDiagonal);
                    }
                }
                if (right2LeftDiagonalIndices1.containsKey(column)) {
                    IncrementalShapeAggregator right2leftDiagonal = right2LeftDiagonalIndices1.get(column);
                    cell.addAggregator(right2leftDiagonal);
                    if (column - 1 < 0) {
                        diagonal.add(right2leftDiagonal);
                    } else {
                        right2LeftDiagonalIndices2.put(column - 1, right2leftDiagonal);
                    }
                }
            }
            left2RightDiagonalIndices1.clear();
            left2RightDiagonalIndices1.putAll(left2RightDiagonalIndices2);
            left2RightDiagonalIndices2.clear();
            right2LeftDiagonalIndices1.clear();
            right2LeftDiagonalIndices1.putAll(right2LeftDiagonalIndices2);
            right2LeftDiagonalIndices2.clear();
        }
        aggregators.addAll(diagonal);
        aggregators.addAll(vertical.values());
        aggregators.addAll(horizontal.values());
    }

    private boolean isOnLeft2RightDiagonal(int row, int column) {
        if (column == 0 && row == 0) {
            return true;
        }
        if (column == 0 ^ row == 0) {
            return (edgeLength - row - 1 >= winingSegmentLength) &&
                    (edgeLength - column >= winingSegmentLength);
        }
        return false;//no edge
    }

    private boolean isOnRight2LeftDiagonal(int row, int column) {
        if (column == edgeLength - 1 && row == 0) {
            return true;
        }
        if (column == edgeLength - 1 ^ row == 0) {
            return (edgeLength - row - 1 >= winingSegmentLength) &&
                    (-winingSegmentLength + column + 1 > -1);
        }
        return false;//no edge
    }

    Map<BoardCellCoordinates, Set<Integer>> getWinningCoordinates() {
        Map<BoardCellCoordinates, Set<Integer>> jackPot= new TreeMap<>();
        for (IncrementalShapeAggregator aggregator : aggregators) {
            for (Map.Entry<BoardCellCoordinates, Set<Integer>> item : aggregator.getWinningCoordinates().entrySet()) {
                if (jackPot.containsKey(item.getKey())) {
                    jackPot.get(item.getKey()).addAll(item.getValue());
                } else {
                    jackPot.put(item.getKey(), item.getValue());
                }
            }
        }
        return jackPot;
    }
}
