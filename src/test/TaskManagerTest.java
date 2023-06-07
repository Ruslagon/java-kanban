package test;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    T taskManager;
    Task task1;
    Task task2;
    Task task3;
    Epic epic1;
    Epic epic2;
    Epic epic3;
    Epic epic4;
    Epic epic5;

    void setTaskManager(T taskManagerForSet)
    {taskManager = taskManagerForSet;}
    @BeforeEach
    void beforeEach() {
        taskManager.deleteAllTasks();
        task1 = new Task("Test addNewTask", "Test addNewTask description", Status.NEW); //id = 1;
        task2 = new Task("Task2", "create second task", Status.DONE,"06.06.23 02:42",20);//id = 2
        task3 = new Task("Task3", "create 3 task", Status.DONE,"05.06.23 02:42",20);//id = 3
        epic1 = new Epic("Epic1", "create first epic");//id = 4
        epic2= new Epic("Epic2", "create 2 epic");  //id = 5
        epic3 = new Epic("Epic3", "create 3 epic");  //id = 6
        epic4 = new Epic("Epic4", "create 4 epic");  //id = 7
        epic5 = new Epic("Epic5", "create 5 epic");  //id = 8
    }



    @Test
    void createTask() {
        final int taskId = 1;
        taskManager.createTask(task1);//id = 1

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void defineEpicStatus() {
        taskManager.createEpic(epic1);//id1
        taskManager.createEpic(epic2);//id2
        taskManager.createEpic(epic3);//id3
        taskManager.createEpic(epic4);//id4
        taskManager.createEpic(epic5);//id5
        taskManager.createSubTask(new SubTask("Subtask2_1", "create 2 epic", Status.NEW, 2));
        taskManager.createSubTask(new SubTask("Subtask3_1", "create 3 epic", Status.DONE, 3));
        taskManager.createSubTask(new SubTask("Subtask4_1", "create 4 epic", Status.NEW, 4));
        taskManager.createSubTask(new SubTask("Subtask4_2", "create 4 epic", Status.DONE, 4));
        taskManager.createSubTask(new SubTask("Subtask5_1", "create 5 epic", Status.IN_PROGRESS, 5));

        assertEquals(Status.NEW, taskManager.getEpic(1).getStatus(),"ошибка определения статуса");
        assertEquals(Status.NEW, taskManager.getEpic(2).getStatus(),"ошибка определения статуса");
        assertEquals(Status.DONE, taskManager.getEpic(3).getStatus(),"ошибка определения статуса");
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(4).getStatus(),"ошибка определения статуса");
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(5).getStatus(),"ошибка определения статуса");
    }

    @Test
    void getAllTasks() {
        taskManager.createTask(task1);//id1
        taskManager.createTask(task2);//id2
        taskManager.createTask(task3);//id3

        assertEquals(3,taskManager.getAllTasks().size(), "Task должно быть 3");
    }

    @Test
    void getAllEpics() {
        taskManager.createEpic(epic1);//id1
        taskManager.createEpic(epic2);//id2
        taskManager.createEpic(epic3);//id3

        assertEquals(3,taskManager.getAllEpics().size(), "Epic должно быть 3");
    }

    @Test
    void getAllSubTasks() {
        taskManager.createEpic(epic1);//id=1

        taskManager.createSubTask(new SubTask("Subtask1", "create 1 epic", Status.NEW, 1));//id=2
        taskManager.createSubTask(new SubTask("Subtask2", "create 2 epic", Status.DONE, 1));//id=3

        assertEquals(2,taskManager.getAllSubTasks().size(), "Subtask должно быть 2");

    }

    @Test
    void deleteAllTasks() {
        taskManager.createEpic(epic1);//id=1

        taskManager.createSubTask(new SubTask("Subtask1", "create 1 epic", Status.NEW, 1));//id=2
        taskManager.createSubTask(new SubTask("Subtask2", "create 2 epic", Status.DONE, 2));//id=3

        taskManager.createTask(task1);//id4

        taskManager.deleteAllTasks();

        assertEquals(0,taskManager.getAllTasks().size(), "все Task должны быть удалены");
        assertEquals(0,taskManager.getAllEpics().size(), "все Task должны быть удалены");
        assertEquals(0,taskManager.getAllSubTasks().size(), "все Task должны быть удалены");
    }

    @Test
    void deleteAllSimpleTasks() {
        taskManager.createTask(task1);//id1
        taskManager.createTask(task2);//id2
        taskManager.createTask(task3);//id3

        taskManager.deleteAllSimpleTasks();

        assertNull(taskManager.getTask(1), "Такого Task не должно существовать");
        assertNull(taskManager.getTask(2), "Такого Task не должно существовать");
        assertNull(taskManager.getTask(3), "Такого Task не должно существовать");
    }

    @Test
    void deleteAllEpicMap() {
        taskManager.createEpic(epic1);//id1
        taskManager.createEpic(epic2);//id2
        taskManager.createEpic(epic3);//id3

        taskManager.deleteAllEpicMap();


        assertNull(taskManager.getEpic(1), "Такого Epic не должно существовать");
        assertNull(taskManager.getEpic(2), "Такого Epic не должно существовать");
        assertNull(taskManager.getEpic(3), "Такого Epic не должно существовать");

    }

    @Test
    void deleteAllSubTasks() {
        taskManager.createEpic(epic1);//id=1


        taskManager.createSubTask(new SubTask("Subtask1", "create 1 epic", Status.NEW, 1));//id=2
        taskManager.createSubTask(new SubTask("Subtask2", "create 2 epic", Status.DONE, 2));//id=3

        taskManager.createTask(task1);//id4

        taskManager.deleteAllSubTasks();

        assertNull(taskManager.getSubTask(2), "этот subTask удален");
        assertNull(taskManager.getSubTask(3), "этот subTask удален");
        assertNull(taskManager.getSubTask(4), "этого subTask не существует");
    }

    @Test
    void getTask() {
        taskManager.createTask(task1);//id1
        taskManager.createTask(task2);//id2
        taskManager.createTask(task3);//id3

        assertEquals(task1,taskManager.getTask(1),"ошибка в вызове Task");
        assertEquals(task2,taskManager.getTask(2),"ошибка в вызове Task");
        assertEquals(task3,taskManager.getTask(3),"ошибка в вызове Task");

        assertNull(taskManager.getTask(4), "Такого Task не должно существовать");


    }

    @Test
    void getEpic() {
        taskManager.createEpic(epic1);//id1
        taskManager.createEpic(epic2);//id2
        taskManager.createEpic(epic3);//id3

        assertEquals(epic1,taskManager.getEpic(1),"ошибка в вызове Epic");
        assertEquals(epic2,taskManager.getEpic(2),"ошибка в вызове Epic");
        assertEquals(epic3,taskManager.getEpic(3),"ошибка в вызове Epic");

        assertNull(taskManager.getEpic(4), "Такого Epic не должно существовать");
    }

    @Test
    void getSubTask() {
        taskManager.createEpic(epic1);//id=1

        var subTask1 = new SubTask("Subtask1", "create 1 epic", Status.NEW, 1);//id=2
        taskManager.createSubTask(subTask1);//id=2
        taskManager.createSubTask(new SubTask("Subtask2", "create 2 epic", Status.DONE, 2));//id=3

        assertEquals(subTask1, taskManager.getSubTask(2), "Ошибка получения SubTask");

        assertNull(taskManager.getSubTask(4), "Такого SubTask не должно существовать");
    }

    @Test
    void getEpicsSubTasks() {
        assertEquals(0,taskManager.getEpicsSubTasks(epic1).size(),"Список должен быть пуст");

        taskManager.createEpic(epic1);//id=1


        taskManager.createSubTask(new SubTask("Subtask1", "create 1 epic", Status.NEW, 1));//id=2
        taskManager.createSubTask(new SubTask("Subtask2", "create 2 epic", Status.DONE, 2));//id=3
        taskManager.createSubTask(null);//id=4

        assertEquals(1,taskManager.getEpicsSubTasks(epic1).size(),"Список должен быть иметь 2 элемента");
    }

    @Test
    void createEpic() {
        taskManager.createEpic(epic1);//id1
        taskManager.createEpic(epic2);//id2
        taskManager.createEpic(epic3);//id3
        taskManager.createEpic(null);//id4

        assertEquals(epic1, taskManager.getEpic(1),"ошибка в создании epic");
        assertEquals(epic3, taskManager.getEpic(3), "ошибка в создании epic");
        assertNull(taskManager.getEpic(4));
    }

    @Test
    void createSubTask() {
        taskManager.createEpic(epic1);//id=1
        taskManager.createSubTask(new SubTask("Subtask1", "create 1 epic", Status.NEW, 1));//id=2

        assertNotNull(taskManager.getSubTask(2),"ошибка создания subtask");

        taskManager.createSubTask(new SubTask("Subtask2", "create 2 epic", Status.NEW, 2));//id=3

        assertNull(taskManager.getSubTask(3), "SubTask не должен быть создан, т.к. имеет не правильный EpicId");
    }

    @Test
    void updateTask() {
        taskManager.createTask(task1);//id1
        taskManager.createTask(task2);//id2
        task3 = new Task("Task3", "create 3 task", Status.DONE,2,"06.06.23 02:42",20);

        taskManager.updateTask(task3);

        assertEquals(task3,taskManager.getTask(2), "ошибка обновления Task");
    }

    @Test
    void updateEpic() {
        taskManager.createEpic(epic1);//id1
        taskManager.createEpic(epic2);//id2
        epic3 = new Epic("epic3", "create 3",2);

        taskManager.updateEpic(epic3);

        assertEquals(epic3,taskManager.getEpic(2), "ошибка обновления Epic");
    }

    @Test
    void updateSubTask() {
        taskManager.createEpic(epic1);//id=1

        SubTask subTask1 = new SubTask("Subtask1", "create 1 epic", Status.NEW, 1,2);
        taskManager.createSubTask(new SubTask("Subtask2", "create 2 epic", Status.DONE, 1));//id=2

        taskManager.updateSubTask(subTask1);

        assertEquals(subTask1,taskManager.getSubTask(2),"ошибка обновления SubTask");
    }

    @Test
    void deleteTaskById() {
        taskManager.createTask(task1);//id1
        taskManager.createTask(task2);//id2
        taskManager.createTask(task3);//id3

        taskManager.deleteTaskById(1);

        assertNull(taskManager.getTask(1), "Task удален");
    }

    @Test
    void deleteEpicById() {
        taskManager.createEpic(epic1);//id1
        taskManager.createEpic(epic2);//id2
        taskManager.createEpic(epic3);//id3

        taskManager.deleteEpicById(2);

        assertNull(taskManager.getEpic(2), "Epic удален");
    }

    @Test
    void deleteSubTaskById() {
        taskManager.createEpic(epic1);//id=1

        SubTask subTask1 = new SubTask("Subtask1", "create 1 epic", Status.NEW, 1);//id2
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(new SubTask("Subtask2", "create 2 epic", Status.DONE, 1));//id=3

        taskManager.deleteSubTaskById(2);

        assertNull(taskManager.getSubTask(2), "SubTask удален");
    }

    @Test
    void getTasksByPriority() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        assertEquals(3, taskManager.getTasksByPriority().size(), "не верное количество элементов");

        ArrayList<Task> expected = new ArrayList<>(List.of(new Task[]{task1, task3, task2}));



        assertIterableEquals(expected, taskManager.getTasksByPriority());
    }
}