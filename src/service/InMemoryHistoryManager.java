package service;

import model.Task;
import tools.CustomLinkedList;
import tools.Node;

import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    final private HashMap<Integer, Node<Task>> idByViewedTasks = new HashMap<>();
    CustomLinkedList customLinkedList = new CustomLinkedList();


    @Override
    public List<Task> getHistory(){
        return customLinkedList.getTasks();
    }

    @Override
    public void add(Task task){
        int taskId = task.getId();
        if (idByViewedTasks.containsKey(taskId)){
            customLinkedList.removeNode(idByViewedTasks.get(taskId));
        }
        Node<Task> taskNode = customLinkedList.linkLast(task);
        idByViewedTasks.put(taskId,taskNode);
    }

    @Override
    public void remove(int id){
        if (idByViewedTasks.containsKey(id)){
            customLinkedList.removeNode(idByViewedTasks.get(id));
            idByViewedTasks.remove(id);
        }
    }

}
