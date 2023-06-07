package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import static model.Tasks.*;

public class Task {
    protected String name;
    protected String description;
    protected Status status;
    protected int id;

    protected LocalDateTime startTime;

    protected Duration duration = Duration.ofMinutes(0);

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, Status status, String startTime, long durationMinutes) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = LocalDateTime.parse(startTime, formatter);
        this.duration = Duration.ofMinutes(durationMinutes);
    }

    public Task(String name, String description, Status status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Task(String name, String description, Status status, int id, String startTime, long durationMinutes) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.startTime = LocalDateTime.parse(startTime, formatter);
        this.duration = Duration.ofMinutes(durationMinutes);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Tasks getType() {
        return TASK ;
    }

    public String toStringForFile(){
        String startTimeString;
        String endTimeString;
        if (startTime != null){
            startTimeString = startTime.format(formatter);
        } else {
            startTimeString = "null";
        }
        if (getEndTime().isPresent()){
            endTimeString = startTime.format(formatter);
        } else {
            endTimeString = "null";
        }
        return String.join(",", Integer.toString(id), getClass().getSimpleName().toUpperCase(),
                name, status.name(), description, startTimeString, String.valueOf(duration.toMinutes()),endTimeString,"");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return name.equals(task.name) && description.equals(task.description)
                && status.equals(task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status);
    }

    public Optional<LocalDateTime> getEndTime() {
        if (startTime == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(startTime.plus(duration));
        }
    }

    @Override
    public String toString() {
        String startTimeString;
        if (startTime != null){
            startTimeString = startTime.format(formatter);
        } else {
            startTimeString = "null";
        }
        String data = "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", startTime=" + startTimeString +
                ", duration=" + duration.toMinutes() +
                ", endTime=";
        if (getEndTime().isPresent()) {
            data = data + getEndTime().get().format(formatter);
        } else {
            data = data + "null";
        }
        return data + '}' + "\n";
    }

    public Optional<LocalDateTime> getStartTime() {return Optional.ofNullable(startTime);}

    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }
}
