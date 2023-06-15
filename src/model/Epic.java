package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static model.Tasks.EPIC;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIdList;

    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subTaskIdList = new ArrayList<>();
        this.duration = Duration.ofMinutes(0);
    }

    public Epic(String name, String description, int id) {
        super(name, description, Status.NEW, id);
        this.subTaskIdList = new ArrayList<>();
        this.duration = Duration.ofMinutes(0);
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

    public Tasks getType() {
        return EPIC;
    }

    @Override
    public String toString() {
        String startTimeString;
        String endTimeString;
        if (startTime != null){
            startTimeString = startTime.format(formatter());
        } else {
            startTimeString = "null";
        }
        if (getEndTime().isPresent()) {
            endTimeString = getEndTime().get().format(formatter());
        } else {
            endTimeString = "null";
        }
        return "Epic{" +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTimeString +
                ", duration=" + duration.toMinutes() +
                ", endTime=" + endTimeString +
                ", subTaskIdList=" + subTaskIdList +
                '}' + "\n";
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {this.endTime = endTime;}

    public void addDuration(Duration duration) {this.duration = duration.plus(duration);}

    public void deductDuration(Duration duration) {this.duration = duration.minus(duration);}

    public void clearTime() {startTime = null;
    endTime = null;
    duration = Duration.ofMinutes(0);}

    @Override
    public Optional<LocalDateTime> getEndTime() {return Optional.ofNullable(endTime);}
}
