package org.example.clock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WorldClockExample {

    public static void main(String[] args) {
        try
        {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://worldtimeapi.org/api/timezone/america/chicago"))
                    .version(HttpClient.Version.HTTP_2)
                    .headers("Content-Type","application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement jsonElement = new JsonParser().parse(response.body());
            String json = gson.toJson(jsonElement);
            System.out.println(json);
        }
        catch (Exception e)
        {
            System.err.println("Could not connect to worldtimeapi.org/api/");
        }
    }
}
