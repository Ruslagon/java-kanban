package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int freeId = 1;
    HashMap<Integer, Task> taskMap;
    HashMap<Integer, Epic> epicMap;
    HashMap<Integer, SubTask> subTaskMap;
    HashMap<Integer, ArrayList<Integer>> epicToSubTasksId;

    public Manager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subTaskMap = new HashMap<>();
        epicToSubTasksId = new HashMap<>();
    }

    // HashMap сделаны с ключем индексом к задаче
    public HashMap<Integer, Task> getTaskMap() {
        return taskMap;
    }

    public HashMap<Integer, Epic> getEpicMap() {
        return epicMap;
    }

    public HashMap<Integer, SubTask> getSubTaskMap() {
        return subTaskMap;
    }

    public void clearAllMap() {
        clearTaskMap();
        clearEpicMap();
        clearSubTaskMap();
    }

    public void clearTaskMap() {
        taskMap.clear();
    }

    public void clearEpicMap() {
        epicMap.clear();
    }

    public void clearSubTaskMap() {
        subTaskMap.clear();
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

    public void setOneTask(Object someTask) {
        String classType = String.valueOf(someTask.getClass());
        switch (classType) {
            case ("class model.Task"):
                putTask((Task)someTask);
                break;
            case ("class model.Epic"):
                putEpic((Epic)someTask);
                break;
            case ("class model.SubTask"):
                putSubTask((SubTask)someTask);
                break;
            default:
                System.out.println("Введён неверный объект");
        }
    }

    public void putTask(Task task) {
        taskMap.put(freeId, task);
        freeId++;
    }

    public void putEpic(Epic epic) {
        epicMap.put(freeId, epic);
        autoEpicStatus(freeId);
        freeId++;
    }

    public void putSubTask(SubTask subTask){
        subTaskMap.put(freeId, subTask);
        Epic epic = epicMap.get(subTask.getEpicId());
        epic.addSubTaskId(freeId);
        autoEpicStatus(getEpicId(epic));
        freeId++;
    }

    public void autoEpicStatus(int id) {
        boolean isSubTaskNew = false;
        boolean isSubTaskDone = false;
        boolean isSubTaskInProgress = false;
        Epic epic = epicMap.get(id);
        ArrayList<Integer> subTaskList = epic.getSubTaskIdList();
        for (Integer subId : subTaskList) {
            String subStatus = subTaskMap.get(subId).getStatus();
            switch (subStatus){
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

    public void updateOneTask(int id, Object someTask){
        String classType = String.valueOf(someTask.getClass());
        switch (classType) {
            case ("class model.Task"):
                updateTask(id, (Task)someTask);
                break;
            case ("class model.Epic"):
                updateEpic(id, (Epic)someTask);
                break;
            case ("class model.SubTask"):
                updateSubTask(id, (SubTask)someTask);
                break;
            default:
                System.out.println("Введён неверный объект");
        }
    }

    public void updateTask(int id,Task task) {
        if (taskMap.containsKey(id)) {
            taskMap.put(id, task);
        } else {
            System.out.println("Ошибка в id");
        }
    }

    public void updateEpic(int id, Epic epic) {
        if (epicMap.containsKey(id)) {
            ArrayList<Integer> subTaskId = epicMap.get(id).getSubTaskIdList();
            epic.setSubTaskIdList(subTaskId);
            epicMap.put(id, epic);
            autoEpicStatus(id);
        } else {
            System.out.println("Ошибка в id");
        }
    }

    public void updateSubTask(int id, SubTask subTask) {
        if (subTaskMap.containsKey(id)) {
            subTaskMap.put(id, subTask);
            autoEpicStatus(subTask.getEpicId());
        } else {
            System.out.println("Ошибка в id");
        }
    }

    public void deleteById(int id) {
        if (taskMap.containsKey(id)) {
            taskMap.remove(id);
        } else if (epicMap.containsKey(id)) {
            for (Integer subTaskId : epicMap.get(id).getSubTaskIdList()) {
                subTaskMap.remove(subTaskId);
            }
            epicMap.remove(id);
        } else if (subTaskMap.containsKey(id)) {
            int epicId = subTaskMap.get(id).getEpicId();
            subTaskMap.remove(id);
            epicMap.get(epicId).removeOneSubTask(id);
            autoEpicStatus(epicId);
        } else {
            System.out.println("Задачи с таким id нет");
        }
    }

    public ArrayList<SubTask> getEpicsSubTasks(Epic epic) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        for (Integer subTaskId : epic.getSubTaskIdList()) {
            subTasks.add(subTaskMap.get(subTaskId));
        }
        return subTasks;
    }

    public ArrayList<SubTask> getEpicsSubTasksByID(int epicId) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        for (Integer subTaskId : epicMap.get(epicId).getSubTaskIdList()) {
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
        System.out.println("такого обьекта нет");
        return 0;
    }


}
