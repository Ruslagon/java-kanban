package test;

import model.Epic;
import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import service.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest {

    @Override
    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
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
}