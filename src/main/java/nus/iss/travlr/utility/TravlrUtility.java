package nus.iss.travlr.utility;

import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import nus.iss.travlr.model.Itinerary;

public class TravlrUtility {
    public static JsonArray itiListTJsonArray(List<Itinerary> itineraryList) {
    JsonArrayBuilder jab = Json.createArrayBuilder();
    // iterate list, create a json array of objects
    for (Itinerary iti : itineraryList) {
        jab.add(iti.toJsonObject());
    }
    JsonArray jarr = jab.build();
    return jarr;
    }
}
