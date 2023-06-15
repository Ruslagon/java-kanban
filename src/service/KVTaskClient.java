package service;

import com.google.gson.Gson;
import model.Status;
import model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String apiToken;
    HttpClient client = HttpClient.newHttpClient();
    URI url;
    HttpRequest.BodyPublisher body;
    HttpResponse<String> response;
    URI uri;

    HttpRequest request;
    public KVTaskClient(URI url) throws IOException, InterruptedException {
        this.url = url;
        uri = URI.create(url + "register");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        apiToken = response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        body = HttpRequest.BodyPublishers.ofString(json);
        uri = URI.create(url.toString() + "save/" + key + "?API_TOKEN=" + apiToken);
        request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String load(String key) throws IOException, InterruptedException {
        uri = URI.create(url.toString() + "load/" + key + "?API_TOKEN=" + apiToken);
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request,HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
