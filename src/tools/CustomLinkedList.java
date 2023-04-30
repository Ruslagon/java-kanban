package tools;
import model.Task;

import java.util.ArrayList;

public class CustomLinkedList {
    private Node<Task> head;
    private Node<Task> tail;

    public ArrayList<Task> getTasks(){
        Node<Task> currentNode = head;
        ArrayList<Task> tasks = new ArrayList<>();

        while (currentNode != null){
            tasks.add(currentNode.data);
            currentNode = currentNode.next;
        }

        return tasks;
    }

    public void removeNode(Node<Task> node){
        Node<Task> prev = node.prev;
        Node<Task> next = node.next;

        if (node.equals(head)){
            head = next;
        }
        if (node.equals(tail)){
            tail = prev;
        }

        node = null;

        if (prev != null) {
            prev.next = next;
        }
        if (next != null) {
            next.prev = prev;
        }

    }

    public Node<Task> linkLast(Task element){
        Node<Task> oldTail = tail;
        Node<Task> newNode = new Node<>(oldTail, element, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        return newNode;
    }
}
