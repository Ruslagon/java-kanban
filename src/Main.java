import model.Task;
import model.Epic;
import model.SubTask;
import service.Manager;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task = new Task("Task1", "create first task", "NEW");
        manager.setOneTask(task);
        task = new Task("Task2", "create second task", "DONE");
        manager.setOneTask(task);

        Epic epic = new Epic("Epic1", "create first epic", "NEW");
        manager.setOneTask(epic);
        int epicId = manager.getEpicId(epic);
        SubTask subTask = new SubTask("Subtask1", "create first task", "DONE", epicId);
        manager.setOneTask(subTask);
        subTask = new SubTask("Subtask2", "create second task", "IN_PROGRESS", epicId);
        manager.setOneTask(subTask);

        epic = new Epic("Epic2", "create second epic", "IN_PROGRESS");
        manager.setOneTask(epic);
        epicId = manager.getEpicId(epic);
        subTask = new SubTask("Subtask3", "create third epic", "NEW", epicId);
        manager.setOneTask(subTask);

        printAllMaps(manager);

        manager.updateOneTask(1,new Task("Task1.1", "recreate first task", "IN_PROGRESS"));
        manager.updateOneTask(6, new Epic("Epic2.1", "recreate second epic", "DONE"));
        manager.updateOneTask(5, new SubTask("Subtask2.1", "recreate second task", "DONE", 3));

        printAllMaps(manager);

        manager.deleteById(1);
        manager.deleteById(3);
        manager.deleteById(7);

        printAllMaps(manager);

        manager.clearAllMap();

        printAllMaps(manager);
    }

    public static void printAllMaps(Manager manager){
        System.out.println(manager.getTaskMap());
        System.out.println(manager.getEpicMap());
        System.out.println(manager.getSubTaskMap() + "\n\n");
    }
}
