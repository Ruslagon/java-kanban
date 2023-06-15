package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<SubTask> getAllSubTasks();

    void deleteAllTasks();
    void deleteAllSimpleTasks();
    void deleteAllEpicMap();
    void deleteAllSubTasks();

    Task getTask(int id);
    Epic getEpic(int id);
    SubTask getSubTask(int id);
    ArrayList<SubTask> getEpicsSubTasks(Epic epic);
    ArrayList<SubTask> getEpicsSubTasks(int id);


    void createTask(Task task);
    void createEpic(Epic epic);
    void createSubTask(SubTask subTask);


    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubTask(SubTask subTask);

    void deleteTaskById(int id);
    void deleteEpicById(int epicId);
    void deleteSubTaskById(Integer subTaskId);

    List<Task> getHistory();

    public List<Task> getTasksByPriority();
}