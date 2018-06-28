package t3.core.data.structures;

import java.util.Map;
import java.util.TreeMap;

/**
 * Essentially, this is a linked list connected to other linked lists as it splits. It represents connected partitions
 * where each partition belongs solely to one player or solely to no one.
 * <br><br>
 * Players carve this "Island" to get partitions for themselves as they make moves.
 * <br><br>
 * As for time complexity, the need to iterate over each cell(node) is alleviated by partitioning cells which
 * makes traversal a Log N task. In case of single-partitioned nodes the task may still be reduced as there is no need
 * to go over all of them to figure there is no winning combination.
 */
public class IslandPartition {
    private Map<BoardCellCoordinates, Node> nodes = new TreeMap();
    private Node head;
    private Node tail;
    private IslandPartition west, east;
    private Integer belongsTo;

    public IslandPartition(Node node, Integer playerId) {
        nodes.put(node.cell.getCoordinates(), node);
        head = node;
        tail = node;
        belongsTo = playerId;
    }

    public IslandPartition() {
        //
    }

    /**
     * Insert before the head and removes link to the other partition.
     *
     * @param node
     */
    void mergeFromWest(Node node) {
        head.previous = node;
        node.next = head;
        head = node;
        head.previous = null;
    }

    /**
     * Insert after the tail and removes link to the other partition.
     *
     * @param node
     */
    void mergeFromEast(Node node) {
        tail.previous = node;
        node.next = head;
        tail = node;
        head.previous = null;
    }

    /**
     * Chops the head.
     */
    void chopHead() {
        Node oldHead = head;
        head = head.next;
        oldHead.next = null;
        nodes.remove(oldHead.cell.getCoordinates());
    }

    /**
     * Chops the tail and connects with a break away partition.
     * @param newEastPartition
     */
    void chopTailAndConnectTo(IslandPartition newEastPartition) {
        Node oldTail = tail;
        tail = tail.previous;
        tail.next = null;
        nodes.remove(oldTail.cell.getCoordinates());
        newEastPartition.west = this;
        east = newEastPartition;
    }

    /**
     * This splits, merges and creates new partitions.
     *
     * @param coordinates
     * @param newPlayerId
     */
    public IslandPartition repartition(BoardCellCoordinates coordinates, int newPlayerId) {
        Node node = null;
        IslandPartition currentPartition = this;
        while (currentPartition != null) {
            node = currentPartition.nodes.get(coordinates);
            if (node != null) {
                break;
            }
            currentPartition = currentPartition.east;// look for given coordinates in the next partition
        }
        if (node == null) {
            throw new IllegalArgumentException("Unknown node");
        }
        IslandPartition easternIsland = currentPartition.east;
        IslandPartition westernIsland = currentPartition.west;
        if (node.isTail()) {// is eastern edge of the original partition?
            if (easternIsland != null) {
                if (easternIsland.belongsTo == node.cell.getPlayerId()) {
                    easternIsland.mergeFromWest(node);
                    return this;
                } else {
                    easternIsland.chopHead();
                    return this;
                }
            }
            IslandPartition newEastPartition = new IslandPartition(node, newPlayerId);
            currentPartition.chopTailAndConnectTo(newEastPartition);
            return this;
        }
        if (node.isHead()) {// is western edge of the original partition?
            if (westernIsland != null) {
                if (westernIsland.belongsTo == node.cell.getPlayerId()) {
                    // merge with the west partition
                    westernIsland.mergeFromEast(node);
                    return this;
                } else {
                    IslandPartition newEastPartition = new IslandPartition(node, newPlayerId);
                    westernIsland.chopTailAndConnectTo(newEastPartition);
                    return this;
                }
            }
            currentPartition.chopHead();
            IslandPartition newWestIsland = new IslandPartition(node, newPlayerId);
            newWestIsland.east = currentPartition;
            currentPartition.west = newWestIsland;
            return newWestIsland;
        }
        //in the middle
        IslandPartition newWestIsland = new IslandPartition();
        newWestIsland.head = this.head;
        newWestIsland.tail = node.previous;
        if (newWestIsland.tail != null) {
            newWestIsland.tail.next = null;
        }
        IslandPartition newEastIsland = new IslandPartition();
        newEastIsland.head = node.next;
        newEastIsland.tail = this.tail;
        // new islands partitioned, now connect them
        newWestIsland.east = this;
        newWestIsland.west = this.west;
        newEastIsland.west = this;
        newEastIsland.east = this.east;
        this.east = newEastIsland;
        this.west = newWestIsland;
        newEastIsland.belongsTo = this.belongsTo;
        newWestIsland.belongsTo = this.belongsTo;
        this.belongsTo = newPlayerId;
        return newWestIsland;
    }

    public void add(BoardCell cell) {
        if (belongsTo != null) {
            new IllegalStateException("Cells may be added to unmarked partitions only");
        }
        if (nodes.containsKey(cell.getCoordinates())) {
            new IllegalArgumentException("Loop prevented");
        }
        Node newNode = new Node(cell);
        nodes.put(cell.getCoordinates(), newNode);
        if (head == null) {
            head = newNode;
            tail = newNode;
            return;
        }
        tail.next = newNode;
        newNode.previous = tail;
        tail = newNode;

    }

    class Node {
        private BoardCell cell;
        private Node previous, next;

        public Node(BoardCell cell) {
            this.cell = cell;
        }

        @Override
        public String toString() {
            return "Node{cell=" + cell + ", next=" + next + '}';
        }

        public boolean isHead() {
            return previous == null;
        }

        public boolean isTail() {
            return next == null;
        }
    }

    public Node getHead() {
        return head;
    }

    public Node getTail() {
        return tail;
    }

    public IslandPartition getWest() {
        return west;
    }

    public IslandPartition getEast() {
        return east;
    }

    public Integer getBelongsTo() {
        return belongsTo;
    }

    public int getSize() {
        return nodes.size();
    }

    @Override
    public String toString() {
        return "Partition{size=" + nodes.size() + ", belongsTo=" + belongsTo + '}';
    }
}

