package t3.core.data.structures;

import t3.core.data.structures.aggregators.IncrementalShapeAggregator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents cell of a board game. Delegates book-keeping functions to aggregators.
 */
public class BoardCell {
    /**
     * True if there is a stone put by a player.
     */
    private Integer playerId;
    private final BoardCellCoordinates coordinates;
    private List<IncrementalShapeAggregator> aggregators = new LinkedList<>();

    public BoardCell(BoardCellCoordinates boardCellCoordinates) {
        coordinates = boardCellCoordinates;
    }

    /**
     * Make a record on the lowest level and do bookkeeping. Interface for the controller.
     *
     * @param newPlayerId
     * @return winner or null
     */
    public Integer mark(int newPlayerId) {
        for (IncrementalShapeAggregator aggregator : aggregators) {
            // if it breaks here we will not be able to recover
            Integer winner = aggregator.update(this, newPlayerId);
            if (winner != null) {
                return winner;// short cut as the game is over
            }
        }
        this.playerId = newPlayerId;
        return null;
    }

    /**
     *
     * @return true if marked by a player
     */
    public boolean isMarked() {
        return playerId != null;
    }

    /**
     *
     * @return ID of the owner
     */
    public Integer getPlayerId() {
        return playerId;
    }

    public List<IncrementalShapeAggregator> getAggregators() {
        return Collections.unmodifiableList(aggregators);
    }

    /**
     * Registers a call-back to an aggregator.
     * @param aggregator
     */
    public void addAggregator(IncrementalShapeAggregator aggregator) {
        aggregators.add(aggregator);
        aggregator.addCell(this);
    }

    public BoardCellCoordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return coordinates.toString();
    }
}
