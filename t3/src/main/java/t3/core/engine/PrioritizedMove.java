package t3.core.engine;

import t3.core.data.structures.BoardCellCoordinates;

public class PrioritizedMove implements Comparable<PrioritizedMove> {
    private Integer priority;
    private BoardCellCoordinates coordinates;

    public PrioritizedMove(Integer priority, BoardCellCoordinates coordinates) {
        this.priority = priority;
        this.coordinates = coordinates;
    }

    @Override
    public int compareTo(PrioritizedMove o) {
        return priority.compareTo(o.priority);
    }

    public Integer getPriority() {
        return priority;
    }

    public BoardCellCoordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return "PrioritizedMove{" +
                "priority=" + priority +
                ", coordinates=" + coordinates +
                '}';
    }
}
