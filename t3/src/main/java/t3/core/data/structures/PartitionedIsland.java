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
 * makes traversal a Log N task. In case of single-partitioned nodes the task is reduced at aggregator level
 * as there is no need to go over all of them to figure there is no winning combination which is done.
 */
public class PartitionedIsland {
    private Map<BoardCellCoordinates, Node> nodes = new TreeMap();
    private Map<BoardCellCoordinates, PartitionedIsland> islandMap = new TreeMap();
    private Node head;
    private Node tail;
    private PartitionedIsland west, east;
    private Integer belongsTo;
    private BoardCellCoordinates westernTip;

    private PartitionedIsland(Node node, Integer playerId, PartitionedIsland partitionedIsland) {
        nodes.put(node.cell.getCoordinates(), node);
        head = node;
        tail = node;
        belongsTo = playerId;
        this.islandMap = partitionedIsland.islandMap;
        this.westernTip = partitionedIsland.westernTip;
        islandMap.put(node.cell.getCoordinates(), this);
    }

    public PartitionedIsland() {
        //
    }

    /**
     * Insert before the head and removes link to the other partition.
     *TODO bug fix
     */
    PartitionedIsland mergeFromWest() {
        if (west == null) {
            throw new IllegalStateException("No western partition to merge with");
        }
        Node newHead = west.tail;
        head.previous = newHead;
        if (west.getSize() > 1) {
            west.shrinkTail();
        } else {
            west = west.west;
            if (west != null) {
                west.east = this;
            }
        }
        newHead.next = head;
        newHead.previous = null;
        head = newHead;
        nodes.put(newHead.cell.getCoordinates(), newHead);
        islandMap.put(newHead.cell.getCoordinates(), this);
        return this;
    }

    private void shrinkTail() {
        nodes.remove(tail.cell.getCoordinates());// shrink it
        if (tail.previous == null) {
            east.west = west;
            east = null;
            return;
        }
        tail.previous.next = null;
        tail = tail.previous;
    }

    private void shrinkHead() {
        nodes.remove(head.cell.getCoordinates());// shrink it
        head.next.previous = null;
        head = head.next;
    }

    /**
     * Takes over a small tip of unallocated space or merges to the other partition of this player.
     *
     */
    private void mergeFromEast() {
        if (east == null) {
            throw new IllegalStateException("No eastern partition to merge with");
        }
        Node newTail = east.head;
        tail.next = newTail;
        east.shrinkHead();
        newTail.previous = tail;
        newTail.next = null;
        tail = newTail;
        nodes.put(newTail.cell.getCoordinates(), newTail);
        islandMap.put(newTail.cell.getCoordinates(), this);
    }

    /**
     * Separates western tip from this partition.
     */
    private PartitionedIsland chopWesternTip(int forPlayerId) {
        Node oldHead = head;
        head = head.next;
        PartitionedIsland newWest = new PartitionedIsland(oldHead, forPlayerId, this);
        islandMap.put(oldHead.getCoordinates(), newWest);
        oldHead.next = null;
        if (head != null) {
            head.previous = null;
        }
        nodes.remove(oldHead.cell.getCoordinates());
        newWest.east = this;
        this.west = newWest;
        return newWest;
    }

    /**
     * Chops the tail and connects with a break away partition.
     * @param forPlayerId
     */
    private void chopEasternTip(int forPlayerId) {
        if (getSize() == 1) {
            belongsTo = forPlayerId;
            return;
        }
        Node oldTail = tail;
        tail = tail.previous;
        PartitionedIsland newEast = new PartitionedIsland(oldTail, forPlayerId, this);
        islandMap.put(oldTail.getCoordinates(), newEast);
        oldTail.previous = null;
        if (tail != null) {
            tail.next = null;
        }
        nodes.remove(oldTail.cell.getCoordinates());
        newEast.west = this;
        this.east = newEast;
    }

    public void absorbNeighbours(int newPlayerId) {
        if (getSize() == 1) {
            belongsTo = newPlayerId;
        }
        if (west != null && west.belongsTo != null && west.belongsTo == newPlayerId) {
            west.tail.next = head;//connecting
            head.previous = west.tail;
            west.tail = tail;//stretching
            Node absorbedNode = head;
            do {
                west.addNode(absorbedNode);
                absorbedNode = absorbedNode.next;
            } while (absorbedNode != null);
            west.east = east;
        }
    }

    /**
     * This splits, merges and creates new partitions and may return new reference to this island.
     * //TODO refactor this as reference is no longer needs to be returned
     * @param coordinates taken cell coordinates
     * @param newPlayerId
     */
    public PartitionedIsland repartition(BoardCellCoordinates coordinates, int newPlayerId) {
        PartitionedIsland currentPartition = getPartition(coordinates);
        Node node = currentPartition.nodes.get(coordinates);
        if (node == null) {
            throw new IllegalArgumentException("Unknown node " + coordinates.toString());
        }
        PartitionedIsland easternIsland = currentPartition.east;
        PartitionedIsland westernIsland = currentPartition.west;
        if (node.isTail()) {// is eastern edge of the original partition?
            if (easternIsland != null) {
                if (easternIsland.belongsTo == newPlayerId) {
                    easternIsland.mergeFromWest();
                }
                getPartition(coordinates).absorbNeighbours(newPlayerId);
                return getPartition(coordinates);
            }
            currentPartition.chopEasternTip(newPlayerId);
            getPartition(coordinates).absorbNeighbours(newPlayerId);
            return getPartition(coordinates);
        }
        if (node.isHead()) {// is western edge of the original partition?
            if (westernIsland != null) {
                if (westernIsland.belongsTo == newPlayerId) {
                    westernIsland.mergeFromEast();
                    getPartition(coordinates).absorbNeighbours(newPlayerId);
                    return getPartition(coordinates);
                }
            }
            currentPartition.chopWesternTip(newPlayerId);
            return getPartition(coordinates);
        }
        currentPartition.splitInThree(node, newPlayerId);
        return getPartition(coordinates);
    }

    /**
     * Called whenever one node is taken in the interior of an unoccupied segment. This results in three nodes
     * with the taken partition in between and two free partitions on the sides.
     *
     * @param middleNode taken node
     * @param newPlayerId who took it
     */
    private void splitInThree(Node middleNode, int newPlayerId) {
        if (middleNode.isHead() || middleNode.isTail()) {
            return;
        }
        PartitionedIsland middlePartition = new PartitionedIsland(middleNode, newPlayerId, this);
        Node easternPartitionTip = middleNode.next;
        easternPartitionTip.previous = null;
        middleNode.next = null;//disconnect east and center
        PartitionedIsland easternPartition = new PartitionedIsland(easternPartitionTip, belongsTo, this);
        easternPartition.tail = tail;
        nodes.remove(middleNode.cell.getCoordinates());
        tail = middleNode.previous;
        tail.next = null;
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
    }

    /**
     * Populates new partition in node by node fashion during repartitioning.
     * @param newNode
     */
    private void addNode(Node newNode) {
        nodes.put(newNode.cell.getCoordinates(), newNode);
        islandMap.put(newNode.cell.getCoordinates(), this);
    }

    /**
     * Beef up from Iterable.
     *
     * @param cells
     */
    public void addAll(Iterable<BoardCell> cells) {
        for (BoardCell boardCell : cells) {
            add(boardCell);
        }
    }

    /**
     * One by one initialization method.
     *
     * @param cell
     */
    public void add(BoardCell cell) {
        if (belongsTo != null) {
            new IllegalStateException("Only unmarked cells may be added");
        }
        if (nodes.containsKey(cell.getCoordinates())) {
            new IllegalArgumentException("Loop prevented");
        }
        if (westernTip == null) {
            westernTip = cell.getCoordinates();
        }
        Node newNode = new Node(cell);
        nodes.put(cell.getCoordinates(), newNode);
        islandMap.put(cell.getCoordinates(), this);
        if (head == null) {
            head = newNode;
            tail = newNode;
            return;
        }
        tail.next = newNode;
        newNode.previous = tail;
        tail = newNode;
    }

    /**
     * Node of double linked list which is wrapper for BoardCell.
     */
    public class Node {
        private BoardCell cell;
        private Node previous, next;

        Node(BoardCell cell) {
            if (cell == null) {
                throw new IllegalArgumentException("Null cell");
            }
            this.cell = cell;
        }

        @Override
        public String toString() {
            return cell.toString();
        }

        public boolean isHead() {
            return previous == null;
        }

        public boolean isTail() {
            return next == null;
        }

        public BoardCellCoordinates getCoordinates() {
            return cell.getCoordinates();
        }
    }

    public Node getHead() {
        return head;
    }

    public Node getTail() {
        return tail;
    }

    public PartitionedIsland getWest() {
        return west;
    }

    public PartitionedIsland getEast() {
        return east;
    }

    public Integer getBelongsTo() {
        return belongsTo;
    }

    public boolean isFree() {
        return belongsTo == null;
    }

    public int getSize() {
        return nodes.size();
    }

    @Override
    public String toString() {
        if (head == tail)
            return "{" + head + ", owner=" + belongsTo + '}';

        return "{" + head + "-" + tail + ", owner=" + belongsTo + '}';
    }

    /**
     * Used to retrieve the entire partition where the coordinates belong.
     *
     * @param coordinates
     * @return containing partition
     */
    public PartitionedIsland getPartition(BoardCellCoordinates coordinates) {
        return islandMap.get(coordinates);
    }

    public PartitionedIsland getWestmostPoint() {
        return islandMap.get(westernTip);
    }
}

