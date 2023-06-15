package service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;

    private final Gson gson;

    FileBackedTasksManager fileBackedTaskManager;
    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        fileBackedTaskManager = FileBackedTasksManager.loadFromFile(new File("resources" + File.separator + "HttpFileTaskManager.csv"));
        gson = new Gson();
        server.createContext("/tasks/task", this::task);
        server.createContext("/tasks/subtask", this::subtask);
        server.createContext("/tasks/epic", this::epic);
        server.createContext("/tasks/history",this::history);
        server.createContext("/tasks/prioritized_tasks",this::priority);
        server.start();
    }

    public void stop(){
        server.stop(0);
    }

    private void task(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/task/task");
            String possibleId = h.getRequestURI().toString().replaceFirst("/tasks/task/", "");
            String response;
            int id;
            if ("GET".equals(h.getRequestMethod())) {
                if (possibleId.isEmpty()) {
                    response = gson.toJson(fileBackedTaskManager.getAllTasks());
                    sendText(h, response);
                    System.out.println("Успешное возвращение tasks");
                } else {
                    possibleId = possibleId.split("=")[1];
                    id = getId(possibleId);
                    if (id == -1) {
                        System.out.println("id был введен неверно, получено: " + possibleId);
                        h.sendResponseHeaders(405, 0);
                    } else {
                        System.out.println(fileBackedTaskManager.getTask(id));
                        response = gson.toJson(fileBackedTaskManager.getTask(id));
                        sendText(h, response);
                        System.out.println("Успешное возвращение task");
                    }
                }
            } else if ("DELETE".equals(h.getRequestMethod())) {
                if (possibleId.isEmpty()) {
                    fileBackedTaskManager.deleteAllSimpleTasks();
                    System.out.println("Успешное удаление tasks");
                    h.sendResponseHeaders(200, 0);
                } else {
                    possibleId = possibleId.split("=")[1];
                    id = getId(possibleId);
                    if (id == -1) {
                        System.out.println("id был введен неверно, получено: " + possibleId);
                        h.sendResponseHeaders(405, 0);
                    } else {
                        fileBackedTaskManager.deleteTaskById(id);
                        System.out.println("Успешное удаление task");
                        h.sendResponseHeaders(200, 0);
                    }
                }
            } else if ("POST".equals(h.getRequestMethod())) {
                try {
                    String body = readText(h);
                    Task task = gson.fromJson(body, Task.class);
                    System.out.println(task);
                    if (task.getStatus() == null || task.getName() == null || task.getDescription() == null) {
                        System.out.println("Введены не все обязательные поля");
                        h.sendResponseHeaders(405, 0);
                    } else if (task.getId() != null) {
                        fileBackedTaskManager.updateTask(task);
                        System.out.println("Успешное обновление task");
                        h.sendResponseHeaders(200, 0);
                    } else {
                        fileBackedTaskManager.createTask(task);
                        System.out.println("Успешное добавление task");
                        h.sendResponseHeaders(200, 0);
                    }
                } catch (Exception e) {
                    System.out.println("Task введен неверно");
                    h.sendResponseHeaders(405, 0);
                }
            } else {
                System.out.println("/tasks/task ждёт GET, POST, DELETE - запросы, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void subtask(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/task/subtask");
            String possibleId = h.getRequestURI().toString().replaceFirst("/tasks/subtask/", "");
            String response;
            int id;
            if ("GET".equals(h.getRequestMethod())) {
                if (possibleId.isEmpty()) {
                    response = gson.toJson(fileBackedTaskManager.getAllSubTasks());
                    sendText(h, response);
                    System.out.println("Успешное возвращение subtasks");
                } else {
                    boolean containsEpic = possibleId.contains("epic");
                    possibleId = possibleId.split("=")[1];
                    id = getId(possibleId);
                    if (id == -1) {
                        System.out.println("id был введен неверно, получено: " + possibleId);
                        h.sendResponseHeaders(405, 0);
                    } else if (containsEpic) {
                        response = gson.toJson(fileBackedTaskManager.getEpicsSubTasks(id));
                        sendText(h, response);
                        System.out.println("Успешное возвращение subtasks");
                    } else {
                        response = gson.toJson(fileBackedTaskManager.getSubTask(id));
                        sendText(h, response);
                        System.out.println("Успешное возвращение subtask");
                    }
                }
            } else if ("DELETE".equals(h.getRequestMethod())) {
                if (possibleId.isEmpty()) {
                    fileBackedTaskManager.deleteAllSubTasks();
                    System.out.println("Успешное удаление subtasks");
                    h.sendResponseHeaders(200, 0);
                } else {
                    possibleId = possibleId.split("=")[1];
                    id = getId(possibleId);
                    if (id == -1) {
                        System.out.println("id был введен неверно, получено: " + possibleId);
                        h.sendResponseHeaders(405, 0);
                    } else {
                        fileBackedTaskManager.deleteSubTaskById(id);
                        System.out.println("Успешное удаление subtask");
                        h.sendResponseHeaders(200, 0);
                    }
                }
            } else if ("POST".equals(h.getRequestMethod())) {
                try {
                    SubTask subtask = gson.fromJson(readText(h), SubTask.class);
                    if (subtask.getStatus() == null || subtask.getName() == null || subtask.getDescription() == null ||
                     subtask.getEpicId() == null) {
                        System.out.println("Введены не все обязательные поля");
                        h.sendResponseHeaders(405, 0);
                    } else if (subtask.getId() != null) {
                        fileBackedTaskManager.updateSubTask(subtask);
                        System.out.println("Успешное обновление subtask");
                        h.sendResponseHeaders(200, 0);
                    } else {
                        fileBackedTaskManager.createSubTask(subtask);
                        System.out.println("Успешное добавление subtask");
                        h.sendResponseHeaders(200, 0);
                    }
                } catch (Exception e) {
                    System.out.println("Task введен неверно");
                    h.sendResponseHeaders(405, 0);
                }
            } else {
                System.out.println("/tasks/subtask ждёт GET, POST, DELETE - запросы, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void epic(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/task/epic");
            String possibleId = h.getRequestURI().toString().replaceFirst("/tasks/epic/", "");
            String response;
            int id;
            if ("GET".equals(h.getRequestMethod())) {
                if (possibleId.isEmpty()) {
                    response = gson.toJson(fileBackedTaskManager.getAllEpics());
                    sendText(h, response);
                    System.out.println("Успешное возвращение epics");
                } else {
                    possibleId = possibleId.split("=")[1];
                    id = getId(possibleId);
                    if (id == -1) {
                        System.out.println("id был введен неверно, получено: " + possibleId);
                        h.sendResponseHeaders(405, 0);
                    } else {
                        response = gson.toJson(fileBackedTaskManager.getEpic(id));
                        sendText(h, response);
                        System.out.println("Успешное возвращение epic");
                    }
                }
            } else if ("DELETE".equals(h.getRequestMethod())) {
                if (possibleId.isEmpty()) {
                    fileBackedTaskManager.deleteAllEpicMap();
                    System.out.println("Успешное удаление epics");
                    h.sendResponseHeaders(200, 0);
                } else {
                    possibleId = possibleId.split("=")[1];
                    id = getId(possibleId);
                    if (id == -1) {
                        System.out.println("id был введен неверно, получено: " + possibleId);
                        h.sendResponseHeaders(405, 0);
                    } else {
                        fileBackedTaskManager.deleteEpicById(id);
                        System.out.println("Успешное удаление epic");
                        h.sendResponseHeaders(200, 0);
                    }
                }
            } else if ("POST".equals(h.getRequestMethod())) {
                try {
                    Epic epic = gson.fromJson(readText(h), Epic.class);
                    if (epic.getName() == null || epic.getDescription() == null) {
                        System.out.println("Введены не все обязательные поля");
                        h.sendResponseHeaders(405, 0);
                    } else if (epic.getId() != null) {
                        fileBackedTaskManager.updateEpic(epic);
                        System.out.println("Успешное обновление epic");
                        h.sendResponseHeaders(200, 0);
                    } else {
                        fileBackedTaskManager.createEpic(epic);
                        System.out.println("Успешное добавление epic");
                        h.sendResponseHeaders(200, 0);
                    }
                } catch (Exception e) {
                    System.out.println("Task введен неверно");
                    h.sendResponseHeaders(405, 0);
                }
            } else {
                System.out.println("/tasks/epic ждёт GET, POST, DELETE - запросы, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void history(HttpExchange h) throws IOException {
        String response;
        List<Task> list = fileBackedTaskManager.getHistory();
        try {
            if ("GET".equals(h.getRequestMethod())) {
                response = gson.toJson(list);
                sendText(h, response);
            } else {
                System.out.println("/tasks/history ждёт GET-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            System.out.println("ошибка вне предположений");
            h.sendResponseHeaders(500, 0);
        } finally {
            h.close();
        }
    }

    private void priority(HttpExchange h) throws IOException {
        String response;
        List<Task> list = fileBackedTaskManager.getTasksByPriority();
        try {
            if ("GET".equals(h.getRequestMethod())) {
                response = gson.toJson(list);
                sendText(h, response);
            } else {
                System.out.println("/tasks/prioritized_tasks ждёт GET-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            System.out.println("ошибка вне предположений");
            h.sendResponseHeaders(500, 0);
        } finally {
            h.close();
        }
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes());
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    private int getId(String strId){
        try {
            return (int)Integer.parseInt(strId);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

}
