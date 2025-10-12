package clock.examples;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Stream;

import static clock.util.Constants.AMERICA_CHICAGO;

public class WorldClockExample 
{
    private static Logger logger = LogManager.getLogger(WorldClockExample.class);

    private String timeZone = AMERICA_CHICAGO;

    public static void main(String[] args) {
        try
        {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://worldtimeapi.org/api/timezone/"))
                    .version(HttpClient.Version.HTTP_2)
                    .headers("Content-Type","text/html")
                    .GET()
                    .build();
            HttpResponse<Stream<String>> responseList = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofLines());
            List<String> zones = responseList.body().toList();

            int size = 0;
            if (args.length != 0) {
                size = Math.min(Integer.parseInt(args[0]), zones.size());
                if (size != zones.size()) {
                    zones = zones.subList(0, size);
                }
            }
            zones.forEach(timezone -> {
                try {
                    HttpRequest request2 = HttpRequest.newBuilder().uri(URI.create("https://worldtimeapi.org/api/timezone/" + timezone))
                            .version(HttpClient.Version.HTTP_2)
                            .headers("Content-Type","text/html")
                            .GET()
                            .build();
                    HttpResponse<String> response = HttpClient.newBuilder()
                            .build()
                            .send(request2, HttpResponse.BodyHandlers.ofString());
                    logger.info(response);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonElement jsonElement = new JsonParser().parse(response.body());
                    String json = gson.toJson(jsonElement);
                    logger.info("body -> {}", json);
                }
                catch (Exception e)
                { logger.error("Something happened when retrieving timezone info for {}" + timezone, e); }
            });


        }
        catch (Exception e)
        { logger.error("Something happened when connecting to worldtimeapi.org"); }
    }
}