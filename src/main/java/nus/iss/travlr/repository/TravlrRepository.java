package nus.iss.travlr.repository;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import nus.iss.travlr.model.Itinerary;

@Repository
public class TravlrRepository {
    @Autowired @Qualifier("redisTemplate")
    private RedisTemplate<String, String> template;

    public void addItinerary(String userName, Itinerary itinerary) {

        // get current itinerary
        Optional<List<Itinerary>> optItineraryList = this.getItinerary(userName);
        // if empty, create a new one
        if (optItineraryList.isEmpty()) {

            JsonArray jarr = Json
                .createArrayBuilder()
                .add(itinerary.toJsonObject())
                .build();
            System.out.println("Packed JsonArray: " + jarr.toString());
            template.opsForHash().put("main", userName, jarr.toString());
        }
        else {

            List<Itinerary> itineraryList = optItineraryList.get();
            itineraryList.add(itinerary);

            JsonArrayBuilder jab = Json.createArrayBuilder();
            // iterate list, create a json array of objects
            for (Itinerary iti : itineraryList) {
                jab.add(iti.toJsonObject());
            }
            JsonArray jarr = jab.build();

            template.opsForHash().put("main", userName, jarr.toString());
        }
    }

    public Optional<List<Itinerary>> getItinerary(String userName) {
        String retrievedItinerary = (String) template.opsForHash().get("main", userName);
        if (retrievedItinerary == null) {
            return Optional.empty();
        }
        JsonReader jr = Json.createReader(new StringReader(retrievedItinerary));
        JsonArray itineraryArray = jr.readArray();
        List<Itinerary> itineraryList = new ArrayList<>();
        for (JsonValue val : itineraryArray) {
            JsonObject itineraryObject = val.asJsonObject();
            String name = itineraryObject.getString("name");
            String country = itineraryObject.getString("country");
            String description = itineraryObject.getString("description");
            itineraryList.add(new Itinerary(name, country, description));
        }
        return Optional.of(itineraryList);
    }
}
