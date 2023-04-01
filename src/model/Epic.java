package model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIdList;

    public Epic(String name, String description) {
        super(name, description, "NEW");
        this.subTaskIdList = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description, "NEW", id);
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

    @Override
    public String toString() {
        return "Epic{" +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", subTaskId=" + subTaskIdList +
                '}' + "\n";
    }
}
