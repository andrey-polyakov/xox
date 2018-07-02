package t3.core.engine;

import t3.core.data.structures.BoardCellCoordinates;
import t3.core.data.structures.aggregators.IncrementalShapeAggregator;

import java.util.Map;
import java.util.Set;

public class BasicT3Stratagy implements Strategy {

    private int playerId;
    private GenericTicTacToe game;

    public BasicT3Stratagy(int playerId, GenericTicTacToe game) {
        this.playerId = playerId;
        this.game = game;
    }

    @Override
    public BoardCellCoordinates makeMove() {
        if (game.isGameOver()) {
            throw new IllegalArgumentException("Game is over");
        }
        BoardCellCoordinates selection = null;
        int numberOfPotentialWinners = 0;
        for (Map.Entry<BoardCellCoordinates, Set<Integer>> entry : game.getWinningCoordinates().entrySet()) {
            if (entry.getValue().contains(playerId)) {
                return entry.getKey();        // If I can win on this move, do it.
            }
            //If the other player can win on the next move, block that winning square.
            if (entry.getValue().size() > numberOfPotentialWinners) {
                numberOfPotentialWinners = entry.getValue().size();
                selection = entry.getKey();// If there is more than one option the most threatening one.
            }
        }
        if (selection != null) {
            return selection;
        }
//TODO
//If, on the next move, my opponent can set up a fork, block that possibility by moving into the square that is common to his two winning combinations.
//
// If I can make a move that will set up a winning combination for myself, do it. But ensure that this move does not force the opponent into establishing a fork.
        PrioritizedMove bestMove = null;
        for (IncrementalShapeAggregator o : game.getAggregators()) {
            if (o.isCompletable()) {
                PrioritizedMove suggested = o.getBestMoveFor(playerId, game.getWiningSegmentLength());
                if (suggested == null) {
                    continue;
                }
                if (bestMove != null) {
                    if (suggested.getPriority() > bestMove.getPriority()) {
                        bestMove = suggested;
                    }
                } else {
                    bestMove = suggested;
                }
            }
        }
        return bestMove.getCoordinates();
    }
}
