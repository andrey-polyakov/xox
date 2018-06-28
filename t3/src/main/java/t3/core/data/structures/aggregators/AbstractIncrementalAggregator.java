package t3.core.data.structures.aggregators;

import t3.core.data.structures.BoardCell;
import t3.core.data.structures.IslandPartition;
import t3.core.data.structures.WinningCoordinate;

import java.util.*;

/**
 * Keeps track of nodes forming a particular shape. For instance,
 * it could be a line, diagonal or a diamond.<br><br>
 * <p>
 * Aggregators are needed to reduce time-complexity of board searches. Internally,
 * store partitioned islands of nodes(cells).
 */
public abstract class AbstractIncrementalAggregator implements IncrementalShapeAggregator {
    protected IslandPartition island = new IslandPartition();
    protected List<WinningCoordinate> winning = new LinkedList();
    protected int winningSegmentSize;
    private boolean completable = true;
    private Integer winner;

    public AbstractIncrementalAggregator(int winningSegmentSize) {
        this.winningSegmentSize = winningSegmentSize;
    }

    /**
     * Produces spots which if occupied by particular players
     * complete a winning shape in one turn.
     * <br><br>
     * Example: Consider all in row combination of size five:<br>
     * XXX_X <br>Here X may win if he marks the gap.
     *
     * @return
     */
    @Override
    public List<WinningCoordinate> getWinningCoordinates() {
        if (!completable) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(winning);
    }

    /**
     * Add one stone to the aggregate.
     *
     * @param cell of the winner or null
     * @param newPlayerId
     */
    @Override
    public Integer update(BoardCell cell, int newPlayerId) {
        island = island.repartition(cell.getCoordinates(), newPlayerId);
        return winner;
    }

    @Override
    public boolean isCompletable() {
        return completable;
    }

    @Override
    public Integer getWinner() {
        return winner;
    }

    @Override
    public void addCell(BoardCell cell) {
        island.add(cell);
    }


}
