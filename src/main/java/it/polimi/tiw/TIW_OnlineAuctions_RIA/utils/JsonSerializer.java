package it.polimi.tiw.TIW_OnlineAuctions_RIA.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class JsonSerializer {
    private static final Gson gson = new GsonBuilder()
    .registerTypeAdapter(
            Instant.class,
            (com.google.gson.JsonSerializer<Instant>) (instant, type, jsonSerializationContext) ->
                    new JsonPrimitive(instant.atZone(ZoneId.of("Europe/Rome")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
    )
    .create();

    public static Gson getInstance() {
        return gson;
    }
}
