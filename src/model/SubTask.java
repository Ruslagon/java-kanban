package model;

import java.util.Objects;

import static model.Tasks.SUBTASK;
import static model.Tasks.TASK;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Status status, int epicId, String startTime, long durationMinutes) {
        super(name, description, status, startTime, durationMinutes);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Status status, int epicId, int id) {
        super(name, description, status, id);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Status status, int epicId, int id, String startTime, long durationMinutes) {
        super(name, description, status, id, startTime, durationMinutes);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        String startTimeString;
        if (startTime != null){
            startTimeString = startTime.format(formatter());
        } else {
            startTimeString = "null";
        }
        String data = "SubTask{" +
                "epicId=" + epicId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", startTime=" + startTimeString +
                ", duration=" + duration.toMinutes() +
                ", endTime=";
        if (getEndTime().isPresent()) {
            data = data + getEndTime().get().format(formatter());
        } else {
            data = data + "null";
        }
        return data + '}' + "\n";
    }

    @Override
    public String toStringForFile(){
        return super.toStringForFile() + Integer.toString(epicId);
    }

    public Tasks getType() {
        return SUBTASK ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
