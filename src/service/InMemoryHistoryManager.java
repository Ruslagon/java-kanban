package service;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{

    private ArrayList<Task> tasksHistory = new ArrayList<>();


    @Override
    public ArrayList<Task> getHistory(){
        return tasksHistory;
    }

    @Override
    public void add(Task task){
        if (tasksHistory.size() == 10) {
            tasksHistory.remove(0);
        }
        tasksHistory.add(task);
    }

}
