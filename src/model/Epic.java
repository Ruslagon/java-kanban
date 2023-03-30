package model;

import java.util.ArrayList;

public class Epic extends Task {

    protected ArrayList<Integer> subTaskIdList;

    public Epic(String name, String description, String status) {
        super(name, description, status);
        this.subTaskIdList = new ArrayList<>();
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

    public void removeOneSubTask(int subTaskId){
        subTaskIdList.remove((Integer)subTaskId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", subTaskId=" + subTaskIdList +
                '}' + "\n";
    }
}
