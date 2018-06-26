package t3.core.implementation;

import t3.core.GameEngine;
import t3.core.GameState;
import t3.core.InvalidMoveException;
import t3.core.data.structures.RoundRobin;

import java.util.Set;
import java.util.TreeSet;

/**
 * Concrete implementation of generalized tic-tac-toe.
 */
public class GenericTicTacToeGameEngine implements GameEngine {

    private RoundRobin turn;
    private int nextTurn;

    public GenericTicTacToeGameEngine() {
        Set<Integer> items = new TreeSet<>();
        items.add(1);
        items.add(2);
        items.add(3);
        turn = new RoundRobin(items);
    }

    @Override
    public void playerMove(int player, int x, int y) throws InvalidMoveException {
        if (player != nextTurn) {
            throw new InvalidMoveException("Specified player must wait for his turn");
        }
    }

    @Override
    public void computerMove() throws InvalidMoveException {
        if (3 != nextTurn) {
            throw new InvalidMoveException("This is not computer's turn yet");
        }
    }

    @Override
    public GameState getState() {
        return null;
    }
}
