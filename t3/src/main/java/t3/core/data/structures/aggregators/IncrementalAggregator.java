package t3.core.data.structures.aggregators;

import t3.core.data.structures.BoardCell;
import t3.core.data.structures.BoardCellCoordinates;
import t3.core.data.structures.WinningCoordinate;

import java.util.List;

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
public interface IncrementalAggregator {

    /**
     * Returns coordinates which if occupied complete the shape.
     * @return
     */
    List<WinningCoordinate> getWinningCoordinates();


    /**
     * Add one stone to the aggregate.
     *
     * @param playerId of the winner or null
     */
    Integer putMark(Integer playerId);

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
     * True if no player can complete the shape.
     *
     * @return
     */
    boolean isCompletable();

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
