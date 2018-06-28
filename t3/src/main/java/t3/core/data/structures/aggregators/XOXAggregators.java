package t3.core.data.structures.aggregators;

/**
 * TicTatToe specific aggregators. There are only
 * three shapes, namely, rows, columns, and diagonals.
 *
 */
public class XOXAggregators {

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
