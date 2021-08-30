package it.polimi.tiw.TIW_OnlineAuctions_RIA.utils;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;

public class JsonSerializer {
    private static final Gson gson = new GsonBuilder()
    .registerTypeAdapter(
            Instant.class,
            (com.google.gson.JsonSerializer<Instant>) (instant, type, jsonSerializationContext) ->
                    new JsonPrimitive(instant.getEpochSecond())
    )
    .create();

    public static Gson getInstance() {
        return gson;
    }
}
