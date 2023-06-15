package test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private final Gson gson = new Gson();
    HttpTaskServer taskServer;
    HttpClient client;
    Task task1;
    Task task2;
    Task task3;
    Epic epic1;
    Epic epic2;
    Epic epic3;
    Epic epic4;
    Epic epic5;
    SubTask subTask1;
    SubTask subTask2;
    SubTask subTask3;
    SubTask subTask4;
    @BeforeEach
    void beforeEach() throws IOException, InterruptedException {
        taskServer = new HttpTaskServer();
        client = HttpClient.newHttpClient();
        deleteAll();
        task1 = new Task("Test addNewTask", "Test addNewTask description", Status.NEW); //id = 1;//132
        task2 = new Task("Task2", "create second task", Status.DONE,"06.06.23 02:42",20);//id = 2
        task3 = new Task("Task3", "create 3 task", Status.DONE,"05.06.23 02:42",20);//id = 3
        epic1 = new Epic("Epic1", "create first epic");//id = 4
        epic2 = new Epic("Epic2", "create 2 epic");  //id = 5
        epic3 = new Epic("Epic3", "create 3 epic");  //id = 6
        epic4 = new Epic("Epic4", "create 4 epic");  //id = 7
        epic5 = new Epic("Epic5", "create 5 epic");  //id = 8
        subTask1 = new SubTask("subTask1", "new subtask1", Status.NEW, 1);
        subTask2 = new SubTask("subTask2", "new subtask2", Status.NEW, 2,"19.10.23 12:30",190);
        subTask3 = new SubTask("subTask3", "new subtask3", Status.IN_PROGRESS, 2,"29.10.23 12:30",190);
        subTask4 = new SubTask("subTask4", "new subtask4", Status.DONE, 3,"19.11.23 12:30",190);
    }

    @AfterEach
    void afterEach() throws IOException, InterruptedException {
        deleteAll();
        taskServer.stop();
    }

    @Test
    void task() throws IOException, InterruptedException {
        //POST
        URI url = URI.create("http://localhost:8080/tasks/task/");


        Task task4 = null;
        String json = gson.toJson(task4);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405,response.statusCode(),"task не должен быть принят");

        json = gson.toJson(task1);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(),"task должен быть принят");

        task1.setId(1);
        task1.setName("taskUpdate");
        json = gson.toJson(task1);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(),"task должен быть обновлен");

        task1.setName(null);
        json = gson.toJson(task1);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405,response.statusCode(),"task не должен быть принят");

        json = gson.toJson(task2);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        json = gson.toJson(task3);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //GET
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(3,JsonParser.parseString(response.body()).getAsJsonArray().size(),"на сервере всего 3 таска");
        System.out.println(response.body());

        url = URI.create("http://localhost:8080/tasks/task/?id=dw1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405,response.statusCode(),"введенный id нельзя считывать");

        url = URI.create("http://localhost:8080/tasks/task/?id=3");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        task3.setId(3);
        assertEquals(task3,gson.fromJson(response.body(),Task.class),"task должен совпасть");

        //DELETE
        url = URI.create("http://localhost:8080/tasks/task/?Id=3");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(),"task должен быть удален");

        url = URI.create("http://localhost:8080/tasks/task/?Iваd=123ыва3");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405,response.statusCode(),"id невозможно считать");

        url = URI.create("http://localhost:8080/tasks/task/");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(),"все таски удалены");
    }

    @Test
    void subtask() throws IOException, InterruptedException {
        //groundwork
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(epic2);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(epic3);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        //POST
        url = URI.create("http://localhost:8080/tasks/subtask/");

        SubTask subTask5 = null;
        json = gson.toJson(subTask5);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405,response.statusCode(),"subtask не должен быть принят");

        json = gson.toJson(subTask1);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(),"subtask должен быть принят");

        subTask1.setId(4);
        subTask1.setName("taskUpdate");
        json = gson.toJson(subTask1);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(),"subTask должен быть обновлен");

        subTask1.setName(null);
        json = gson.toJson(subTask1);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405,response.statusCode(),"subTask не должен быть принят");

        json = gson.toJson(subTask2);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        json = gson.toJson(subTask3);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        json = gson.toJson(subTask4);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //GET
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(4,JsonParser.parseString(response.body()).getAsJsonArray().size(),"на сервере всего 4 сабтаска");
        System.out.println(response.body());

        url = URI.create("http://localhost:8080/tasks/subtask/?id=dw1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405,response.statusCode(),"введенный id нельзя считывать");

        url = URI.create("http://localhost:8080/tasks/subtask/?id=5");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        subTask2.setId(5);
        assertEquals(subTask2,gson.fromJson(response.body(),SubTask.class),"subTask должен совпасть");

        url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=2");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(2,JsonParser.parseString(response.body()).getAsJsonArray().size(),"у epic2 должно быть 2 subTask");

        //DELETE
        url = URI.create("http://localhost:8080/tasks/subtask/?Id=4");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(),"subtask должен быть удален");

        url = URI.create("http://localhost:8080/tasks/subtask/?Iваd=123ыва3");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405,response.statusCode(),"id невозможно считать");

        url = URI.create("http://localhost:8080/tasks/subtask/");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(),"все сабтаски удалены");
    }

    @Test
    void epic() throws IOException, InterruptedException {
        //POST
        URI url = URI.create("http://localhost:8080/tasks/epic/");

        SubTask epic6 = null;
        String json = gson.toJson(epic6);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405,response.statusCode(),"epic не должен быть принят");

        json = gson.toJson(epic1);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(),"epic должен быть принят");

        epic1.setId(1);
        epic1.setName("taskUpdate");
        json = gson.toJson(epic1);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(),"epic должен быть обновлен");

        epic1.setName(null);
        json = gson.toJson(epic1);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405,response.statusCode(),"epic не должен быть принят");

        json = gson.toJson(epic2);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        json = gson.toJson(epic3);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        json = gson.toJson(epic4);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //GET
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(4,JsonParser.parseString(response.body()).getAsJsonArray().size(),"на сервере всего 4 эпика");
        System.out.println(response.body());

        url = URI.create("http://localhost:8080/tasks/epic/?id=dw1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405,response.statusCode(),"введенный id нельзя считывать");

        url = URI.create("http://localhost:8080/tasks/epic/?id=4");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        epic4.setId(5);
        epic4.setStatus(Status.NEW);
        assertEquals(epic4,gson.fromJson(response.body(),Epic.class),"epic должен совпасть");


        //DELETE
        url = URI.create("http://localhost:8080/tasks/epic/?Id=4");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(),"epic должен быть удален");

        url = URI.create("http://localhost:8080/tasks/epic/?Iваd=123ыва3");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405,response.statusCode(),"id невозможно считать");

        url = URI.create("http://localhost:8080/tasks/epic/");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(),"все epic удалены");
    }

    @Test
    void history() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic1);//1
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(epic2);//2
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(epic3);//3
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(subTask1);//4
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        json = gson.toJson(subTask2);//5
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        json = gson.toJson(subTask3);//6
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        json = gson.toJson(subTask4);//7
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/task/");
        json = gson.toJson(task1);//8
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        json = gson.toJson(task2);//9
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        json = gson.toJson(task3);//10
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/epic/?id=3");//hist 3
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/?id=4");//hist 3 4
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/?id=6");//hist 3 4 6
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/task/?id=8");//hist 3 4 6 8
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/task/?id=10");//hist 3 4 6 8 10
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/history/");//hist 3 4 6 8 10
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(5,JsonParser.parseString(response.body()).getAsJsonArray().size(),"ошибка истории");

    }

    @Test
    void priority() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic1);//1
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(epic2);//2
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(epic3);//3
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(subTask1);//4
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        json = gson.toJson(subTask2);//5
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        json = gson.toJson(subTask3);//6
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        json = gson.toJson(subTask4);//7
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/task/");
        json = gson.toJson(task1);//8
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        json = gson.toJson(task2);//9
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        json = gson.toJson(task3);//10
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/prioritized_tasks/");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request,HttpResponse.BodyHandlers.ofString());
        List<Integer> list = new ArrayList<>();
        for (JsonElement jsonElement : JsonParser.parseString(response.body()).getAsJsonArray()) {
            list.add(jsonElement.getAsJsonObject().get("id").getAsInt());
        }
        Integer[] expected = new Integer[]{4, 8, 10, 9, 5, 6, 7};
        ArrayList<Integer> expectedList = new ArrayList<>();
        Collections.addAll(expectedList, expected);
        System.out.println(list);

        assertIterableEquals(expectedList, list,"ошибка определения приоритета");

    }

    void deleteAll() throws IOException, InterruptedException {
        URI uri1 = URI.create("http://localhost:8080/tasks/task/");
        URI uri2 = URI.create("http://localhost:8080/tasks/epic/");

        HttpRequest request = HttpRequest.newBuilder().uri(uri1).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(uri2).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

}