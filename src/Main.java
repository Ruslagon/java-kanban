import model.Status;
import model.Task;
import model.Epic;
import model.SubTask;
import service.HistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;
import service.Managers;

public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = new Managers().getDefault();

        Task task = new Task("Task1", "create first task", Status.NEW);
        inMemoryTaskManager.createOneTask(task);
        task = new Task("Task2", "create second task", Status.DONE);
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
        subTask = new SubTask("Subtask2", "create second task", Status.IN_PROGRESS, epicId);
        inMemoryTaskManager.createOneTask(subTask);

        epic = new Epic("Epic2", "create second epic");
        inMemoryTaskManager.createOneTask(epic);
        epicId = inMemoryTaskManager.getEpicId(epic);
        subTask = new SubTask("Subtask3", "create third epic", Status.NEW, epicId);
        inMemoryTaskManager.createOneTask(subTask);

        printAllMaps(inMemoryTaskManager);

        inMemoryTaskManager.updateOneTask(new Task("Task1.1", "recreate first task", Status.IN_PROGRESS, 1));
        inMemoryTaskManager.updateOneTask(new Epic("Epic2.1", "recreate second epic", 6));
        inMemoryTaskManager.updateOneTask(new SubTask("Subtask2.1", "recreate second task", Status.DONE, 3, 5));

        printAllMaps(inMemoryTaskManager);

        System.out.println(inMemoryTaskManager.getHistory());

        inMemoryTaskManager.deleteById(1);
        inMemoryTaskManager.deleteById(3);
        inMemoryTaskManager.deleteById(7);

        printAllMaps(inMemoryTaskManager);

        inMemoryTaskManager.deleteAllTasks();

        printAllMaps(inMemoryTaskManager);

        inMemoryTaskManager.deleteAllSubTasks();


    }

    public static void printAllMaps(TaskManager inMemoryTaskManager){
        System.out.println(inMemoryTaskManager.getAllTasks());
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println(inMemoryTaskManager.getAllSubTasks() + "\n\n");
    }
}
