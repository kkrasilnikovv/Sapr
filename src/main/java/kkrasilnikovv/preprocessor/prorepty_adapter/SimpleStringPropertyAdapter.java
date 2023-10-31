package kkrasilnikovv.preprocessor.prorepty_adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import javafx.beans.property.SimpleStringProperty;

import java.lang.reflect.Type;

public class SimpleStringPropertyAdapter implements JsonSerializer<SimpleStringProperty>, JsonDeserializer<SimpleStringProperty> {
    @Override
    public JsonElement serialize(SimpleStringProperty src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.get());
    }

    @Override
    public SimpleStringProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return new SimpleStringProperty(json.getAsString());
    }
}
