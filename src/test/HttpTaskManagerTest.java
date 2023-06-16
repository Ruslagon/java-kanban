package test;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.KVServer;
import service.Managers;
import service.TaskManager;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest {

    @BeforeAll
    static void beforeAll() throws IOException {
        new KVServer().start();
    }
    @Override
    @BeforeEach
    void beforeEach() throws IOException, InterruptedException {
        try {
            taskManager = Managers.httpTaskManager();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
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
    void loadFromServer() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createEpic(epic3);
        taskManager.createTask(task1);
        taskManager.createTask(task3);
        task2 = new Task("Task2", "create second task", Status.DONE,"06.06.23 02:42",20);//id = 2
        task3 = new Task("Task3", "create 3 task", Status.DONE,"05.06.23 02:42",20);//id = 3
        taskManager.createSubTask(new SubTask("Task2", "create second task", Status.NEW,2,"09.06.23 02:42",20));
        taskManager.createSubTask(new SubTask("Task2", "create second task", Status.IN_PROGRESS,3,"16.06.23 02:42",20));
        taskManager.createSubTask(new SubTask("Task2", "create second task", Status.DONE,3,"20.06.23 02:42",20));
        TaskManager taskManager2;
        try {
            taskManager2 = Managers.httpTaskManager();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertIterableEquals(taskManager.getAllTasks(),taskManager2.getAllTasks(),"Tasks не совпадают");
        assertIterableEquals(taskManager.getAllSubTasks(),taskManager2.getAllSubTasks(),"SubTasks не совпадают");
        assertIterableEquals(taskManager.getAllEpics(),taskManager2.getAllEpics(),"Epics не совпадают");
        assertIterableEquals(taskManager.getTasksByPriority(),taskManager2.getTasksByPriority(),"Tasks по приоритету не совпадают");
        assertIterableEquals(taskManager.getHistory(),taskManager2.getHistory(),"истории не совпадают");


    }
}