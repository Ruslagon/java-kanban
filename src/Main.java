import model.Task;
import model.Epic;
import model.SubTask;
import service.Manager;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task = new Task("Task1", "create first task", "NEW");
        manager.createOneTask(task);
        task = new Task("Task2", "create second task", "DONE");
        manager.createOneTask(task);

        Epic epic = new Epic("Epic1", "create first epic");
        manager.createOneTask(epic);
        int epicId = manager.getEpicId(epic);
        SubTask subTask = new SubTask("Subtask1", "create first task", "DONE", epicId);
        manager.createOneTask(subTask);
        subTask = new SubTask("Subtask2", "create second task", "IN_PROGRESS", epicId);
        manager.createOneTask(subTask);

        epic = new Epic("Epic2", "create second epic");
        manager.createOneTask(epic);
        epicId = manager.getEpicId(epic);
        subTask = new SubTask("Subtask3", "create third epic", "NEW", epicId);
        manager.createOneTask(subTask);

        printAllMaps(manager);

        manager.updateOneTask(new Task("Task1.1", "recreate first task", "IN_PROGRESS", 1));
        manager.updateOneTask(new Epic("Epic2.1", "recreate second epic", 6));
        manager.updateOneTask(new SubTask("Subtask2.1", "recreate second task", "DONE", 3, 5));

        printAllMaps(manager);

        manager.deleteById(1);
        manager.deleteById(3);
        manager.deleteById(7);

        printAllMaps(manager);

        manager.deleteAllTasks();

        printAllMaps(manager);

        manager.deleteAllSubTasks();
    }

    public static void printAllMaps(Manager manager){
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks() + "\n\n");
    }
}
