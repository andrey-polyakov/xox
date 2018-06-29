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
     * @param forPlayerId
     */
    IslandPartition mergeFromWest(int forPlayerId) {
        if (west == null) {
            throw new IllegalStateException("No western partition to merge with");
        }
        Node newHead = west.tail;
        head.previous = newHead;
        west.moveTail();
        newHead.next = head;
        newHead.previous = null;
        head = newHead;
        nodes.put(newHead.cell.getCoordinates(), newHead);
        if (west.getSize() == 0) {
            west = null;
            return this;
        }
        if (west.getSize() == 1) {
            if (west.west == null) {
                return west; // edge
            }
            west = west.west;// expand to the east
            west.east = this;// fix eastern pointer, one cell partition get lost
            if (west != null && west.belongsTo == forPlayerId) {// if east from the gap we took is ours then merge
                this.mergeFromWest(forPlayerId);
            }
        }
        return west;
    }

    private void moveTail() {
        nodes.remove(tail.cell.getCoordinates());// shrink it
        if (tail.previous == null)
            return;
        tail.previous.next = null;
        tail = tail.previous;
    }

    private void moveHead() {
        nodes.remove(head.cell.getCoordinates());// shrink it
        if (head.next == null)
            return;
        head.next.previous = null;
        head = head.next;
    }

    /**
     * Takes over a small tip of unallocated space or merges to the other partition of this player.
     *
     */
    void mergeFromEast(int forPlayerId) {
        if (east == null) {
            throw new IllegalStateException("No eastern partition to merge with");
        }
        Node newTail = east.head;
        tail.next = newTail;
        east.moveHead();
        newTail.previous = tail;
        newTail.next = null;
        tail = newTail;
        nodes.put(newTail.cell.getCoordinates(), newTail);
        if (east.getSize() == 0) {
            east = null;
            return;
        }
        if (east.getSize() == 1) {
            if (east.east == null) {
                return; // edge
            }
            east = east.east;// expand to the east
            east.west = this;// fix eastern pointer, one cell partition get lost
            if (east != null && east.belongsTo == forPlayerId) {// if east from the gap we took is ours then merge
                this.mergeFromEast(forPlayerId);
            }
        }
    }

    /**
     * Separates western tip from this partition.
     */
    IslandPartition chopWesternTip(int forPlayerId) {
        Node oldHead = head;
        IslandPartition newWest = new IslandPartition(oldHead, forPlayerId);
        head = head.next;
        head.previous = null;
        oldHead.next = null;
        nodes.remove(oldHead.cell.getCoordinates());
        newWest.east = this;
        this.west = newWest;
        return newWest;
    }

    /**
     * Chops the tail and connects with a break away partition.
     * @param forPlayerId
     */
    IslandPartition chopEasternTipAndAttachTo(int forPlayerId) {
        Node oldTail = tail;
        tail = tail.previous;
        IslandPartition newEast = new IslandPartition(oldTail, forPlayerId);
        oldTail.previous = null;
        if (tail != null) {
            tail.next = null;
        }
        nodes.remove(oldTail.cell.getCoordinates());
        newEast.west = this;
        this.east = newEast;
        return this;
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
            throw new IllegalArgumentException("Unknown node with " + coordinates.toString());
        }
        IslandPartition easternIsland = currentPartition.east;
        IslandPartition westernIsland = currentPartition.west;
        if (node.isTail()) {// is eastern edge of the original partition?
            if (easternIsland != null) {
                if (easternIsland.belongsTo == null) {
                    return currentPartition.chopEasternTipAndAttachTo(newPlayerId);
                }
                if (easternIsland.belongsTo == newPlayerId) {
                    return easternIsland.mergeFromWest(newPlayerId);
                } else {
                    return easternIsland.chopWesternTip(newPlayerId);
                }
            }
            if (currentPartition.getSize() > 1) {
                currentPartition.chopEasternTipAndAttachTo(newPlayerId);
                return this;
            }
        }
        if (node.isHead()) {// is western edge of the original partition?
            if (westernIsland != null) {
                if (westernIsland.belongsTo == newPlayerId) {
                    // merge with the west partition
                    westernIsland.mergeFromEast(newPlayerId);
                    return this;
                } else {
                    westernIsland.chopEasternTipAndAttachTo(newPlayerId);
                    return this;
                }
            }
            return currentPartition.chopWesternTip(newPlayerId);
        }
        //in the middle
        currentPartition.splitInThree(node, newPlayerId);
        return this;
    }

    private void splitInThree(Node middleNode, int newPlayerId) {
        IslandPartition middlePartition = new IslandPartition(middleNode, newPlayerId);
        middlePartition.tail = middleNode;
        Node easternPartitionTip = middleNode.next;
        easternPartitionTip.previous = null;
        middleNode.next = null;
        IslandPartition easternPartition = new IslandPartition(easternPartitionTip, belongsTo);
        easternPartition.tail = tail;
        nodes.remove(middleNode.cell.getCoordinates());
        tail = middleNode.previous;
        tail.next = null;
        middleNode.previous.next = null;
        middleNode.previous = null;
        do {
            nodes.remove(easternPartitionTip.cell.getCoordinates());
            easternPartition.addNode(easternPartitionTip);
            easternPartitionTip = easternPartitionTip.next;
        } while (easternPartitionTip != null);
        east = middlePartition;
        middlePartition.west = this;
        middlePartition.east = easternPartition;
        easternPartition.west = middlePartition;
        middlePartition.belongsTo = newPlayerId;
    }

    private void addNode(Node newNode) {
        nodes.put(newNode.cell.getCoordinates(), newNode);
    }

    public void add(BoardCell cell) {
        if (belongsTo != null) {
            new IllegalStateException("Only unmarked cells may be added");
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

