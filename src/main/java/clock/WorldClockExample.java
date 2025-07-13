package clock;

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

import static clock.contract.ClockConstants.AMERICA_CHICAGO;

public class WorldClockExample 
{
    static { System.setProperty("appName", WorldClockExample.class.getSimpleName()); }
    private static Logger logger = LogManager.getLogger(WorldClockExample.class);
    public static void main(String[] args) {
        try
        {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://worldtimeapi.org/api/timezone/"+AMERICA_CHICAGO))
                    .version(HttpClient.Version.HTTP_2)
                    .headers("Content-Type","application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            logger.info(response);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement jsonElement = new JsonParser().parse(response.body());
            String json = gson.toJson(jsonElement);
            logger.info("body -> {}", json);
        }
        catch (Exception e)
        { logger.error("Something happened when connecting to worldtimeapi.org"); }
    }
}