package service;

import java.io.IOException;
import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.io.BufferedReader;
import java.io.FileReader;

public class FileBackedTasksManager extends InMemoryTaskManager{
    public FileBackedTasksManager() {
        super();
    }
    File file = new File("resources" + File.separator + "data.csv");
    private void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write(decodeText("id,type,name,status,description,epic\n"));
            for (Task task : taskMap.values()) {
                writer.write(decodeText(task.toStringForFile() + "\n"));
            }
            for (Epic epic : epicMap.values()) {
                writer.write(decodeText(epic.toStringForFile() + "\n"));
            }
            for (SubTask subTask : subTaskMap.values()) {
                writer.write(decodeText(subTask.toStringForFile() + "\n"));
            }
            writer.write(decodeText("\n"));
            writer.write(decodeText(historyToString(historyManager)));
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время записи файла.");
        }
    }

    private static String decodeText(String input) {
        return new String(input.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }

    private Task fromString(String value){
        String[] taskPart = value.split(",");
        if (taskPart[1].equals(Tasks.TASK.name())){
            return new Task(taskPart[2],taskPart[4], Status.valueOf(taskPart[3]), Integer.parseInt(taskPart[0]));
        } else if (taskPart[1].equals(Tasks.EPIC.name())) {
            return new Epic(taskPart[2],taskPart[4], Integer.parseInt(taskPart[0]));
        } else {
            return new SubTask(taskPart[2],taskPart[4], Status.valueOf(taskPart[3]), Integer.parseInt(taskPart[5]),
                    Integer.parseInt(taskPart[0]));
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
        for (String id : value.split(",")) {
            historyId.add(Integer.parseInt(id));
        }
        return historyId;

    }

    static FileBackedTasksManager loadFromFile(File newFile) {
        FileBackedTasksManager newManager = new FileBackedTasksManager();
        newManager.file = newFile;
        try (BufferedReader reader = new BufferedReader(new FileReader(newFile))){
            reader.readLine();
            while (reader.ready()) {
                String line = decodeText(reader.readLine());
                if (line.equals("")){
                    line = decodeText(reader.readLine());
                    for (Integer historyId : historyFromString(line)) {
                        newManager.addToHistory(newManager.getSomeTaskById(historyId));
                    }
                } else {
                    Task unknownTask = newManager.fromString(line);
                    String taskType = unknownTask.getClass().getSimpleName().toUpperCase();
                    if (Tasks.TASK.name().equals(taskType)){
                        newManager.taskMap.put(unknownTask.getId(),unknownTask);
                    } else if (Tasks.EPIC.name().equals(taskType)){
                        newManager.epicMap.put(unknownTask.getId(),(Epic) unknownTask);
                    } else {
                        newManager.subTaskMap.put(unknownTask.getId(), (SubTask) unknownTask);
                        SubTask subTask = (SubTask) unknownTask;
                        newManager.epicMap.get(subTask.getEpicId()).addSubTaskId(subTask.getId());
                        newManager.defineEpicStatus(subTask.getEpicId());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
        }
        return newManager;
    }

    public Task getSomeTaskById(int id){
        if (taskMap.containsKey(id)) {
            return taskMap.get(id);
        } else if (epicMap.containsKey(id)){
            return epicMap.get(id);
        } else {
            return subTaskMap.get(id);
        }
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
    void addToHistory(Task task){
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
    public void createSubTask(SubTask subTask){
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
    public void deleteTaskById(int id){
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

    public static void printAll(TaskManager inMemoryTaskManager){
        System.out.println(inMemoryTaskManager.getAllTasks());
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println(inMemoryTaskManager.getAllSubTasks() + "\n");

        System.out.println(inMemoryTaskManager.getHistory() + "\n\n");
    }

}
