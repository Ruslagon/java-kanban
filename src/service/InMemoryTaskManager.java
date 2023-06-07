package service;


import model.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    int freeId = 1;
    final Map<Integer, Task> taskMap;
    final Map<Integer, Epic> epicMap;
    final Map<Integer, SubTask> subTaskMap;

    final HistoryManager historyManager = Managers.getDefaultHistory();

    final NavigableSet<Task> tasksByPriority;

    final Comparator<Task> taskComparator = (Task o1, Task o2) -> {
        if (o1.getStartTime().isEmpty() && o2.getStartTime().isEmpty()) {
            return o1.getId() - o2.getId();
        } else if (o1.getStartTime().isEmpty()) {
            return -1;
        } else if (o2.getStartTime().isEmpty()) {
            return 1;
        } else if (o1.getStartTime().get().isBefore(o2.getStartTime().get())) {
            return -1;
        } else if (o1.getStartTime().get().isEqual(o2.getStartTime().get())) {
            return 0;
        } else {
            return 1;
        }
    };

    public InMemoryTaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subTaskMap = new HashMap<>();
        tasksByPriority = new TreeSet<>(taskComparator);
    }

    // HashMap сделаны с ключом индексом к задаче
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        if (!taskMap.isEmpty()){
            tasks.addAll(taskMap.values());
        }
        return tasks;
    }

    public List<Epic> getAllEpics() {
        List<Epic> epics = new ArrayList<>();
        if (!epicMap.isEmpty()){
            epics.addAll(epicMap.values());
        }
        return epics;
    }

    public List<SubTask> getAllSubTasks() {
        List<SubTask> subTasks = new ArrayList<>();
        if (!subTaskMap.isEmpty()){
            subTasks.addAll(subTaskMap.values());
        }
        return subTasks;
    }

    @Override
    public void deleteAllTasks() {
        deleteAllSimpleTasks();
        deleteAllEpicMap();
        freeId = 1;
    }

    @Override
    public void deleteAllSimpleTasks() {
        for (Integer taskId : taskMap.keySet()) {
            removeFromHistory(taskId);
            tasksByPriority.remove(taskMap.get(taskId));
        }
        taskMap.clear();
    }

    @Override
    public void deleteAllEpicMap() {
        for (Integer epicId : epicMap.keySet()) {
            removeFromHistory(epicId);
        }
        epicMap.clear();

        for (SubTask subTask : subTaskMap.values()) {
            removeFromHistory(subTask.getId());
            tasksByPriority.remove(subTask);
        }
        subTaskMap.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (SubTask subTask : subTaskMap.values()) {
            removeFromHistory(subTask.getId());
            tasksByPriority.remove(subTask);
        }
        subTaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.setSubTaskIdList(new ArrayList<>());
            epic.clearTime();
            defineEpicStatus(epic.getId());
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

    boolean addToTasksByPriority(Task task) {
        Optional<Task> lowerTask = Optional.ofNullable(tasksByPriority.lower(task));
        Optional<Task> higherTask = Optional.ofNullable(tasksByPriority.higher(task));

        if (task.getStartTime().isEmpty() || (lowerTask.isEmpty() && higherTask.isEmpty())) {
            tasksByPriority.add(task);
            return true;
        }

        if (lowerTask.isEmpty()) {
            if (higherTask.get().getStartTime().get().isBefore(task.getEndTime().get())) {
                return false;
            } else {
                tasksByPriority.add(task);
                return true;
            }
        }

        if (higherTask.isEmpty()) {
            if (lowerTask.get().getEndTime().isPresent() && lowerTask.get().getEndTime().get().isAfter(task.getStartTime().get())) {
                return false;
            } else {
                tasksByPriority.add(task);
                return true;
            }
        }

        if (lowerTask.get().getEndTime().isPresent() && higherTask.get().getStartTime().isPresent() &&
                lowerTask.get().getEndTime().get().isBefore(task.getStartTime().get()) &&
                higherTask.get().getStartTime().get().isAfter(task.getEndTime().get())){
            tasksByPriority.add(task);
            return true;
        } else if (lowerTask.get().getEndTime().isEmpty() &&
                higherTask.get().getStartTime().get().isAfter(task.getEndTime().get())) {
            tasksByPriority.add(task);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void createTask(Task task) {
        if (task != null) {
            task.setId(freeId);

            if (addToTasksByPriority(task)) {
                taskMap.put(freeId, task);
                freeId++;
            }
        }
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic != null) {
            epic.setId(freeId);
            epicMap.put(freeId, epic);
            defineEpicStatus(freeId);
            freeId++;
        }
    }

    @Override
    public void createSubTask(SubTask subTask){
        if (subTask != null) {
            subTask.setId(freeId);
            if (addToTasksByPriority(subTask) && epicMap.containsKey(subTask.getEpicId())) {
                subTaskMap.put(freeId, subTask);

                int thisSubTaskEpicId = subTask.getEpicId();
                Epic epicOfThisSubTask = epicMap.get(thisSubTaskEpicId);
                epicOfThisSubTask.addSubTaskId(freeId);
                defineEpicTime(subTask, epicOfThisSubTask);

                defineEpicStatus(thisSubTaskEpicId);
                freeId++;
            }
        }
    }

    void defineEpicStatus(int id) {
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

    void defineEpicTime(SubTask subTask, Epic epic) {
        if (subTask.getStartTime().isPresent()) {
            if (epic.getStartTime().isEmpty()) {
                epic.setStartTime(subTask.getStartTime().get());
            } else if (subTask.getStartTime().get().isBefore(epic.getStartTime().get())) {
                epic.setStartTime(subTask.getStartTime().get());
            }
            epic.addDuration(subTask.getDuration().get());

            if (epic.getEndTime().isEmpty()) {
                epic.setEndTime(subTask.getEndTime().get());
            } else if (epic.getEndTime().get().isBefore(subTask.getEndTime().get())) {
                epic.setEndTime(subTask.getEndTime().get());
            }
        }
    }

    public void updateOneTask(Task someTask){
        Tasks taskType = someTask.getType();
        switch (taskType) {
            case TASK:
                updateTask(someTask);
                break;
            case EPIC:
                updateEpic((Epic)someTask);
                break;
            case SUBTASK:
                updateSubTask((SubTask)someTask);
                break;
            default:
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && taskMap.containsKey(task.getId())) {
            Task oldTask = taskMap.get(task.getId());
            tasksByPriority.remove(oldTask);
            if (addToTasksByPriority(task)) {
                taskMap.put(task.getId(), task);
            } else {
                tasksByPriority.add(oldTask);
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            int epicId = epic.getId();
            if (epicMap.containsKey(epicId)) {
                ArrayList<Integer> subTaskIdList = epicMap.get(epicId).getSubTaskIdList();
                epic.setSubTaskIdList(subTaskIdList);
                epicMap.put(epicId, epic);
                defineEpicStatus(epicId);
            }
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int id = subTask.getId();
        if (subTaskMap.containsKey(id)) {
            SubTask oldSubTask = subTaskMap.get(id);
            tasksByPriority.remove(oldSubTask);
            if (addToTasksByPriority(subTask)) {
                Epic epicOfSubTask = epicMap.get(subTask.getEpicId());
                subTaskMap.put(id, subTask);
                defineEpicStatus(subTask.getEpicId());

                subTask.getDuration().ifPresent(epicOfSubTask::deductDuration);
                if (epicOfSubTask.getStartTime().isEmpty() || oldSubTask.getStartTime().isEmpty()) {
                    defineEpicTime(subTask, epicOfSubTask);
                } else {
                    epicOfSubTask.clearTime();
                    epicOfSubTask.getSubTaskIdList().stream()
                            .map(subTaskMap::get)
                            .forEach(subTaskOfEpic -> defineEpicTime(subTaskOfEpic, epicOfSubTask));
                }
            } else {
                tasksByPriority.add(oldSubTask);
            }
        }
    }

    public void deleteById(int id) {
        deleteTaskById(id);
        deleteEpicById(id);
        deleteSubTaskById(id);

    }

    @Override
    public void deleteTaskById(int id){
        if (taskMap.containsKey(id)) {
            removeFromHistory(id);
            tasksByPriority.remove(taskMap.get(id));
            taskMap.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int epicId) {
        if (epicMap.containsKey(epicId)) {
            for (Integer subTaskId : epicMap.get(epicId).getSubTaskIdList()) {
                tasksByPriority.remove(subTaskMap.get(subTaskId));
                subTaskMap.remove(subTaskId);
                removeFromHistory(subTaskId);
            }
            epicMap.remove(epicId);
            removeFromHistory(epicId);
        }
    }

    @Override
    public void deleteSubTaskById(Integer subTaskId) {
        if (subTaskMap.containsKey(subTaskId)) {
            int epicId = subTaskMap.get(subTaskId).getEpicId();
            epicMap.get(epicId).removeOneSubTask(subTaskId);
            subTaskMap.remove(subTaskId);
            defineEpicStatus(epicId);
            epicMap.get(epicId).clearTime();
            epicMap.get(epicId).getSubTaskIdList().stream()
                    .map(subTaskMap::get)
                    .forEach(subTaskOfEpic -> defineEpicTime(subTaskOfEpic, epicMap.get(epicId)));
            removeFromHistory(subTaskId);
        }
    }

    @Override
    public ArrayList<SubTask> getEpicsSubTasks(Epic epic) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        if ((epic == null) || (epic.getId() == 0) || epic.getSubTaskIdList() == null) {
            return subTasks;
        }
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

    void addToHistory(Task task){
        historyManager.add(task);
    }

    private void removeFromHistory(int id){
        historyManager.remove(id);
    }

    public List<Task> getTasksByPriority() {
        List<Task> list = new ArrayList<>(tasksByPriority);
        return list;
    }

}
