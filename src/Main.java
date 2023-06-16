import model.Status;
import model.Task;
import model.Epic;
import model.SubTask;
import service.*;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        new KVServer().start();
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();

        Task task = new Task("Task1", "create first task", Status.NEW);
        inMemoryTaskManager.createOneTask(task);
        task = new Task("Task2", "create second task", Status.DONE,"06.06.23 02:42",20);
        inMemoryTaskManager.createOneTask(task);

        inMemoryTaskManager.getTask(1);
        System.out.println(inMemoryTaskManager.getHistory());
        inMemoryTaskManager.getTask(2);
        System.out.println(inMemoryTaskManager.getHistory());

        Epic epic = new Epic("Epic1", "create first epic");
        inMemoryTaskManager.createOneTask(epic);
        int epicId = inMemoryTaskManager.getEpicId(epic);
        SubTask subTask = new SubTask("Subtask1", "create first task", Status.DONE, epicId);
        inMemoryTaskManager.createOneTask(subTask);
        subTask = new SubTask("Subtask2", "create second task", Status.IN_PROGRESS, epicId,"07.06.23 02:52",20);
        inMemoryTaskManager.createOneTask(subTask);
        subTask = new SubTask("Subtask3", "create 3 task", Status.DONE, epicId,"08.06.23 02:42",20);
        inMemoryTaskManager.createOneTask(subTask);

        inMemoryTaskManager.getEpic(3);
        System.out.println(inMemoryTaskManager.getHistory());

        epic = new Epic("Epic2", "create second epic");
        inMemoryTaskManager.createOneTask(epic);
        epicId = inMemoryTaskManager.getEpicId(epic);
        subTask = new SubTask("Subtask3", "create third epic", Status.NEW, epicId);
        inMemoryTaskManager.createOneTask(subTask);

        printAllMaps(inMemoryTaskManager);
        inMemoryTaskManager.getEpic(7);
        System.out.println(inMemoryTaskManager.getHistory());

        inMemoryTaskManager.getSubTask(5);
        System.out.println(inMemoryTaskManager.getHistory());

        printAllMaps(inMemoryTaskManager);

        inMemoryTaskManager.updateOneTask(new Task("Task1.1", "recreate first task", Status.IN_PROGRESS, 1,"05.06.23 02:42",20));
        inMemoryTaskManager.updateOneTask(new Epic("Epic2.1", "recreate second epic", 6));
        inMemoryTaskManager.updateOneTask(new SubTask("Subtask2.1", "recreate second task", Status.DONE, 3, 5, "07.06.23 02:42",20));

        printAllMaps(inMemoryTaskManager);

        epic = new Epic("Epic3", "create 3 epic");
        inMemoryTaskManager.createOneTask(epic);
        epicId = inMemoryTaskManager.getEpicId(epic);
        inMemoryTaskManager.getEpic(9);

        inMemoryTaskManager.getSubTask(5);
        System.out.println(inMemoryTaskManager.getHistory());

        System.out.println(inMemoryTaskManager.getTasksByPriority() + "asd");

        inMemoryTaskManager.deleteById(9);
        inMemoryTaskManager.deleteById(3);
        System.out.println(inMemoryTaskManager.getHistory());

        printAllMaps(inMemoryTaskManager);

        inMemoryTaskManager.deleteById(1);
        inMemoryTaskManager.deleteById(3);
        inMemoryTaskManager.deleteById(7);

        printAllMaps(inMemoryTaskManager);

        inMemoryTaskManager.deleteAllTasks();

        printAllMaps(inMemoryTaskManager);

        inMemoryTaskManager.deleteAllSubTasks();

        TaskManager fileBackedTasksManager = Managers.httpTaskManager();

        task = new Task("Task1", "create first task", Status.NEW);

        fileBackedTasksManager.createTask(task);
        task = new Task("Task2", "create second task", Status.DONE);
        fileBackedTasksManager.createTask(task);

        epic = new Epic("Epic1", "create first epic");
        fileBackedTasksManager.createEpic(epic);
        epicId = 3;
        subTask = new SubTask("Subtask1", "create first task", Status.DONE, epicId);
        fileBackedTasksManager.createSubTask(subTask);

        fileBackedTasksManager.getEpic(3);
        fileBackedTasksManager.getSubTask(4);

        System.out.println(fileBackedTasksManager.getSubTask(10));

        System.out.println(fileBackedTasksManager.getTasksByPriority() + "asd");

        fileBackedTasksManager = new HttpTaskManager();

        System.out.println(fileBackedTasksManager.getTasksByPriority() + "asd");
    }



    public static void printAllMaps(TaskManager inMemoryTaskManager){
        System.out.println(inMemoryTaskManager.getAllTasks());
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println(inMemoryTaskManager.getAllSubTasks() + "\n\n");
    }
}
