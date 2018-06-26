package t3.core.data.structures;

import java.util.Iterator;
import java.util.Set;

/**
 * Round-robin style scheduler to determine who takes the next turn.
 */
public class RoundRobin {
    private Node head;

    public RoundRobin(Set<Integer> list) {
        if (list.size() < 2) {
            throw new IllegalArgumentException("List must contain at least two items");
        }
        Iterator<Integer> iterator = list.iterator();
        Node previous = head = new Node(iterator.next());
        while (iterator.hasNext()) {
            previous.setNext(new Node(iterator.next()));
        }
        previous.setNext(head);
    }

    /**
     * Internal structure, not useful outside.
     */
    class Node {
        private final int id;
        private Node next;

        public Node(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public Node getNext() {
            return next;
        }

        void setNext(Node next) {
            this.next = next;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "id=" + id +
                    '}';
        }
    }

    /**
     * For testing
     * @return
     */
    Node getHead() {
        return head;
    }

    /**
     * Passes the turn.
     * @return passes the turn and return player's id
     */
    public Integer passTurn() {
        head = head.getNext();
        return head.getId();
    }

    /**
     * Returns whoc is playing next without passing the turn.
     * @return next player
     */
    public Integer getNextPlayer() {
        return head.getNext().getId();
    }

}
