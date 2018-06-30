package t3.core.data.structures.aggregators;

import t3.core.data.structures.BoardCell;
import t3.core.data.structures.BoardCellCoordinates;

import java.util.Map;
import java.util.Set;

/**
 * Ever increasing aggregator which keep track of
 * player moves in particular areas of the board.
 * These areas may take different shapes (line, cross, square, etc).
 * <br><br>
 * The main idea is to answer some questions in logarithmic time. For instance,
 * whether particular players may complete particular shapes next turn or
 * never would.
 *
 */
public interface IncrementalShapeAggregator {

    /**
     * Returns coordinates which if occupied complete the shape.
     * @return
     */
    Map<BoardCellCoordinates, Set<Integer>> getWinningCoordinates();


    /**
     * Add update to the aggregate.
     *
     * @param cell cells to be updated
     * @param newPlayerId new player ID
     */
    Integer update(BoardCell cell, int newPlayerId);

    /**
     * True if tracks a row.
     *
     * @return
     */
    default boolean isHorizontalLineSegment() {
        return false;
    }

    /**
     * True if track a vertical segment.
     * @return
     */
    default boolean isVerticalLineSegment() {
        return false;
    }

    /**
     * True if this aggregator tracks a diagonal.
     * @return
     */
    default boolean isDiagonalLineSegment() {
        return false;
    }

    /**
     *
     * @return winning player id
     */
    Integer getWinner();

    /**
     * Register cell.
     * @param cell
     */
    void addCell(BoardCell cell);
}
