package kkrasilnikovv.preprocessor;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import javafx.beans.property.SimpleIntegerProperty;

import java.lang.reflect.Type;

public class SimpleIntegerPropertyAdapter implements JsonSerializer<SimpleIntegerProperty>, JsonDeserializer<SimpleIntegerProperty> {
    @Override
    public JsonElement serialize(SimpleIntegerProperty src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.get());
    }

    @Override
    public SimpleIntegerProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return new SimpleIntegerProperty(json.getAsInt());
    }
}
