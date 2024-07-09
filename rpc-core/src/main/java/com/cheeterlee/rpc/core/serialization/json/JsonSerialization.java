package com.cheeterlee.rpc.core.serialization.json;

import com.cheeterlee.rpc.core.exception.SerializeException;
import com.cheeterlee.rpc.core.serialization.Serialization;
import com.google.gson.*;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class JsonSerialization implements Serialization {

    static class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {
        @SneakyThrows
        @Override
        public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String name = json.getAsString();
            return Class.forName(name);
        }

        @Override
        public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
            // class -> json
            return new JsonPrimitive(src.getName());
        }
    }

    @Override
    public <T> byte[] serialize(T object) {
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
            String json = gson.toJson(object);
            return json.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SerializeException("Json serialize failed.", e);
        }
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
            String json = new String(bytes, StandardCharsets.UTF_8);
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            throw new SerializeException("Json deserialize failed.", e);
        }
    }
}
