package service;

import java.io.IOException;

import exceptions.ManagerSaveException;
import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.io.BufferedReader;
import java.io.FileReader;

public class FileBackedTasksManager extends InMemoryTaskManager {
    public FileBackedTasksManager() {
        super();
    }

    File file = new File("resources" + File.separator + "data.csv");

    private void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,startTime,duration,endTime,epic\n");
            for (Task task : taskMap.values()) {
                writer.write(task.toStringForFile() + "\n");
            }
            for (Epic epic : epicMap.values()) {
                writer.write(epic.toStringForFile() + "\n");
            }
            for (SubTask subTask : subTaskMap.values()) {
                writer.write(subTask.toStringForFile() + "\n");
            }
            writer.write("\n");
            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        }
    }

    private Task fromString(String value) {
        String[] taskPart = value.split(",");
        if (taskPart[1].equals(Tasks.TASK.name())) {
            if (taskPart[5].equals("null")) {
                return new Task(taskPart[2], taskPart[4], Status.valueOf(taskPart[3]), Integer.parseInt(taskPart[0]));
            } else {
                return new Task(taskPart[2], taskPart[4], Status.valueOf(taskPart[3]), Integer.parseInt(taskPart[0]),
                        taskPart[5], Integer.parseInt(taskPart[6]));
            }
        } else if (taskPart[1].equals(Tasks.EPIC.name())) {
            return new Epic(taskPart[2], taskPart[4], Integer.parseInt(taskPart[0]));
        } else {
            if (taskPart[5].equals("null")) {
                return new SubTask(taskPart[2], taskPart[4], Status.valueOf(taskPart[3]), Integer.parseInt(taskPart[8]),
                        Integer.parseInt(taskPart[0]));
            } else {
                return new SubTask(taskPart[2], taskPart[4], Status.valueOf(taskPart[3]), Integer.parseInt(taskPart[8]),
                        Integer.parseInt(taskPart[0]), taskPart[5], Integer.parseInt(taskPart[6]));
            }

        }
    }

    private static String historyToString(HistoryManager manager) {
        StringJoiner history = new StringJoiner(",");
        for (Task task : manager.getHistory()) {
            history.add(Integer.toString(task.getId()));
        }
        return history.toString();
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> historyId = new ArrayList<>();
        if (value != null) {
            for (String id : value.split(",")) {
                historyId.add(Integer.parseInt(id));
            }
        }
        return historyId;
    }

    public static FileBackedTasksManager loadFromFile(File newFile) {
        FileBackedTasksManager newManager = new FileBackedTasksManager();
        newManager.file = newFile;
        try (BufferedReader reader = new BufferedReader(new FileReader(newFile))) {
            reader.readLine();
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.equals("")) {
                    line = reader.readLine();
                    for (Integer historyId : historyFromString(line)) {
                        newManager.addToHistory(newManager.getSomeTaskById(historyId));
                    }
                } else {
                    Task unknownTask = newManager.fromString(line);
                    Tasks taskType = unknownTask.getType();
                    if (newManager.freeId <= unknownTask.getId()){
                        newManager.freeId = unknownTask.getId() + 1;
                    }
                    if (Tasks.TASK.equals(taskType)) {
                        if (newManager.addToTasksByPriority(unknownTask)){
                            newManager.taskMap.put(unknownTask.getId(), unknownTask);
                            newManager.addToTasksByPriority(unknownTask);
                        }
                    } else if (Tasks.EPIC.equals(taskType)) {
                        newManager.epicMap.put(unknownTask.getId(), (Epic) unknownTask);
                    } else {
                        if (newManager.addToTasksByPriority(unknownTask)){
                            newManager.subTaskMap.put(unknownTask.getId(), (SubTask) unknownTask);
                            SubTask subTask = (SubTask) unknownTask;
                            newManager.addToTasksByPriority(subTask);
                            newManager.epicMap.get(subTask.getEpicId()).addSubTaskId(subTask.getId());
                            newManager.defineEpicStatus(subTask.getEpicId());
                            newManager.defineEpicTime(subTask, newManager.epicMap.get(subTask.getEpicId()));
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        }
        return newManager;
    }

    public Task getSomeTaskById(int id) {
        if (taskMap.containsKey(id)) {
            return taskMap.get(id);
        } else if (epicMap.containsKey(id)) {
            return epicMap.get(id);
        } else return subTaskMap.getOrDefault(id, null);
    }


    @Override
    public void deleteAllSimpleTasks() {
        super.deleteAllSimpleTasks();
        save();
    }

    @Override
    public void deleteAllEpicMap() {
        super.deleteAllEpicMap();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    void addToHistory(Task task) {
        super.addToHistory(task);
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteSubTaskById(Integer subTaskId) {
        super.deleteSubTaskById(subTaskId);
        save();
    }

    public static void main(String[] args) {

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();

        Task task = new Task("Task1", "create first task", Status.NEW);
        fileBackedTasksManager.createOneTask(task);
        task = new Task("Task2", "create second task", Status.DONE);
        fileBackedTasksManager.createOneTask(task);

        Epic epic = new Epic("Epic1", "create first epic");
        fileBackedTasksManager.createOneTask(epic);
        int epicId = fileBackedTasksManager.getEpicId(epic);
        SubTask subTask = new SubTask("Subtask1", "create first task", Status.DONE, epicId);
        fileBackedTasksManager.createOneTask(subTask);

        fileBackedTasksManager.getEpic(3);
        fileBackedTasksManager.getSubTask(4);

        epic = new Epic("Epic2", "create 2 epic");
        fileBackedTasksManager.createOneTask(epic);
        epicId = fileBackedTasksManager.getEpicId(epic);
        subTask = new SubTask("Subtask2", "create 2 task", Status.NEW, epicId);
        fileBackedTasksManager.createOneTask(subTask);
        subTask = new SubTask("Subtask3", "create 3 task", Status.DONE, epicId);
        fileBackedTasksManager.createOneTask(subTask);

        printAll(fileBackedTasksManager);

        var newFileBacked = FileBackedTasksManager.loadFromFile(new File("resources" + File.separator + "data.csv"));
        printAll(newFileBacked);
    }

    public static void printAll(InMemoryTaskManager inMemoryTaskManager) {
        System.out.println(inMemoryTaskManager.getAllTasks());
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println(inMemoryTaskManager.getAllSubTasks() + "\n");

        System.out.println(inMemoryTaskManager.getHistory() + "\n\n");

        System.out.println(inMemoryTaskManager.getTasksByPriority());
    }

}
