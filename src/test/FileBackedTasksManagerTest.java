package test;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTasksManager;
import service.InMemoryTaskManager;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest {
    @Override
    @BeforeEach
    void beforeEach() {
        taskManager = FileBackedTasksManager.loadFromFile(new File("resources" + File.separator + "testFile.csv"));
        taskManager.deleteAllTasks();
        task1 = new Task("Test addNewTask", "Test addNewTask description", Status.NEW); //id = 1;
        task2 = new Task("Task2", "create second task", Status.DONE,"06.06.23 02:42",20);//id = 2
        task3 = new Task("Task3", "create 3 task", Status.DONE,"05.06.23 02:42",20);//id = 3
        epic1 = new Epic("Epic1", "create first epic");//id = 4
        epic2 = new Epic("Epic2", "create 2 epic");  //id = 5
        epic3 = new Epic("Epic3", "create 3 epic");  //id = 6
        epic4 = new Epic("Epic4", "create 4 epic");  //id = 7
        epic5 = new Epic("Epic5", "create 5 epic");  //id = 8
    }

    @Test
    void loadFromFile() {
        taskManager = FileBackedTasksManager.loadFromFile(new File("resources" + File.separator + "emptyFile.csv"));

        assertEquals(0,taskManager.getAllTasks().size(),"Файл должен быть пустым");
        assertEquals(0,taskManager.getAllEpics().size(),"Файл должен быть пустым");
        assertEquals(0,taskManager.getAllSubTasks().size(),"Файл должен быть пустым");
        assertEquals(0,taskManager.getHistory().size(),"Файл должен быть пустым");

        taskManager = FileBackedTasksManager.loadFromFile(new File("resources" + File.separator + "epicWithNoHistory.csv"));

        epic1 = new Epic("Epic1", "create first epic",1);

        assertEquals(epic1, taskManager.getEpic(1), "ошибка вызова Epic");
        taskManager.deleteAllTasks();
        taskManager.createEpic(epic1);

        assertEquals(0,taskManager.getHistory().size(), "история не пустая");

        taskManager = FileBackedTasksManager.loadFromFile(new File("resources" + File.separator + "testFile.csv"));
        taskManager.deleteAllTasks();

        Task task = new Task("Task1", "create first task", Status.NEW);
        taskManager.createTask(task);
        task = new Task("Task2", "create second task", Status.DONE);
        taskManager.createTask(task);

        Epic epic = new Epic("Epic1", "create first epic");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Subtask1", "create first task", Status.DONE, 3);
        taskManager.createSubTask(subTask);

        taskManager.getEpic(3);
        taskManager.getSubTask(4);

        epic = new Epic("Epic2", "create 2 epic");
        taskManager.createEpic(epic);
        subTask = new SubTask("Subtask2", "create 2 task", Status.NEW, 5);
        taskManager.createSubTask(subTask);
        subTask = new SubTask("Subtask3", "create 3 task", Status.DONE, 5);
        taskManager.createSubTask(subTask);

        assertEquals(2, taskManager.getHistory().size(),"ошибка истории");
    }
}