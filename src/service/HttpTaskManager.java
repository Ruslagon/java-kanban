package service;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.gson.Gson;
import exceptions.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    URI irl;
    KVTaskClient client;
    Gson gson = new Gson();
    public HttpTaskManager() throws IOException, InterruptedException {
        irl = URI.create("http://localhost:8078/");
        client = new KVTaskClient(irl);
        loadFromServer();
    }

    public HttpTaskManager(URI url) throws IOException, InterruptedException {
        this.irl = url;
        client = new KVTaskClient(irl);
        loadFromServer();
    }

    @Override
    void save() {
        try {
            client.put("tasks", gson.toJson(taskMap.values()));
            client.put("subtasks", gson.toJson(subTaskMap.values()));
            client.put("epics", gson.toJson(epicMap.values()));
            client.put("history", gson.toJson(getHistory().stream()
                    .map(Task::getId).collect(Collectors.toList())));
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void loadFromServer() throws IOException, InterruptedException {
        try {
            String tasksJson = client.load("tasks");
            if (!tasksJson.equals("")){
                var tasksJsonArray = JsonParser.parseString(tasksJson).getAsJsonArray();
                for (JsonElement jsonElement : tasksJsonArray) {
                    Task task = gson.fromJson(jsonElement,Task.class);
                    if (freeId <= task.getId()){
                        freeId = task.getId() + 1;
                    }
                    addToTasksByPriority(task);
                    taskMap.put(task.getId(), task);
                }
            }
            String subtasksJson = client.load("subtasks");
            if (!subtasksJson.equals("")){
                var subtasksJsonArray = JsonParser.parseString(subtasksJson).getAsJsonArray();
                for (JsonElement jsonElement : subtasksJsonArray) {
                    SubTask subTask = gson.fromJson(jsonElement,SubTask.class);
                    if (freeId <= subTask.getId()){
                        freeId = subTask.getId() + 1;
                    }
                    addToTasksByPriority(subTask);
                    subTaskMap.put(subTask.getId(), subTask);
                }
            }
            String epicsJson = client.load("epics");
            if (!epicsJson.equals("")){
                var epicsJsonArray = JsonParser.parseString(epicsJson).getAsJsonArray();
                for (JsonElement jsonElement : epicsJsonArray) {
                    Epic epic = gson.fromJson(jsonElement,Epic.class);
                    if (freeId <= epic.getId()){
                        freeId = epic.getId() + 1;
                    }
                    epicMap.put(epic.getId(), epic);
                }
            }
            String historyJson = client.load("history");
            if (!historyJson.equals("")){
                var historyJsonArray = JsonParser.parseString(historyJson).getAsJsonArray();
                for (JsonElement jsonElement : historyJsonArray) {
                    int id = jsonElement.getAsInt();
                    addToHistory(getSomeTaskById(id));
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка чтения данных с сервера");
        }
    }
}
