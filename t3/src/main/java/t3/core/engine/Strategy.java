package t3.core.engine;

import t3.core.data.structures.BoardCellCoordinates;

/**
 * Basic game solver interface.
 */
public interface Strategy {

    /**
     * May work as an artificial player or make suggestions to humans.
     * @return best move
     */
    BoardCellCoordinates makeMove();
}
