package service;


import model.Epic;
import model.SubTask;
import model.Task;
import model.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int freeId = 1;
    final private HashMap<Integer, Task> taskMap;
    final private HashMap<Integer, Epic> epicMap;
    final private HashMap<Integer, SubTask> subTaskMap;

    final private HistoryManager historyManager = new Managers().getDefaultHistory();

    public InMemoryTaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subTaskMap = new HashMap<>();
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

    @Override
    public void deleteAllTasks() {
        deleteAllSimpleTasks();
        deleteAllEpicMap();
        freeId = 1;
    }

    @Override
    public void deleteAllSimpleTasks() {
        taskMap.clear();
    }

    @Override
    public void deleteAllEpicMap() {
        epicMap.clear();
        subTaskMap.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTaskMap.clear();
        for (Integer epicId : epicMap.keySet()) {
            epicMap.get(epicId).setSubTaskIdList(new ArrayList<>());
            defineEpicStatus(epicId);
        }
    }

    @Override
    public Task getTask(int id) {
        addToHistory(taskMap.get(id));
        return taskMap.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        addToHistory(epicMap.get(id));
        return epicMap.get(id);
    }

    @Override
    public SubTask getSubTask (int id) {
        addToHistory(subTaskMap.get(id));
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

    @Override
    public void createTask(Task task) {
        task.setId(freeId);
        taskMap.put(freeId, task);
        freeId++;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(freeId);
        epicMap.put(freeId, epic);
        defineEpicStatus(freeId);
        freeId++;
    }

    @Override
    public void createSubTask(SubTask subTask){
        subTask.setId(freeId);
        subTaskMap.put(freeId, subTask);

        int thisSubTaskEpicId = subTask.getEpicId();
        Epic epicOfThisSubTask = epicMap.get(thisSubTaskEpicId);
        epicOfThisSubTask.addSubTaskId(freeId);

        defineEpicStatus(thisSubTaskEpicId);
        freeId++;
    }

    private void defineEpicStatus(int id) {
        boolean isSubTaskNew = false;
        boolean isSubTaskDone = false;
        boolean isSubTaskInProgress = false;
        Epic epic = epicMap.get(id);
        ArrayList<Integer> subTaskList = epic.getSubTaskIdList();
        for (Integer subId : subTaskList) {
            Status subTaskStatus = subTaskMap.get(subId).getStatus();
            switch (subTaskStatus){
                case NEW:
                    isSubTaskNew = true;
                    break;
                case DONE:
                    isSubTaskDone = true;
                    break;
                case IN_PROGRESS:
                    isSubTaskInProgress = true;
                    break;
                default:
            }
        }
        if ((isSubTaskNew && !(isSubTaskDone || isSubTaskInProgress)) ||
                (!isSubTaskNew && !isSubTaskDone && !isSubTaskInProgress)){
            epic.setStatus(Status.NEW);
        } else if (isSubTaskDone && !(isSubTaskNew || isSubTaskInProgress)){
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
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

    @Override
    public void updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            taskMap.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int epicId = epic.getId();
        if (epicMap.containsKey(epicId)) {
            ArrayList<Integer> subTaskIdList = epicMap.get(epicId).getSubTaskIdList();
            epic.setSubTaskIdList(subTaskIdList);
            epicMap.put(epicId, epic);
            defineEpicStatus(epicId);
        }
    }

    @Override
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

    @Override
    public void deleteTaskById(int id){
        taskMap.remove(id);
    }

    @Override
    public void deleteEpicById(int epicId) {
        if (epicMap.containsKey(epicId)) {
            for (Integer subTaskId : epicMap.get(epicId).getSubTaskIdList()) {
                subTaskMap.remove(subTaskId);
            }
            epicMap.remove(epicId);
        }
    }

    @Override
    public void deleteSubTaskById(Integer subTaskId) {
        if (subTaskMap.containsKey(subTaskId)) {
            int epicId = subTaskMap.get(subTaskId).getEpicId();
            epicMap.get(epicId).removeOneSubTask(subTaskId);
            subTaskMap.remove(subTaskId);
            defineEpicStatus(epicId);
        }
    }

    @Override
    public ArrayList<SubTask> getEpicsSubTasks(Epic epic) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        int epicId = epic.getId();
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
        return 0;
    }

    @Override
    public List<Task> getHistory(){
        return historyManager.getHistory();
    }

    private void addToHistory(Task task){
        historyManager.add(task);
    }

    public void removeFromHistory(int id){
        historyManager.remove(id);
    }

}
