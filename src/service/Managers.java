package service;

import java.io.File;
import java.io.IOException;

public class Managers {
    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTaskManager() {
        return new FileBackedTasksManager();
    }

    public static TaskManager getFileBackedTaskManagerWithFileName(File file) {
        return FileBackedTasksManager.loadFromFile(file);
    }

    public static TaskManager HttpTaskManager() throws IOException, InterruptedException {
        return new HttpTaskManager();
    }
}
