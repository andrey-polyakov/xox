package t3.core.data.structures;

import t3.core.data.structures.aggregators.IncrementalAggregator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents cell of a board game. Performs book-keeping
 * functions.
 */
public class BoardCell {
    /**
     * True if there is a stone put by a player.
     */
    private Integer playerId;
    private final BoardCellCoordinates coordinates;
    private List<IncrementalAggregator> aggregators = new LinkedList<>();

    public BoardCell(BoardCellCoordinates boardCellCoordinates) {
        coordinates = boardCellCoordinates;
    }

    /**
     * Make a record on the lowest level and do bookkeeping.
     *
     * @param playerId
     * @return winner or null
     */
    public Integer mark(int playerId) {
        this.playerId = playerId;
        for (IncrementalAggregator aggregator : aggregators) {
            Integer winner = aggregator.putMark(playerId);
            if (winner != null) {
                return winner;// short cut as the game is over
            }
        }
        return null;
    }

    public boolean isMarked() {
        return playerId != null;
    }

    public List<IncrementalAggregator> getAggregators() {
        return Collections.unmodifiableList(aggregators);
    }

    public void addAggregator(IncrementalAggregator aggregator) {
        aggregators.add(aggregator);
        aggregator.addCell(this);
    }

    public BoardCellCoordinates getCoordinates() {
        return coordinates;
    }
}
