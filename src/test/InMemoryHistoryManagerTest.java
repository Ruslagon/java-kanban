package test;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.InMemoryHistoryManager;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;
    Task task1;
    Task task2;
    Task task3;
    Task taskNull;
    @BeforeEach
    void beforeEach() {
        task1 = new Task("Task1", "create first task", Status.NEW, 1);
        task2 = new Task("Task2", "create second task", Status.DONE,2,"06.06.23 02:42",20);
        task3 = new Task("Task3", "create 3 task", Status.IN_PROGRESS,3,"04.06.23 02:42",300);
        taskNull = null;

        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void getHistory() {
        historyManager.add(task1);
        historyManager.add(task2);

        assertIterableEquals(List.of(task1,task2),historyManager.getHistory(), "История имеет порядок");

    }

    @Test
    void add() {
        historyManager.add(taskNull);
        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не сохраняет пустые значения");

        historyManager.add(task1);
        historyManager.add(task2);

        history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История не пустая.");

        historyManager.add(task1);

        assertEquals(2, history.size(), "Элемент не дублируется в истории");
    }

    @Test
    void remove() {
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(1);

        assertEquals(1, historyManager.getHistory().size(), "в истории остался только один элемент.");

        historyManager.remove(4);

        assertEquals(1, historyManager.getHistory().size(), "на удалении был не существующий элемент.");

        historyManager.add(task1);
        historyManager.add(task3);

        historyManager.remove(1);
        assertIterableEquals(List.of(task2,task3), historyManager.getHistory(), "должен убрать значение из середины.");
        historyManager.remove(3);
        assertIterableEquals(List.of(task2), historyManager.getHistory(), "должен убрать значение c конца.");

    }
}