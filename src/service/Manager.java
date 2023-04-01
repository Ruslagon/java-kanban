package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int freeId = 1;
    private HashMap<Integer, Task> taskMap;
    private HashMap<Integer, Epic> epicMap;
    private HashMap<Integer, SubTask> subTaskMap;
    private HashMap<Integer, ArrayList<Integer>> epicToSubTasksId;

    public Manager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subTaskMap = new HashMap<>();
        epicToSubTasksId = new HashMap<>();
    }

    // HashMap сделаны с ключем индексом к задаче
    public HashMap<Integer, Task> getAllTasks() {
        return taskMap;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epicMap;
    }

    public HashMap<Integer, SubTask> getAllSubTasks() {
        return subTaskMap;
    }

    public void deleteAllTasks() {
        deleteAllSimpleTasks();
        deleteAllEpicMap();
        freeId = 1;
    }

    public void deleteAllSimpleTasks() {
        taskMap.clear();
    }

    public void deleteAllEpicMap() {
        epicMap.clear();
        subTaskMap.clear();
        epicToSubTasksId.clear();
    }

    public void deleteAllSubTasks() {
        subTaskMap.clear();
        for (Integer epicId : epicMap.keySet()) {
            epicToSubTasksId.put(epicId, new ArrayList<>());
            epicMap.get(epicId).setSubTaskIdList(new ArrayList<>());
            defineEpicStatus(epicId);
        }
    }

    public Task getTask(int id) {
        return taskMap.get(id);
    }

    public Epic getEpic(int id) {
        return epicMap.get(id);
    }

    public SubTask getSubTask (int id) {
        return subTaskMap.get(id);
    }

    public void createOneTask(Object someTask) {
        String classType = String.valueOf(someTask.getClass());
        switch (classType) {
            case ("class model.Task"):
                createTask((Task)someTask);
                break;
            case ("class model.Epic"):
                createEpic((Epic)someTask);
                break;
            case ("class model.SubTask"):
                createSubTask((SubTask)someTask);
                break;
            default:
        }
    }

    public void createTask(Task task) {
        task.setId(freeId);
        taskMap.put(freeId, task);
        freeId++;
    }

    public void createEpic(Epic epic) {
        epic.setId(freeId);
        epicMap.put(freeId, epic);
        defineEpicStatus(freeId);
        epicToSubTasksId.put(freeId, new ArrayList<>());
        freeId++;
    }

    public void createSubTask(SubTask subTask){
        subTask.setId(freeId);
        subTaskMap.put(freeId, subTask);

        int thisSubTaskEpicId = subTask.getEpicId();
        Epic epicOfThisSubTask = epicMap.get(thisSubTaskEpicId);
        epicOfThisSubTask.addSubTaskId(freeId);
        epicToSubTasksId.get(thisSubTaskEpicId).add(freeId);

        defineEpicStatus(getEpicId(epicOfThisSubTask));
        freeId++;
    }

    public void defineEpicStatus(int id) {
        boolean isSubTaskNew = false;
        boolean isSubTaskDone = false;
        boolean isSubTaskInProgress = false;
        Epic epic = epicMap.get(id);
        ArrayList<Integer> subTaskList = epic.getSubTaskIdList();
        for (Integer subId : subTaskList) {
            String subTaskStatus = subTaskMap.get(subId).getStatus();
            switch (subTaskStatus){
                case ("NEW"):
                    isSubTaskNew = true;
                    break;
                case ("DONE"):
                    isSubTaskDone = true;
                    break;
                case ("IN_PROGRESS"):
                    isSubTaskInProgress = true;
                    break;
                default:
            }
        }
        if ((isSubTaskNew && !(isSubTaskDone || isSubTaskInProgress)) ||
                (!isSubTaskNew && !isSubTaskDone && !isSubTaskInProgress)){
            epic.setStatus("NEW");
        } else if (isSubTaskDone && !(isSubTaskNew || isSubTaskInProgress)){
            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
    }

    public void updateOneTask(Object someTask){
        String classType = String.valueOf(someTask.getClass());
        switch (classType) {
            case ("class model.Task"):
                updateTask((Task)someTask);
                break;
            case ("class model.Epic"):
                updateEpic((Epic)someTask);
                break;
            case ("class model.SubTask"):
                updateSubTask((SubTask)someTask);
                break;
            default:
        }
    }

    public void updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            taskMap.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (epicMap.containsKey(id)) {
            ArrayList<Integer> subTaskId = epicToSubTasksId.get(id);
            epic.setSubTaskIdList(subTaskId);
            epicMap.put(id, epic);
            defineEpicStatus(id);
        }
    }

    public void updateSubTask(SubTask subTask) {
        int id = subTask.getId();
        if (subTaskMap.containsKey(id)) {
            subTaskMap.put(id, subTask);
            defineEpicStatus(subTask.getEpicId());
        }
    }

    public void deleteById(int id) {
        deleteTaskById(id);
        deleteEpicById(id);
        deleteSubTaskById(id);

    }

    public void deleteTaskById(int id){
        if (taskMap.containsKey(id)) {
            taskMap.remove(id);
        }
    }

    public void deleteEpicById(int id) {
        if (epicMap.containsKey(id)) {
            for (Integer subTaskId : epicToSubTasksId.get(id)) {
                subTaskMap.remove(subTaskId);
            }
            epicMap.remove(id);
        }
    }

    public void deleteSubTaskById(Integer id) {
        if (subTaskMap.containsKey(id)) {
            int epicId = subTaskMap.get(id).getEpicId();
            epicToSubTasksId.get(epicId).remove(id);
            epicMap.get(epicId).setSubTaskIdList(epicToSubTasksId.get(epicId));
            subTaskMap.remove(id);
            defineEpicStatus(epicId);
        }
    }

    public ArrayList<SubTask> getEpicsSubTasks(Epic epic) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        int epicId = 0;
        for (Epic epicOfMap : epicMap.values()) {
            if (epic.equals(epicOfMap)) {
                epicId = epicOfMap.getId();
                break;
            }
        }
        for (Integer subTaskId : epicToSubTasksId.get(epicId)) {
            subTasks.add(subTaskMap.get(subTaskId));
        }
        return subTasks;
    }

    public int getEpicId(Epic epicForCheck){
        for (Integer epicId: epicMap.keySet()) {
            if (epicForCheck.equals(epicMap.get(epicId))) {
                return epicId;
            }
        }
        return 0;
    }


}
