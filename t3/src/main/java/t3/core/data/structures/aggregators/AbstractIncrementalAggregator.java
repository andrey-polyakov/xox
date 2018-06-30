package t3.core.data.structures.aggregators;

import t3.core.data.structures.BoardCell;
import t3.core.data.structures.BoardCellCoordinates;
import t3.core.data.structures.PartitionedIsland;

import java.util.*;

/**
 * Keeps track of nodes forming a particular shape. For instance,
 * it could be a line, diagonal or a diamond.<br><br>
 * <p>
 * Aggregators are needed to reduce time-complexity of board searches. Internally,
 * store partitioned islands of nodes(cells).
 */
public abstract class AbstractIncrementalAggregator implements IncrementalShapeAggregator {
    protected PartitionedIsland island = new PartitionedIsland();
    protected Map<BoardCellCoordinates, Set<Integer>> winning = new TreeMap<>();
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
    public Map<BoardCellCoordinates, Set<Integer>> getWinningCoordinates() {
        if (!completable) {
            return Collections.emptyMap();
        }
        PartitionedIsland current = island.getWestmostPoint();
        int west = 0, center = 0, east = 0;
        do {
            if (current.isFree()) {
                BoardCellCoordinates winningCoordinate = current.getHead().getCoordinates();
                // xx____ long gap
                if (current.getWest() != null) {
                    west = current.getWest().getSize();
                    if (west + 1 >= winningSegmentSize) {
                        recordWinner(winningCoordinate, current.getWest().getBelongsTo());
                    }
                }
                // ____xx long gap
                if (current.getEast() != null) {
                    east =  current.getEast().getSize();
                    if (east + 1 >= winningSegmentSize) {
                        recordWinner(winningCoordinate, current.getEast().getBelongsTo());
                    }
                }
                if (current.getEast() == null || current.getWest() == null) {
                    current = current.getEast();
                    continue;
                }
                // xxxx_xx short gap
                if (current.getSize() == 1 && east + west + 1 >= winningSegmentSize) {
                    //is this a gap between partitions of the same player?
                    if (current.getWest().getBelongsTo() == current.getEast().getBelongsTo()) {
                        recordWinner(winningCoordinate, current.getWest().getBelongsTo());
                    }
                }
            }
            current = current.getEast();
        } while (current != null);
        return Collections.unmodifiableMap(winning);
    }

    private void recordWinner(BoardCellCoordinates coordinates, int winnerId) {
        Set<Integer> winners = winning.getOrDefault(coordinates, new TreeSet<>());
        winners.add(winnerId);
        winning.put(coordinates, winners);
    }

    /**
     * Add the aggregate with a new mark.
     *
     * @param cell of the winner or null
     * @param newPlayerId
     */
    @Override
    public Integer update(BoardCell cell, int newPlayerId) {
        island = island.repartition(cell.getCoordinates(), newPlayerId).getWestmostPoint();
        PartitionedIsland currentPartition = island.getPartition(cell.getCoordinates());
        if (currentPartition.getSize() >= winningSegmentSize) {
            winner = currentPartition.getBelongsTo();
        }
        return winner;
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
