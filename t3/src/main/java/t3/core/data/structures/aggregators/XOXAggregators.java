package t3.core.data.structures.aggregators;

import t3.core.data.structures.BoardCell;
import t3.core.data.structures.BoardCellCoordinates;
import t3.core.data.structures.PartitionedIsland;
import t3.core.engine.GenericTicTacToe;

import java.util.*;

/**
 * TicTatToe specific aggregators. There are only
 * three shapes, namely, rows, columns, and diagonals.
 *
 */
public class XOXAggregators {

    public static final int FOUR_CORNERS = 4;

    static class RowAggregator extends AbstractIncrementalAggregator {
        public RowAggregator(int winningSegmentSize) {
            super(winningSegmentSize);
        }

        @Override
        public boolean isHorizontalLineSegment() {
            return true;
        }
    }

    static class ColumnAggregator extends AbstractIncrementalAggregator {
        public ColumnAggregator(int winningSegmentSize) {
            super(winningSegmentSize);
        }

        @Override
        public boolean isVerticalLineSegment() {
            return true;
        }
    }

    static class DiagonalAggregator extends AbstractIncrementalAggregator {
        public DiagonalAggregator(int winningSegmentSize) {
            super(winningSegmentSize);
        }

        @Override
        public boolean isDiagonalLineSegment() {
            return true;
        }
    }

    static class CornerAggregator extends AbstractIncrementalAggregator {
        private GenericTicTacToe board;
        private int hitCorners = 0;
        private Set<Integer> players = new HashSet<>();

        public CornerAggregator(int winningSegmentSize, GenericTicTacToe board) {
            super(winningSegmentSize);
            this.board = board;
        }

        @Override
        public boolean isDiagonalLineSegment() {
            return false;
        }

        @Override
        public boolean isHorizontalLineSegment() {
            return false;
        }

        @Override
        public boolean isVerticalLineSegment() {
            return false;
        }

        @Override
        public boolean isAllCorners() {
            return true;
        }

        @Override
        public Map<BoardCellCoordinates, Set<Integer>> getWinningCoordinates() {
            BoardCellCoordinates emptyCorner = null;
            for (BoardCellCoordinates corner : board.getCorners()) {
                if (board.getCell(corner).getPlayerId() != null) {
                    players.add(board.getCell(corner).getPlayerId());
                    hitCorners++;
                } else {
                    emptyCorner = corner;
                }
            }
            if (hitCorners == FOUR_CORNERS - 1) {
                if (players.size() == 1) {
                    for (Integer player : players) {
                        recordWinner(emptyCorner, player);
                    }
                }
            }
            return Collections.emptyMap();
        }

        @Override
        public Integer update(BoardCell cell, int newPlayerId) {
            island = island.repartition(cell.getCoordinates(), newPlayerId).getWestmostPoint();
            Integer winner = null;
            players.add(newPlayerId);
            hitCorners++;
            if (hitCorners == FOUR_CORNERS && players.size() == 1 && players.contains(newPlayerId)) {
                for (Integer player : players) {
                    winner = player;
                }
            }
            return winner;
        }
    }

    public static IncrementalShapeAggregator newCornerAggregator(GenericTicTacToe board) {
        return new CornerAggregator(FOUR_CORNERS, board);
    }

    public static IncrementalShapeAggregator newRowAggregator(int winningSegmentSize) {
        return new RowAggregator(winningSegmentSize);
    }

    public static IncrementalShapeAggregator newColumnAggregator(int winningSegmentSize) {
        return new ColumnAggregator(winningSegmentSize);
    }

    public static IncrementalShapeAggregator newDiagonalAggregator(int winningSegmentSize) {
        return new DiagonalAggregator(winningSegmentSize);
    }

}
