package t3.core.data.structures.aggregators;

import t3.core.data.structures.BoardCell;
import t3.core.data.structures.BoardCellCoordinates;
import t3.core.data.structures.WinningCoordinate;

import java.util.*;

/**
 * Keeps track of stones forming a particular shape. For instance,
 * it could be a line, diagonal or a diamond.<br><br>
 * <p>
 * Aggregators are needed to reduce time-complexity of board searches.
 */
public abstract class AbstractIncrementalAggregator implements IncrementalAggregator {
    protected List<BoardCell> trackedCells = new LinkedList();
    protected List<WinningCoordinate> winning = new LinkedList();
    protected SortedMap<Integer, Integer> playerToMarks = new TreeMap<>();
    protected int winningSegmentSize;
    private boolean completable = true;
    private Integer winner;
    private int totalMarkedCounter = 0;

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
     * @param playerId of the winner or null
     */
    @Override
    public Integer putMark(Integer playerId) {
        Integer marks = playerToMarks.getOrDefault(playerId, 0) + 1;
        playerToMarks.put(playerId, marks);
        totalMarkedCounter++;
        if (playerToMarks.keySet().size() == 1) {
            // Single player
            // winner short-cut check
            if (totalMarkedCounter == winningSegmentSize && winningSegmentSize == trackedCells.size()) {
                winner = playerId;// easy case, the entire segment must be marked to win
                return playerId; // and one player marked the entire segment, so he wins
            }
            return null;
        }
        // test if this sequence is not solvable
        int remainder = trackedCells.size() - totalMarkedCounter;
        if (winningSegmentSize - playerToMarks.lastKey() > remainder) {
            completable = false; // not possible to win
            return null;
        }
        // Full scan for other cases
        for (BoardCell trackedCell : trackedCells) {

        }
        return null;
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
        trackedCells.add(cell);
    }
}
