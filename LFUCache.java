// Time Complexity for get and put: O(1)
// Space COmplexity: O(n)

class LFUCache {
    class Node {
        int key;
        int val;
        int count;
        Node next, prev;

        Node(int key, int val) {
            this.key = key;
            this.val = val;
            count = 1;
        }
    }

    class DLList {
        Node head, tail;
        int len;

        DLList() {
            head = new Node(0, 0);
            tail = new Node(0, 0);
            head.next = tail;
            tail.prev = head;
            len = 0;
        }

        public void addNode(Node node) {
            Node temp = head.next;
            head.next = node;
            temp.prev = node;
            node.next = temp;
            node.prev = head;
            len++;
            map.put(node.key, node);
        }

        public void removeNode(Node node) {
            Node prev = node.prev;
            Node next = node.next;
            prev.next = next;
            next.prev = prev;
            len--;
            map.remove(node.key);
        }

        public void removeTail() {
            Node node = tail.prev;
            removeNode(node);
        }
    }

    Map<Integer, Node> map;
    Map<Integer, DLList> freq;
    int capacity, size, maxFreq;
    public LFUCache(int capacity) {
        map = new HashMap<>();
        freq = new HashMap<>();
        this.capacity = capacity;
        size = 0;
        maxFreq = 0;
    }

    public int get(int key) {
        if(map.get(key) == null) {
            return -1;
        }
        Node node = map.get(key);
        int prevFreq = node.count;
        DLList prev = freq.get(prevFreq);
        prev.removeNode(node);


        int newFreq = prevFreq + 1;
        maxFreq = Math.max(maxFreq, newFreq);
        DLList cur = freq.getOrDefault(newFreq, new DLList());
        node.count++;
        cur.addNode(node);

        freq.put(newFreq, cur);
        freq.put(prevFreq, prev);
        return node.val;
    }

    public void put(int key, int value) {
        if(capacity == 0) {
            return;
        }
        if(map.get(key) != null) {
            map.get(key).val = value;
            get(key);
            return;
        }

        Node node = new Node(key, value);
        DLList cur = freq.getOrDefault(1, new DLList());
        cur.addNode(node);
        size++;

        if(size > capacity) {
            if(cur.len > 1) {
                cur.removeTail();
            } else {
                for(int i = 2; i <= maxFreq; i++) {
                    if(freq.get(i) != null && freq.get(i).len > 0) {
                        freq.get(i).removeTail();
                        break;
                    }
                }
            }
            size--;
        }
        freq.put(1, cur);
    }
}

/**
 * Your LFUCache object will be instantiated and called as such:
 * LFUCache obj = new LFUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */