package model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIdList;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subTaskIdList = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description, Status.NEW, id);
        this.subTaskIdList = new ArrayList<>();
    }

    public void removeOneSubTask(int subTaskId){
        subTaskIdList.remove((Integer)subTaskId);
    }

    public void addSubTaskId(int id){
        subTaskIdList.add(id);
    }

    public ArrayList<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }

    public void setSubTaskIdList(ArrayList<Integer> subTaskIdList) {
        this.subTaskIdList = subTaskIdList;
    }

    @Override
    public String toString() {
        return "Epic{" +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status.name() + '\'' +
                ", subTaskId=" + subTaskIdList +
                '}' + "\n";
    }
}
